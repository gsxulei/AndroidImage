package com.cost.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.cost.plugin.visitor.CostClassVisitor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class CostTransform extends Transform
{
	@Override
	public String getName()
	{
		return "cost";
	}

	@Override
	public Set<QualifiedContent.ContentType> getInputTypes()
	{
		return TransformManager.CONTENT_CLASS;
	}

	@Override
	public Set<? super QualifiedContent.Scope> getScopes()
	{
		return TransformManager.SCOPE_FULL_PROJECT;
	}

	@Override
	public boolean isIncremental()
	{
		return false;
	}

	@Override
	public void transform(TransformInvocation invocation) throws TransformException, InterruptedException, IOException
	{
		super.transform(invocation);

		Collection<TransformInput> inputs=invocation.getInputs();
		for(TransformInput input:inputs)
		{
			classes(input.getDirectoryInputs(),invocation);
			jar(input.getJarInputs(),invocation);
		}
	}

	private void classes(Collection<DirectoryInput> inputs,TransformInvocation invocation) throws IOException
	{
		for(DirectoryInput input:inputs)
		{
			File src=input.getFile();

			Collection<File> files=FileUtils.listFiles(src,new SuffixFileFilter(".class",IOCase.INSENSITIVE),
					TrueFileFilter.INSTANCE);

			for(File file:files)
			{
				try
				{
					FileInputStream fis=new FileInputStream(file.getAbsoluteFile());
					byte[] byteCode=addCost(fis);
					fis.close();

					FileOutputStream fos=new FileOutputStream(file.getAbsoluteFile());
					fos.write(byteCode);
					fos.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			File next=invocation.getOutputProvider().getContentLocation(input.getName(),input.getContentTypes(),
					input.getScopes(),Format.DIRECTORY);

			FileUtils.copyDirectory(src,next);
		}
	}

	private void jar(Collection<JarInput> inputs,TransformInvocation invocation) throws IOException
	{
		File temp=invocation.getContext().getTemporaryDir();
		long now=System.currentTimeMillis();
		for(JarInput input:inputs)
		{
			File src=input.getFile();
			File outJar=new File(temp,now+"_"+src.getName());

			JarFile jarFile=new JarFile(src);
			Enumeration<JarEntry> enumeration=jarFile.entries();

			JarOutputStream jos=new JarOutputStream(new FileOutputStream(outJar));
			while(enumeration.hasMoreElements())
			{
				JarEntry jarEntry=enumeration.nextElement();
				String entryName=jarEntry.getName();
				ZipEntry zipEntry=new ZipEntry(entryName);

				InputStream is=jarFile.getInputStream(jarEntry);
				jos.putNextEntry(zipEntry);

				byte[] byteCode;
				if(entryName.endsWith(".class"))
				{
					byteCode=addCost(is);
				}
				else
				{
					byteCode=IOUtils.toByteArray(is);
				}
				jos.write(byteCode);
				is.close();
				jos.closeEntry();
			}
			jos.close();
			jarFile.close();

			File dest=invocation.getOutputProvider().getContentLocation(input.getName(),
					input.getContentTypes(),input.getScopes(),Format.JAR);
			FileUtils.copyFile(outJar,dest);
			if(outJar.delete())
			{
				System.err.println("删除->"+outJar.getName());
			}
		}
	}

	private byte[] addCost(InputStream fis) throws IOException
	{
		ClassReader reader=new ClassReader(fis);

		String className=reader.getClassName();
		if(CostPlugin.isExclude(className))
		{
			System.err.println("---exclude---"+className);
			return reader.b;
		}

		ClassWriter writer=new ClassWriter(reader,ClassWriter.COMPUTE_MAXS);
		ClassVisitor cv=new CostClassVisitor(Opcodes.ASM5,writer);
		reader.accept(cv,ClassReader.EXPAND_FRAMES);
		return writer.toByteArray();
	}
}