package io.ihankun.framework.captcha.v3.draw;


import io.ihankun.framework.captcha.v3.core.CaptchaUtil;

import java.util.Random;

/**
 * 数学表达式生成和计算
 *
 * @author hankun
 */
class Expression {
	private static final char PLUS = '+';
	private static final char MINUS = '-';
	private static final char MULTIPLY = '×';

	/**
	 * 执行表达式
	 *
	 * @param expr 表达式
	 * @return 结果，-1 为 expr 表达式不合法
	 */
	public static int eval(String expr) {
		char[] chars = expr.toCharArray();
		int length = expr.length();
		for (int i = 0; i < chars.length; i++) {
			char operator = chars[i];
			if (PLUS == operator || MINUS == operator || MULTIPLY == operator) {
				int num1 = findInt(expr, 0, i);
				int num2 = findInt(expr, i + 1, length);
				return eval(num1, operator, num2);
			}
		}
		return -1;
	}

	private static int eval(int num1, char operator, int num2) {
		switch (operator) {
			case PLUS:
				return num1 + num2;
			case MINUS:
				return num1 - num2;
			case MULTIPLY:
				return num1 * num2;
			default:
				return -1;
		}
	}

	/**
	 * 随机表达式
	 *
	 * @return 表达式
	 */
	public static String randomExpr(Random random) {
		char[] chars = new char[]{PLUS, MINUS, MULTIPLY};
		char operator = chars[random.nextInt(chars.length)];
		int num1;
		int num2;
		// 乘法减少数值
		if (MULTIPLY == operator) {
			num1 = CaptchaUtil.randNum(random, 1, 10);
			num2 = CaptchaUtil.randNum(random, 1, 10);
		} else {
			num1 = CaptchaUtil.randNum(random, 1, 20);
			num2 = CaptchaUtil.randNum(random, 1, 20);
		}
		// 保证减法的结果不会出现负数
		if (MINUS == operator && num2 > num1) {
			int num = num1;
			num1 = num2;
			num2 = num;
		}
		return String.valueOf(num1) + operator + num2;
	}

	private static int findInt(String expr, int start, int end) {
		return Integer.parseInt(expr.substring(start, end));
	}

}
