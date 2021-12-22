package com.cost.plugin.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CostClassVisitor extends ClassVisitor
{
	public CostClassVisitor(int api,ClassVisitor classVisitor)
	{
		super(api,classVisitor);
	}

	@Override
	public MethodVisitor visitMethod(int access,String name,String descriptor,String signature,String[] exceptions)
	{
		MethodVisitor mv=super.visitMethod(access,name,descriptor,signature,exceptions);
		mv=new CostMethodVisitor(Opcodes.ASM5,mv,access,name,descriptor);
		return mv;
	}
}