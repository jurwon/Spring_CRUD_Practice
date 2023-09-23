package com.myspring.pro27.member.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myspring.pro27.member.service.MemberService;
import com.myspring.pro27.member.vo.MemberVO;



@Controller("memberController")
@EnableAspectJAutoProxy
public class MemberControllerImpl   implements MemberController {
//	private static final Logger logger = LoggerFactory.getLogger(MemberControllerImpl.class);
	@Autowired
	private MemberService memberService;
	@Autowired
	MemberVO memberVO ;
	
	private static final String CURR_IMAGE_REPO_PATH = "c:\\spring\\image_repo";
	
	@Override
	@RequestMapping(value="/member/listMembers.do" ,method = RequestMethod.GET)
	public ModelAndView listMembers(
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//String viewName = getViewName(request);
		String viewName = (String)request.getAttribute("viewName");
		System.out.println("인터셉터 확인 viewName: " +viewName);
//		logger.info("viewName: "+ viewName);
//		logger.debug("viewName: "+ viewName);
		List membersList = memberService.listMembers();
		
		ModelAndView mav = new ModelAndView(viewName);
		mav.addObject("membersList", membersList);
		return mav;
	}

	@Override
	@RequestMapping(value="/member/addMember.do" ,method = RequestMethod.POST)
	public ModelAndView addMember(@ModelAttribute("member") MemberVO member,
							  MultipartHttpServletRequest multipartRequest, HttpServletResponse response) throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		
		// 일반데이터 + 파일데이터 set담을 공간
		Map map = new HashMap();
		
		//일반데이터
		Enumeration enu = multipartRequest.getParameterNames(); //반복
		while(enu.hasMoreElements()){
			String name=(String)enu.nextElement(); //key(name, id, pwd, email)
			String value=multipartRequest.getParameter(name); //value(이순신, m2. 1234,eee@naver.com)
			System.out.println("아이디 : "+ name +", value : "+value);
			map.put(name,value);
		}
		
		//파일데이터
		List fileList= fileProcess(multipartRequest);
		map.put("fileList", fileList);
		
		member.setImage((String)fileList.get(0)); //memverVO에 이미지 이름 추가
		
