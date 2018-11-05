package com.x62.commons.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CalculatorUtils
{
	/**
	 * 计算算术表达式
	 *
	 * @param text 算术表达式字符串
	 * @return 结算结果
	 */
	public static String doCalculate(String text)
	{
		text=preProcess(text);
		List<String> arr=split(text);
		return compute(arr);
	}

	/**
	 * 分解表达式
	 *
	 * @param str 算术表达式字符串
	 * @return 分解结果
	 */
	public static List<String> split(String str)
	{
		List<String> list=new ArrayList<>();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<str.length();i++)
		{
			String temp=str.charAt(i)+"";
			if("()+*/".contains(temp))
			{
				if(sb.length()>0)
				{
					list.add(sb.toString());
					sb=new StringBuilder();
				}
				list.add(temp);
			}
			else if("-".equals(temp)&&!"()+-*/".contains(sb))
			{
				if(sb.length()>0)
				{
					list.add(sb.toString());
					sb=new StringBuilder();
				}
				list.add(temp);
			}
			else
			{
				sb.append(temp);
			}
		}
		if(sb.length()>0)
		{
			list.add(sb.toString());
		}

		return list;
	}

	/**
	 * 预处理<br/>
	 * 将×替换成*<br/>
	 * 将÷替换成/<br/>
	 * 将%替换成/100<br/>
	 * 去掉末尾操作符
	 *
	 * @param str 算术表达式字符串
	 * @return 预处理结果
	 */
	public static String preProcess(String str)
	{
		StringBuilder result=new StringBuilder();
		str=str.replaceAll("×","*");
		str=str.replaceAll("÷","/");
		str=str.replaceAll("%","/100");
		result.append(str);

		int len=result.length();
		if("*/+-".contains(result.substring(len-1)))
		{
			result.deleteCharAt(len-1);
		}
		return result.toString();
	}

	public static boolean hasOperator(List<String> arr,String operator)
	{
		boolean b=false;
		for(String s : arr)
		{
			if(operator.contains(s))
			{
				b=true;
				break;
			}
		}
		return b;
	}

	/**
	 * 核心计算：加减乘除
	 *
	 * @param arr      表达式拆分后的数组
	 * @param operator 操作符 "+-"或"/*"
	 * @return 计算结果
	 */
	public static List<String> core(List<String> arr,String operator)
	{
		List<String> temp=new ArrayList<>();
		temp.addAll(arr);

		while(hasOperator(temp,operator))
		{
			int len=temp.size();
			for(int i=0;i<len;i++)
			{
				if(operator.contains(temp.get(i)))
				{
					BigDecimal result=new BigDecimal("0");
					BigDecimal left=new BigDecimal(temp.get(i-1));
					BigDecimal right=new BigDecimal(temp.get(i+1));
					if("+".equals(temp.get(i)))
					{
						result=left.add(right);
					}
					else if("-".equals(temp.get(i)))
					{
						result=left.subtract(right);
					}
					else if("*".equals(temp.get(i)))
					{
						result=left.multiply(right);
					}
					else if("/".equals(temp.get(i)))
					{
						result=left.divide(right);
					}

					List<String> d=new ArrayList<>();
					if("*/".equals(operator)&&i>1)
					{
						d.addAll(temp.subList(0,i-1));
					}
					d.add(result.toString());
					d.addAll(temp.subList(i+2,len));
					temp.clear();
					temp.addAll(d);
					break;
				}
			}
		}
		return temp;
	}

	/**
	 * 获取小括号的位置
	 *
	 * @param arr 表达式拆分后的数组
	 * @return 小括号的位置,第一个值为左括号位置,第二个值为右括号位置
	 */
	public static int[] getParenthesesIndex(List<String> arr)
	{
		int[] indexs=new int[]{-1,-1};
		for(int i=0;i<arr.size();i++)
		{
			if("(".equals(arr.get(i)))
			{
				indexs[0]=i;
			}
			else if(")".equals(arr.get(i))&&indexs[1]==-1)
			{
				indexs[1]=i;
			}
		}
		return indexs;
	}

	/**
	 * 计算表达式
	 *
	 * @param arr 表达式拆分后的数组
	 * @return 计算结果
	 */
	public static String compute(List<String> arr)
	{
		List<String> temp=new ArrayList<>();
		temp.addAll(arr);

		String operator="()";
		while(hasOperator(temp,operator))
		{
			int[] indexs=getParenthesesIndex(temp);
			if(indexs[0]<0)
			{
				break;
			}
			List<String> result=temp.subList(indexs[0]+1,indexs[1]);
			result=core(result,"*/");
			result=core(result,"+-");

			List<String> d=new ArrayList<>();
			if(indexs[0]>1)
			{
				d.addAll(temp.subList(0,indexs[0]));
			}
			d.add(String.valueOf(result.get(0)));
			d.addAll(temp.subList(indexs[1]+1,temp.size()));
			temp.clear();
			temp.addAll(d);
		}

		temp=core(temp,"*/");
		temp=core(temp,"+-");
		return temp.get(0);
	}
}