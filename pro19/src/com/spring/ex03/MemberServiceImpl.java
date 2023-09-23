package com.spring.ex03;

// 현 재 클래스 : MemberServiceImpl -> A
public class MemberServiceImpl implements MemberService {
	// MemberDAO : 인터페이스(다른 파일) -> B
	// 결론, A 라는 파일 안에 다른 인스턴스 B가 포함되어 있다. 
	// DI, 느슨한 결합을 의미함.
	// 선언만 되었지, 실제 할당이 안되었음. 
	private MemberDAO memberDAO;

	// 할당(초기화) 하는 방법 2가지 
	// 1) 세터라는 메서드를 이용
	// 2) 생성자를 이용해서 
	public void setMemberDAO(MemberDAO memberDAO) {
		this.memberDAO = memberDAO;
	}

	@Override
	// listMembers 의 기능이, B 외부 파일의 기능을 사용하고 있음. 
	public void listMembers() {
		// 원래, 자기의 구현 기능을 추가해야하는데.
		// 샘플로, 외부의 다른 클래스의 기능을 구현하는 것으로 대체하고 있음. 
		memberDAO.listMembers();
	}
}
