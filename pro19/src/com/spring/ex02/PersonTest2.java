package com.spring.ex02;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

public class PersonTest2 {
	public static void main(String[] args) {
		BeanFactory factory = new XmlBeanFactory(new FileSystemResource("person.xml"));
		PersonService person1 = (PersonService) factory.getBean("personService1");
		person1.sayHello();
		System.out.println();
		// 변경해보기
		

		// 다형성. 
		PersonService person2 = (PersonService) factory.getBean("personService2");
		person2.sayHello();
		
		//위에 시스템에 등록해서 사용했다면, 
		// 원래 우리가 기존 방식. 
		PersonServiceImpl p1 = new PersonServiceImpl();
		PersonServiceImpl p2 = new PersonServiceImpl("이상용");
		PersonServiceImpl p3 = new PersonServiceImpl("이상용",20);

		p1.sayHello();
		p2.sayHello();
		p3.sayHello();
		
		
		
		
	}
}
