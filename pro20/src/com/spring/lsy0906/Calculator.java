package com.spring.lsy0906;

// �ֱ�� Ŭ����. 
public class Calculator {
	public void add(int x, int y) {
		int result=x+y;
		System.out.println("���:"+ result);
	}

	public void subtract(int x, int y) {
		int result=x - y;
		System.out.println("���:"+ result);
	}

	public void multiply(int x, int y) {
		int result=x * y;
		System.out.println("���:"+ result);
	}

	public void divide(int x, int y) {
		int result=x / y;
		System.out.println("���:"+ result);
	}
}