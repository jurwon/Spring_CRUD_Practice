package com.spring.ex02;
// 
// 기본 디폴트 생성자, (= 매개변수가 없는 생성자)
public class PersonServiceImpl implements PersonService {
	private String name;
	private int age;

	// 디폴트 기본 생성자. 
	public PersonServiceImpl() {
		
	}
	
	// 생성자를 이용해서 초기화. 
	// <constructor-arg   value="이순신" />
	public PersonServiceImpl(String name) {
		this.name = name;
	}
// 매개변수가 2개 인 생성자
	public PersonServiceImpl(String name, int age) {
		this.name = name;
		this.age = age;
	}

	@Override
	public void sayHello() {
		System.out.println("이름: " + name);
		System.out.println("나이: " + age + "!!");
	}
}
