package com.spring.lsy0906;

// 주기능 클래스. 
public class Calculator {
	public void add(int x, int y) {
		int result=x+y;
		System.out.println("결과:"+ result);
	}

	public void subtract(int x, int y) {
		int result=x - y;
		System.out.println("결과:"+ result);
	}

	public void multiply(int x, int y) {
		int result=x * y;
		System.out.println("결과:"+ result);
	}

	public void divide(int x, int y) {
		int result=x / y;
		System.out.println("결과:"+ result);
	}
}
