package com.spring.ex01;

// 인터페이스 : 보통은, 추상 메서드로 만 구성.
// 특정 jdk 이후로는 , 일반 메서드도 같이 구성함. 

public interface PersonService {
	// 추상 메서드가 sayHello : 
	// 구체적인 행동은 없음. 
	// 구현하는 클래스가 알아서, 재지정해서 사용해야함.
	public void sayHello();
}
