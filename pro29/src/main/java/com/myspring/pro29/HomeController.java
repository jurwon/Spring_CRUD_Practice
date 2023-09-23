package com.myspring.pro29;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
// 기본 구조가 , 결과 뷰를 반환하는 로직
//오늘 배우는 RestController, 결과 반환을 뷰가 아니라 특정 문자열을 반환
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	/*
	*//**
	 * Simply selects the home view to render by returning its name.
	 */
	/*
	 * @RequestMapping(value = "/", method = RequestMethod.GET) public String
	 * home(Locale locale, Model model) {
	 * logger.info("Welcome home! The client locale is {}.", locale);
	 * 
	 * Date date = new Date(); DateFormat dateFormat =
	 * DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
	 * 
	 * String formattedDate = dateFormat.format(date);
	 * 
	 * model.addAttribute("serverTime", formattedDate );
	 * 
	 * return "home"; }
	 */
	
	
	  @RequestMapping(value = "/", method = RequestMethod.GET) public String
	  home(Locale locale, Model model) { return "JSONTest2"; }
	 
}
