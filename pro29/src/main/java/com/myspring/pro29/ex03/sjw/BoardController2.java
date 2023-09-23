package com.myspring.pro29.ex03.sjw;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.myspring.pro29.ex01.MemberVO;


@RestController
@RequestMapping("/member2")
public class BoardController2 {
	static Logger logger = LoggerFactory.getLogger(BoardController2.class);
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<List<MemberVO>> listMember() {
		logger.info("listMembers 메서드 호출");
		List<MemberVO> list = new ArrayList<MemberVO>();
		for (int i = 0; i < 10; i++) {
			 MemberVO vo = new MemberVO();
			  vo.setId("hong"+i);
			  vo.setPwd("123"+i);
			  vo.setName("홍길동"+i);
			  vo.setEmail("hong"+i+"@test.com");
			  list.add(vo);
		}
		
		return new ResponseEntity(list,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{MemberID}", method = RequestMethod.GET)
	public ResponseEntity<MemberVO> findMember (@PathVariable("MemberID") Integer MemberID) {
		logger.info("findMember 메서드 호출");
		logger.info("@PathVariable(\"MemberID\") 메서드 호출 : "+MemberID);
		MemberVO vo = new MemberVO();
		  vo.setId("hong");
		  vo.setPwd("1234");
		  vo.setName("손흐민");
		  vo.setEmail("hong@test.com");
		return new ResponseEntity(vo,HttpStatus.OK);
	}	
	
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<String> addMember (@RequestBody MemberVO MemberVO) {
		ResponseEntity<String>  resEntity = null;
		try {
			logger.info("addMember 메서드 호출");
			logger.info(MemberVO.toString());
			resEntity =new ResponseEntity("ADD_SUCCEEDED",HttpStatus.OK);
		}catch(Exception e) {
			resEntity = new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		
		return resEntity;
	}	
	
	//수정하기
	@RequestMapping(value = "/{MemberID}", method = RequestMethod.PUT)
	public ResponseEntity<String> modMember (@PathVariable("MemberID") Integer MemberID, @RequestBody MemberVO MemberVO) {
		ResponseEntity<String>  resEntity = null;
		try {
			logger.info("modMember 메서드 호출");
			logger.info(MemberVO.toString());
			resEntity =new ResponseEntity("MOD_SUCCEEDED",HttpStatus.OK);
		}catch(Exception e) {
			resEntity = new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		
		return resEntity;
	}
	
	//삭제하기
	@RequestMapping(value = "/{MemberID}", method = RequestMethod.DELETE)
	public ResponseEntity<String> removeMember (@PathVariable("MemberID") Integer MemberID) {
		ResponseEntity<String>  resEntity = null;
		try {
			logger.info("removeMember 메서드 호출");
			logger.info(MemberID.toString());
			resEntity =new ResponseEntity("REMOVE_SUCCEEDED",HttpStatus.OK);
		}catch(Exception e) {
			resEntity = new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		
		return resEntity;
	}	

}
