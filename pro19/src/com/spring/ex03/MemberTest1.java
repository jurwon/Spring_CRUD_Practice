package com.spring.ex03;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

public class MemberTest1 {
	public static void main(String[] args) {
		BeanFactory factory = new XmlBeanFactory(new FileSystemResource("member.xml"));
		MemberService service = (MemberService) factory.getBean("memberService");
		// 실행은 2가지로 보여드릴게요. 
		// 1) 단순 실행.
		// 2) 디버깅.
		service.listMembers();
	}
}
