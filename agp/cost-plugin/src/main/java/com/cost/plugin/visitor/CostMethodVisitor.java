package com.cost.plugin.visitor;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

public class CostMethodVisitor extends AdviceAdapter
{
	private int start;

	protected CostMethodVisitor(int api,MethodVisitor methodVisitor,int access,String name,String descriptor)
	{
		super(api,methodVisitor,access,name,descriptor);
	}

	@Override
	protected void onMethodEnter()
	{
		super.onMethodEnter();

		start=newLocal(Type.LONG_TYPE);
		invokeStatic(Type.getType("Ljava/lang/System;"),new Method("currentTimeMillis","()J"));
		storeLocal(start);
	}

	@Override
	protected void onMethodExit(int opcode)
	{
		super.onMethodExit(opcode);

		int end=newLocal(Type.LONG_TYPE);
		invokeStatic(Type.getType("Ljava/lang/System;"),new Method("currentTimeMillis","()J"));
		storeLocal(end);

		loadLocal(end);
		loadLocal(start);
		math(SUB,Type.LONG_TYPE);

		invokeStatic(Type.getType("Lcom/andhub/gradle/plugin/demo/utils/Cost;"),new Method("cost","(J)V"));
	}

	@Override
	public AnnotationVisitor visitAnnotation(String descriptor,boolean visible)
	{
		return super.visitAnnotation(descriptor,visible);
	}
}