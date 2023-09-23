package com.spring.ex04;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class LazyTest {
	public static void main(String[] args) {
		ApplicationContext context = new FileSystemXmlApplicationContext("lazy.xml");
//		System.out.println("SecondBean 중간 기본 출력 추가");
		// 해당 빈을 이용시 , 할당이 되는 부분 코드 
		// 원래는 , 서버가 켜지면, 기본은 시스템상 해당 메모리에 로드 올라감 기본.
		// lazy init 이 기능을 사용하면, 서버 시작해도, 메모리가 로드가 안됨.
		// 대신 실제로 사용시, 메모리가 로드가 됨. 
		// 안쓰면, 시스템에 로드 안하고, 
		// 실제 사용시 해당 메모리 로드를 해서, 메모리 효율적으로 사용 가능.
		
		// 실제로 해당 인스턴스를 사용하는 순간. 
		context.getBean("secondBean");
		
		//원래, 개발자가 임의로 인스턴스화해서 사용한 예. 
		First f1 = new First();
		
	}
}


