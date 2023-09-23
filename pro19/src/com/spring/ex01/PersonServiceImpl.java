package com.spring.ex01;

// PersonServiceImpl : A 라는 클래스 . 
// PersonService : B 라는 인터페이스 
public class PersonServiceImpl implements PersonService {
	private String name;
	private int age;

	//<property name="name">
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void sayHello() {
		System.out.println("이름 : " + name);
		System.out.println("나이 : " + age);
	}
}
