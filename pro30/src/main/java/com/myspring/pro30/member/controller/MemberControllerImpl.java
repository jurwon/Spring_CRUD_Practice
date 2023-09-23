package com.myspring.pro30.member.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myspring.pro30.member.service.MemberService;
import com.myspring.pro30.member.vo.MemberVO;

@Controller("memberController")
@EnableAspectJAutoProxy
public class MemberControllerImpl implements MemberController {
	// private static final Logger logger =
	// LoggerFactory.getLogger(MemberControllerImpl.class);
	@Autowired
	private MemberService memberService;
	@Autowired
	MemberVO memberVO;

	private static final String CURR_IMAGE_REPO_PATH = "c:\\spring\\image_repo";

	@RequestMapping(value = { "/main", "" }, method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		// logger.info("Welcome home! The client locale is {}.", locale);

		// 중복파일 피하기 위해 사용하는 네이밍 기법중 하나인데
		// 1) 시간 및 날짜 이용(현재),
		// 2) UUID, 특정 랜덤한 숫자 및 영문자를 생성해주는 도구를 이용하기도함.
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		// 단순 데이터만 뷰에 전달하는 구조만 잠시 보면 됨.
		model.addAttribute("serverTime", formattedDate);
		// 결과 뷰는 모델&뷰 형식이 아니라 단순 뷰 리졸버 해당 뷰로 이동함.
		// return "home";
		// 타일즈 사용
		return "main";
	}

	@Override
	@RequestMapping(value = "/member/listMembers.do", method = RequestMethod.GET)
	public ModelAndView listMembers(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// String viewName = getViewName(request);
		String viewName = (String) request.getAttribute("viewName");
		System.out.println("인터셉터 확인 viewName: " + viewName);
//		logger.info("viewName: "+ viewName);
//		logger.debug("viewName: "+ viewName);
		List membersList = memberService.listMembers();

		ModelAndView mav = new ModelAndView(viewName);
		mav.addObject("membersList", membersList);
		return mav;
	}

	@Override
	@RequestMapping(value = "/member/addMember.do", method = RequestMethod.POST)
	public ModelAndView addMember(@ModelAttribute("member") MemberVO member,
			MultipartHttpServletRequest multipartRequest, HttpServletResponse response) throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");

		// 일반데이터 + 파일데이터 set담을 공간
		Map<String, Object> memberMap = new HashMap<String, Object>();