		int result = 0;
		result = memberService.addMember(member);
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("map", map);
		mav.setViewName("redirect:/member/listMembers.do");
		return mav;
	}
	
	@Override
	@RequestMapping(value="/member/removeMember.do" ,method = RequestMethod.GET)
	public ModelAndView removeMember(@RequestParam("id") String id, 
			           HttpServletRequest request, HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("utf-8");
		memberService.removeMember(id);
		ModelAndView mav = new ModelAndView("redirect:/member/listMembers.do");
		return mav;
	}
	/*
	@RequestMapping(value = { "/member/loginForm.do", "/member/memberForm.do" }, method =  RequestMethod.GET)
	@RequestMapping(value = "/member/*Form.do", method =  RequestMethod.GET)
	public ModelAndView form(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = getViewName(request);
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		return mav;
	}
	*/
	
	@Override
	@RequestMapping(value = "/member/login.do", method = RequestMethod.POST)
	public ModelAndView login(@ModelAttribute("member") MemberVO member,
				              RedirectAttributes rAttr,
		                       HttpServletRequest request, HttpServletResponse response) throws Exception {
	ModelAndView mav = new ModelAndView();
	memberVO = memberService.login(member);
	if(memberVO != null) {
		    HttpSession session = request.getSession();
		    session.setAttribute("member", memberVO);
		    session.setAttribute("isLogOn", true);
		    mav.setViewName("redirect:/member/listMembers.do");
	}else { //로그인 실패
		    rAttr.addAttribute("result","loginFailed");
		    mav.setViewName("redirect:/member/loginForm.do");
	}
	return mav;
	}

	@Override
	@RequestMapping(value = "/member/logout.do", method =  RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		session.removeAttribute("member");
		session.removeAttribute("isLogOn");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/member/listMembers.do");
		return mav;
	}	

	@RequestMapping(value = "/member/*Form.do", method =  RequestMethod.GET)
	private ModelAndView form(@RequestParam(value= "result", required=false) String result,
						       HttpServletRequest request, 
						       HttpServletResponse response) throws Exception {
		String viewName = getViewName(request);
		/* String viewName = (String)request.getAttribute("viewName"); */
		System.out.println(viewName);
		ModelAndView mav = new ModelAndView();
		mav.addObject("result",result);
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
	@RequestMapping(value = "/member/modMember.do", method =  RequestMethod.GET)
	public ModelAndView modMember(@RequestParam("id") String id,HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 수정하는 폼에서, id를 get 방식으로 전송해서, 서버측에 받을 수 있음. 
				// id를 가져오는 구조는, 삭제에서 복붙. 재사용.
//				String id=request.getParameter("id");
				
				
				String viewName = getViewName(request);
				System.out.println("viewName(수정폼)이 뭐야? : " + viewName);
				ModelAndView mav = new ModelAndView();
				
				// mav 에 데이터를 넣는 구조, 회원가입에서 복붙. 재사용.
				// 결과 뷰에, 아이디만 전달함. 
				// 만약, 이 아이디에 관련된 모든 정보를 결과 뷰에 재사용할려면
				// 이 아이디로 하나의 회원의 정보를 디비에서 가져고 와서, 
				// 이 하나의 회원의 정보를 결과 뷰에 넣으면됨. 
				mav.addObject("user_id", id);
				
				// 추가, 해당 아이디로, 정보를 가져오기. 
				// 조회된 한 회원의 정보를 담을 임시 인스턴스 : memberOne
				// getOneMember : 서비스에 아직 없는 메서드 임. 임의로 추가. 
				// 외주, 서비스로 동네2번 가기. 인터페이스도 추상메서드 추가. 
				// 구현한 클래스에도 재정의 하기. 
				MemberVO memberOne = memberService.getOneMember(id);
				
				//출력
				OutputStream out = response.getOutputStream();
				//저장위치 (이미 이미지가 로컬에 있다고 가정)
				String downFile = CURR_IMAGE_REPO_PATH + "\\" + memberOne.getImage();
				System.out.println(downFile);
				File file = new File(downFile); 
				
				//response : webbrowser에 응답
				response.setHeader("Cache-Control", "no-cache"); //캐시 저장 안하고 매번 똑같이 파일 출력. 재사용 안함
				response.addHeader("Content-disposition", "attachment; fileName=" +  memberOne.getImage()); //파일이름 첨부
				
				//이미지 바이트 단위로 읽어서 메모리에 참조형 변수 in에 담음
				FileInputStream in = new FileInputStream(file);
				byte[] buffer = new byte[1024 * 8]; //8바이트
				while (true) {
					int count = in.read(buffer); // 버퍼에 읽어들인 문자개수
					if (count == -1) // 버퍼의 마지막에 도달했는지 체크
						break;
					out.write(buffer, 0, count);
				}
				in.close();
				out.close();
				
				
				
				// 디비에서 , 회원 정보를 가져왔으면 뷰에 데이터 전달하기. 
				mav.addObject("member", memberOne);
				
				// 결과 뷰로 가게끔, 설정. 
				mav.setViewName(viewName);
				return mav;
	}
	
	//애너테이션 기법으로 교체 작업
		@Override
		@RequestMapping(value = "/member/updateMember.do", method =  RequestMethod.POST)
		public ModelAndView updateMember(@ModelAttribute("memberVO") MemberVO memberVO, HttpServletRequest request, HttpServletResponse response) throws Exception {
			request.setCharacterEncoding("utf-8");
//			MemberVO memberVO = new MemberVO();
//			bind(request, memberVO);
			int result = 0;
			// 실제 업데이트를 반영하는 로직, 외주주기. 동네 2번 보내기 
			// 이름 : updateMember
			result = memberService.updateMember(memberVO);
			ModelAndView mav = new ModelAndView("redirect:/member/listMembers.do");
			return mav;
		}
		
		
		private List<String> fileProcess(MultipartHttpServletRequest multipartRequest) throws Exception{
			List<String> fileList= new ArrayList<String>();
			Iterator<String> fileNames = multipartRequest.getFileNames();
			while(fileNames.hasNext()){
				String fileName = fileNames.next();//하나씩 가져옴
				
				MultipartFile mFile = multipartRequest.getFile(fileName);//이미지파일경로
				String originalFileName=mFile.getOriginalFilename();
				
				fileList.add(originalFileName); //리스트에파일이미지 저장
				File file = new File(CURR_IMAGE_REPO_PATH +"\\"+ fileName);
				if(mFile.getSize()!=0){ //File Null Check
					if(! file.exists()){ //경로상에 파일이 존재하지 않을 경우
						if(file.getParentFile().mkdirs()){ //경로에 해당하는 디렉토리들을 생성
							file.createNewFile(); //이후 파일 생성
						}
					}
					mFile.transferTo(new File(CURR_IMAGE_REPO_PATH +"\\"+ originalFileName)); //임시로 저장된 multipartFile을 실제 파일로 전송
				}
			}
			return fileList;
		}
	
}
