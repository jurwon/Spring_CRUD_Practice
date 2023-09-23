package com.spring.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class AccountController  extends MultiActionController  {
	   //서비스 선언 & 초기화
	   private AccountService accService ; 
	   public void setAccService(AccountService accService){
	     this.accService = accService;
	   }	

	   //실제 동작 확인
	   public ModelAndView sendMoney(HttpServletRequest request, HttpServletResponse response) throws Exception {
	      ModelAndView mav=new ModelAndView();
	      accService.sendMoney();
	      mav.setViewName("result");
	      return mav;
	   }
}