		// 일반데이터
		Enumeration enu = multipartRequest.getParameterNames(); // 반복
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement(); // key(name, id, pwd, email)
			String value = multipartRequest.getParameter(name); // value(이순신, m2. 1234,eee@naver.com)
			System.out.println("아이디 : " + name + ", value : " + value);
			memberMap.put(name, value);
		}

		// 이미지 파일 이름 반환과정
		String imageFileName = upload(multipartRequest);

		try {
			// 일반데이터+ 파일이미지 이름
			// 실제 이미지 파일을 -> 물리 저장소에 저장하는 로직.
			// 이미지 파일 첨부 했다. 즉, 널도 아니고, 길이가 0도 아니다.
			if (imageFileName != null && imageFileName.length() != 0) {
				File srcFile = new File(CURR_IMAGE_REPO_PATH + "\\" + "thumbnail" + "\\" + imageFileName);
				File destDir = new File(CURR_IMAGE_REPO_PATH + "\\" + imageFileName);
				FileUtils.moveFileToDirectory(srcFile, destDir, true);
			}

		} catch (Exception e) {
			// 오류 발생시 임시 저장소를 삭제
			File srcFile = new File(CURR_IMAGE_REPO_PATH + "\\" + "thumbnail" + "\\" + imageFileName);
			srcFile.delete();
			// 메세지 글쓰기 작성 오류
			e.printStackTrace();
		}

		memberMap.put("image", imageFileName);

		member.setImage(imageFileName); // memverVO에 이미지 이름 추가
		int result = 0;
		result = memberService.addMember(member);
		
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("map", memberMap);
		mav.setViewName("redirect:/member/listMembers.do");
		return mav;
	}

	// 미디어 저장소, 이미지 파일 올리기. 단일이미지
	private String upload(MultipartHttpServletRequest multipartRequest) throws Exception {
		String imageFileName = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();

		while (fileNames.hasNext()) {
			String fileName = fileNames.next();
			MultipartFile mFile = multipartRequest.getFile(fileName);
			imageFileName = mFile.getOriginalFilename();
			File file = new File(CURR_IMAGE_REPO_PATH + "\\" + fileName);
			if (mFile.getSize() != 0) { 
				if (!file.exists()) { 
					if (file.getParentFile().mkdirs()) { 
						file.createNewFile(); 
					}
				}
				mFile.transferTo(new File(CURR_IMAGE_REPO_PATH + "\\" + "thumbnail" + "\\" + imageFileName)); 
			}
		}
		return imageFileName;
	}

	// 미디어 저장소, 이미지 파일 다운로드. 단일이미지
	@RequestMapping("/memberImage_download.do")
	protected void download(@RequestParam("image") String image,
			                 HttpServletResponse response)throws Exception {
		OutputStream out = response.getOutputStream();
		String downFile = CURR_IMAGE_REPO_PATH +"\\"+ image;
		File file = new File(downFile);

		response.setHeader("Cache-Control", "no-cache");
		response.addHeader("Content-disposition", "attachment; image=" + image);
		FileInputStream in = new FileInputStream(file);
		byte[] buffer = new byte[1024 * 8];
		while (true) {
			int count = in.read(buffer); 
			if (count == -1) 
				break;
			out.write(buffer, 0, count);
		}
		in.close();
		out.close();
	}
	
	
	@Override
	@RequestMapping(value = "/member/removeMember.do", method = RequestMethod.GET)
	public ModelAndView removeMember(@RequestParam("id") String id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		memberService.removeMember(id);
		ModelAndView mav = new ModelAndView("redirect:/member/listMembers.do");
		return mav;
	}
	/*
	 * @RequestMapping(value = { "/member/loginForm.do", "/member/memberForm.do" },
	 * method = RequestMethod.GET)
	 * 
	 * @RequestMapping(value = "/member/*Form.do", method = RequestMethod.GET)
	 * public ModelAndView form(HttpServletRequest request, HttpServletResponse
	 * response) throws Exception { String viewName = getViewName(request);
	 * ModelAndView mav = new ModelAndView(); mav.setViewName(viewName); return mav;
	 * }
	 */

	@Override
	@RequestMapping(value = "/member/login.do", method = RequestMethod.POST)
	public ModelAndView login(@ModelAttribute("member") MemberVO member, RedirectAttributes rAttr,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		memberVO = memberService.login(member);
		if (memberVO != null) {
			HttpSession session = request.getSession();
			session.setAttribute("member", memberVO);
			session.setAttribute("isLogOn", true);
			mav.setViewName("redirect:/member/listMembers.do");
		} else { // 로그인 실패
			rAttr.addAttribute("result", "loginFailed");
			mav.setViewName("redirect:/member/loginForm.do");
		}
		return mav;
	}

	@Override
	@RequestMapping(value = "/member/logout.do", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		session.removeAttribute("member");
		session.removeAttribute("isLogOn");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/member/listMembers.do");
		return mav;
	}

	@RequestMapping(value = "/member/*Form.do", method = RequestMethod.GET)
	private ModelAndView form(@RequestParam(value = "result", required = false) String result,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = getViewName(request);
		/* String viewName = (String)request.getAttribute("viewName"); */
		System.out.println(viewName);
		ModelAndView mav = new ModelAndView();
		mav.addObject("result", result);
		mav.setViewName(viewName);
		return mav;
	}

	private String getViewName(HttpServletRequest request) throws Exception {
		String contextPath = request.getContextPath();
		String uri = (String) request.getAttribute("javax.servlet.include.request_uri");
		if (uri == null || uri.trim().equals("")) {
			uri = request.getRequestURI();
		}

		int begin = 0;
		if (!((contextPath == null) || ("".equals(contextPath)))) {
			begin = contextPath.length();
		}

		int end;
		if (uri.indexOf(";") != -1) {
			end = uri.indexOf(";");
		} else if (uri.indexOf("?") != -1) {
			end = uri.indexOf("?");
		} else {
			end = uri.length();
		}

		String viewName = uri.substring(begin, end);
		if (viewName.indexOf(".") != -1) {
			viewName = viewName.substring(0, viewName.lastIndexOf("."));
		}
		if (viewName.lastIndexOf("/") != -1) {
			viewName = viewName.substring(viewName.lastIndexOf("/", 1), viewName.length());
		}
		return viewName;
	}

	@Override
	@RequestMapping(value = "/member/modMember.do", method = RequestMethod.GET)
	public ModelAndView modMember(@RequestParam("id") String id, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//String id=request.getParameter("id");

		String viewName = getViewName(request);
		System.out.println("viewName(수정폼)이 뭐야? : " + viewName);
		ModelAndView mav = new ModelAndView();

		mav.addObject("user_id", id);
		MemberVO memberOne = memberService.getOneMember(id);

		mav.addObject("member", memberOne);

		mav.setViewName(viewName);
		return mav;
	}

	@Override
	@RequestMapping(value = "/member/updateMember.do", method = RequestMethod.POST)
	public ModelAndView updateMember(@ModelAttribute("memberVO") MemberVO memberVO, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
//			MemberVO memberVO = new MemberVO();
//			bind(request, memberVO);
		int result = 0;
		result = memberService.updateMember(memberVO);
		ModelAndView mav = new ModelAndView("redirect:/member/listMembers.do");
		return mav;
	}

}
