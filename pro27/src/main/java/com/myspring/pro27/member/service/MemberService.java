package com.myspring.pro27.member.service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.myspring.pro27.member.vo.MemberVO;

public interface MemberService {
	 public List listMembers() throws DataAccessException;
	 public int addMember(MemberVO memberVO) throws DataAccessException;
	 public int removeMember(String id) throws DataAccessException;
	 public MemberVO login(MemberVO memberVO) throws Exception;
	 
	//추가, 한 회원의 정보 가져오기 메서드 추가. : getOneMember
	public MemberVO getOneMember(String id) throws DataAccessException;
	// 추가. updateMember
	public int updateMember(MemberVO membeVO) throws DataAccessException;
}
