package com.myspring.pro30.board.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.myspring.pro30.board.service.BoardService;
import com.myspring.pro30.board.vo.ArticleVO;
import com.myspring.pro30.board.vo.ImageVO;
import com.myspring.pro30.member.vo.MemberVO;

@Controller("boardController")
public class BoardControllerImpl implements BoardController {
	private static final String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	@Autowired
	BoardService boardService;
	@Autowired
	ArticleVO articleVO;

	
	//전체 게시글 목록보기
	@Override
	@RequestMapping(value = "/board/listArticles.do", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView listArticles(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		List articlesList = boardService.listArticles();
		ModelAndView mav = new ModelAndView(viewName);
		mav.addObject("articlesList", articlesList);
		return mav;

	}

	//다중 이미지 업로드
	@Override
	@RequestMapping(value = "/board/addNewArticle.do", method = RequestMethod.POST)
	//@ResponseBody : 서버 -> 클라이언트 : 데이터만 전달 (json형태)
	@ResponseBody
	// ResponseEntity : 상태정보 (HTTP Status code) + data
	public ResponseEntity addNewArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		String imageFileName=null;
		
		// 서버 데이터 2가지 처리
		// 1) 일반 data 2) file data
		multipartRequest.setCharacterEncoding("utf-8");
		
		//articleMap : 일반 + 파일
		Map<String, Object> articleMap = new HashMap<String, Object>();
		
		// .getParameterNames(); -> 일반데이터
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			// key: title , value : 게시글 제목
			articleMap.put(name, value);
		}

		
		//파일 저장 부분
		//multiUpload매서드 : 이미지 이름 list 반환
		//String imageFileName = upload(multipartRequest);
		List<String> fileList =multiUpload(multipartRequest);
		
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String id = memberVO.getId();
		
		List<ImageVO> imageFileList = new ArrayList<ImageVO>();
		if(fileList!= null && fileList.size()!=0) {
			for(String fileName : fileList) {
				ImageVO imageVO = new ImageVO();
				imageVO.setImageFileName(fileName);
				imageFileList.add(imageVO);
			}
			articleMap.put("imageFileList", imageFileList);
		}
		
		
		
		articleMap.put("parentNO", 0);
		articleMap.put("id", id);

		String message;
		
		// ResponseEntity = 1)데이터 + 2)상태 + 3)헤더(추가요소) 
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");

		try {
			// articleMap: 구성요소, 
			// 1) 게시글 내용 2) 이미지 파일 이름 3) parentNO:0
			int articleNO = boardService.addNewArticle(articleMap);
			
			// 실제 이미지 파일을 -> 물리 저장소에 저장하는 로직. 
			if (imageFileList != null && imageFileList.size() != 0) {
				boardService.addOnlyImage2(articleMap,articleNO);
				
				for(ImageVO  imageVO:imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO+"\\"+"temp"+"\\"+imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO+"\\"+articleNO);
					//destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir,true);
				}
			}

			message = "<script>";
			message += " alert('새글을 추가했습니다.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/listArticles.do'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			//오류 발생시
			
			//임시 저장소 삭제
			File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
			srcFile.delete();

			message = " <script>";
			message += " alert('오류가 발생했습니다. 다시 시도해 주세요');');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/articleForm.do'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	// 답글쓰기, 단일 이미지 업로드 -> 다중 이미지 업로드 교체.
	@Override
	@RequestMapping(value = "/board/addReply.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity replyNewArticle(@RequestParam("parentNO") int parentNO,
			MultipartHttpServletRequest multipartRequest, HttpServletResponse response) throws Exception {
		String imageFileName = null;
		multipartRequest.setCharacterEncoding("utf-8");
		Map<String, Object> articleMap = new HashMap<String, Object>();

		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			articleMap.put(name, value);
			
			System.out.println("name : "+name +",value : "+value);
		}

		List<String> fileList = multiUpload(multipartRequest);

		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String id = memberVO.getId();
		
		List<ImageVO> imageFileList = new ArrayList<ImageVO>();
		if (fileList != null && fileList.size() != 0) {
			for (String fileName : fileList) {
				ImageVO imageVO = new ImageVO();
				imageVO.setImageFileName(fileName);
				imageFileList.add(imageVO);
			}
			articleMap.put("imageFileList", imageFileList);
		}

		articleMap.put("parentNO", parentNO);
		articleMap.put("id", id);
		
		System.out.println("parentNO : "+ parentNO);
		System.out.println("id : "+ id);

		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			// 변경 부분. 일반 데이터만 추가.
			int articleNO = boardService.addReplyNewArticle(articleMap);

			if (imageFileList != null && imageFileList.size() != 0) {
				boardService.addOnlyImage2(articleMap, articleNO);
				for (ImageVO imageVO : imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					// destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
			}
			message = "<script>";
			message += " alert('글쓰기 성공.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/listArticles.do'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
			srcFile.delete();
			message = " <script>";
			message += " alert('글쓰기 작성 오류');');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/articleForm.do'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	/*
	 * //한개의 이미지 보여주기
	 * 
	 * @RequestMapping(value="/board/viewArticle.do" ,method = RequestMethod.GET)
	 * public ModelAndView viewArticle(@RequestParam("articleNO") int articleNO,
	 * HttpServletRequest request, HttpServletResponse response) throws Exception{
	 * String viewName = (String)request.getAttribute("viewName");
	 * articleVO=boardService.viewArticle(articleNO); ModelAndView mav = new
	 * ModelAndView(); mav.setViewName(viewName); mav.addObject("article",
	 * articleVO); return mav; }
	 */

	// 다중 이미지 보여주기
	@RequestMapping(value = "/board/viewArticle.do", method = RequestMethod.GET)
	public ModelAndView viewArticle(@RequestParam("articleNO") int articleNO, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		Map articleMap = boardService.viewArticle(articleNO);
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		mav.addObject("articleMap", articleMap);
		return mav;
	}

	// 단일 이미지 수정 적용하는 코드.
	// 만약, 재사용 한다면, 일반 데이터는 그대로 두고,
	// 파일 이미지들만 변경해서 사용 하기.
	@RequestMapping(value = "/board/modArticle.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity modArticle(@RequestParam("articleNO") int articleNO,
			MultipartHttpServletRequest multipartRequest, HttpServletResponse response) throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		String imageFileName = null;
		// 임시 저장 박스 , 수정된 글, 이미지를 담는 박스
		Map<String, Object> articleMap = new HashMap<String, Object>();
		// 일반 데이터 추출
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			articleMap.put(name, value);
		}
		// 수정된 이미지 미디어 저장소 저장 후, 파일 이름 가져오기.
		// 수정, 다중이미지 추가로
		List<String> fileList = multiUpload(multipartRequest);

		List<ImageVO> imageFileList = new ArrayList<ImageVO>();
		if (fileList != null && fileList.size() != 0) {
			for (String fileName : fileList) {
				ImageVO imageVO = new ImageVO();
				imageVO.setImageFileName(fileName);
				imageFileList.add(imageVO);
			}
			// 이미지 모델 -> 리스트에 담기 -> 리스트 맵에 담아서, -> 동네2번으로 전달.
			articleMap.put("imageFileList", imageFileList);
		}
		// 일반 데이터, 파일 데이터를 , 맵이라는 컬렉션에 담은 로직은 마지막.

		// 데이터 + 상태 + 헤더추가, 서버 -> 클라이언트, 데이터 만 전달.
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			// 실제 작업 동네2번 외주 주기.
			// 주의사항, 일반 데이터, 파일 데이터 분리해서 디비에 저장.
			// 기존 로직, 새로운 게시글 작성,
			// 이미 있는 게시글을 수정하는 로직, 기존 게시글 번호를 이용하기.
			// modArticle
//			int articleNO = boardService.addNewArticle(articleMap);
			// 수정 적용하기. 일반 데이터만
			boardService.modArticle2(articleMap);

			//
			if (imageFileList != null && imageFileList.size() != 0) {
				// 기존 이미지를 삭제하는 부분 추가하고.

				// 수정 적용하기. 이미지 데이터만
				boardService.addOnlyImage(articleMap);
				for (ImageVO imageVO : imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					// destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
			}

			message = "<script>";
			message += " alert('글수정 성공.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/listArticles.do'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);

		} catch (Exception e) {
			if (imageFileList != null && imageFileList.size() != 0) {
				for (ImageVO imageVO : imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					srcFile.delete();
				}
			}

			message = " <script>";
			message += " alert('글쓰기 오류');');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/articleForm.do'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	@Override
	@RequestMapping(value = "/board/removeArticle.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity removeArticle(@RequestParam("articleNO") int articleNO, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=UTF-8");
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			boardService.removeArticle(articleNO);
			File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
			FileUtils.deleteDirectory(destDir);

			message = "<script>";
			message += " alert('글을 삭제했습니다.');";
			message += " location.href='" + request.getContextPath() + "/board/listArticles.do';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);

		} catch (Exception e) {
			message = "<script>";
			message += " alert('작업중 오류가 발생했습니다.다시 시도해 주세요.');";
			message += " location.href='" + request.getContextPath() + "/board/listArticles.do';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

//이미지만 삭제하기. 
	@Override
	@RequestMapping(value = "/board/deleteImage.do", method = RequestMethod.GET)
	@ResponseBody
	//
	public ResponseEntity deleteImage(@RequestParam("imageFileNO") int imageFileNO,
			@RequestParam("articleNO") int articleNO, @RequestParam("imageFileName") String imageFileName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=UTF-8");

		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			// 파일 이미지만 삭제, 디비에서
			boardService.removeImage(imageFileNO);
			// 미디어 서버 , 저장소에 저장된 파일을 삭제

			// 물리 저장소 삭제,
			File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + imageFileName);
			FileUtils.deleteQuietly(destDir);
//		FileUtils.deleteDirectory(destDir);

			message = "<script>";
			message += " alert('삭제 완료.');";
			message += " location.href='" + request.getContextPath() + "/board/viewArticle.do?articleNO=" + articleNO
					+ "';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);

		} catch (Exception e) {
			message = "<script>";
			message += " alert('삭제 오류.');";
			message += " location.href='" + request.getContextPath() + "/board/listArticles.do';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

//다중이미지 글쓰기 , addMultiImageNewArticle
	@Override
	@RequestMapping(value = "/board/addMultiImageNewArticle.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity addMultiImageNewArticle(MultipartHttpServletRequest multipartRequest,
			HttpServletResponse response) throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		String imageFileName = null;

		// 일반 데이터 + 다중 이미지 포함할 박스.
		Map articleMap = new HashMap();
		// 일반데이터 , 가져와서, 박스에 담는 작업.
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			articleMap.put(name, value);
		}

		// 로그인 한 멤버 정보, 세션에서 가져오기.
		HttpSession session = multipartRequest.getSession();
		MemberVO memberVO = (MemberVO) session.getAttribute("member");
		String id = memberVO.getId();

		// 박스에, 회원의 아이디 추가.
		articleMap.put("id", id);

		// 멀티 이미지를 업로드 하는 메서드.
		// 여러 이미지의 이름을 담은 리스트가 반환.
		// 물리 저장소에 이미지를 업로드함.
		List<String> fileList = multiUpload(multipartRequest);
		// fileList: 업로드 된 여러 이미지들의 파일의 이름 담겨져 있다.

		// 설명하기.
		// 이미지를 처리하기 편하게 하기위해서, 모델 클래스를 만들었음. ImageVO
		// 동네 1 ~ 동네 4 으로 전달 할 때, 박스 (Map) ,
		// 박스 하위에 - 이미지들의 정보를 담은 리스트를 추가해서, 같이 전송.

		// 여러 이미지의 모델 인스턴스를 하나의 리스트에 담기
		List<ImageVO> imageFileList = new ArrayList<ImageVO>();
		// 반복 작업, 담아 두었던 fileList : 이미지의 이름들 저장.
		// 여기서, 하나씩 꺼내어서, 이미지VO 라는 모델 클래스 타입으로 -> 리스트 담기.
		if (fileList != null && fileList.size() != 0) {
			// fileList 안에 이미지가 있다면
			// 반복문
			for (String fileName : fileList) {
				// 각각의 이미지를 모델에 담아서, 리스트에 추가하는 작업.
				ImageVO imageVO = new ImageVO();
				// 파일명을 UTF-8 인코딩 해서 추가.
//			System.out.println("setImageFileName 전 이름 값 확인: " + fileName);
				imageVO.setImageFileName(fileName);
				// 해결2, get에서 , 디코딩해서 원래 이름을 가져와서, 다시 세터
//			System.out.println("getImageFileName 디코딩 전 이름 값 확인: " + imageVO.getImageFileName());
				// URLEncoder.encode(imageFileName,"UTF-8");
//			String decodeFileName = URLDecoder.decode(imageVO.getImageFileName(), "UTF-8");
				// 또 인코딩, 무한 반복. 디코딩, 인코딩 한번 보는 예제 확인 넘어가기.
//			imageVO.setImageFileName(decodeFileName);
//			System.out.println("getImageFileName 디코딩 후 이름 값 확인: " + decodeFileName);
//			System.out.println("setImageFileName 후 이름 값 확인: " + imageVO.getImageFileName());
				imageFileList.add(imageVO);
			}
			// 이미지 모델 -> 리스트에 담기 -> 리스트 맵에 담아서, -> 동네2번으로 전달.
			articleMap.put("imageFileList", imageFileList);
		}
		// 일반 데이터, 파일 데이터를 , 맵이라는 컬렉션에 담은 로직은 마지막.

		// 데이터 + 상태 + 헤더추가, 서버 -> 클라이언트, 데이터 만 전달.
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			// 실제 작업 동네2번 외주 주기.
			// 주의사항, 일반 데이터, 파일 데이터 분리해서 디비에 저장.
			int articleNO = boardService.addNewArticle(articleMap);

			//
			if (imageFileList != null && imageFileList.size() != 0) {
				for (ImageVO imageVO : imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					// destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
			}

			message = "<script>";
			message += " alert('글쓰기 성공.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/listArticles.do'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);

		} catch (Exception e) {
			if (imageFileList != null && imageFileList.size() != 0) {
				for (ImageVO imageVO : imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					srcFile.delete();
				}
			}

			message = " <script>";
			message += " alert('글쓰기 오류');');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/articleForm.do'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

//다중이미지만 업로드. onlyImageUpload
	@Override
	@RequestMapping(value = "/board/onlyImageUpload.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity onlyImageUpload(@RequestParam("articleNO") int articleNO,
			MultipartHttpServletRequest multipartRequest, HttpServletResponse response) throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		String imageFileName = null;

		// 일반 데이터 + 다중 이미지 포함할 박스.
		Map articleMap = new HashMap();

		List<String> fileList = multiUpload(multipartRequest);

		List<ImageVO> imageFileList = new ArrayList<ImageVO>();

		if (fileList != null && fileList.size() != 0) {
			for (String fileName : fileList) {
				ImageVO imageVO = new ImageVO();
				imageVO.setImageFileName(fileName);
				imageFileList.add(imageVO);
			}
			articleMap.put("imageFileList", imageFileList);
		}

		String message;
		ResponseEntity resEnt = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		try {
			// 실제 작업. 기존 게시글에 이미지만 추가,
			boardService.addOnlyImage2(articleMap, articleNO);

			//
			if (imageFileList != null && imageFileList.size() != 0) {
				for (ImageVO imageVO : imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					// destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
			}

			message = "<script>";
			message += " alert('사진업로드 성공.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/viewArticle.do?articleNO="
					+ articleNO + "';";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);

		} catch (Exception e) {
			if (imageFileList != null && imageFileList.size() != 0) {
				for (ImageVO imageVO : imageFileList) {
					imageFileName = imageVO.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					srcFile.delete();
				}
			}

			message = " <script>";
			message += " alert('글쓰기 오류');');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/articleForm.do'; ";
			message += " </script>";
			resEnt = new ResponseEntity(message, responseHeaders, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	@RequestMapping(value = "/board/*Form.do", method = RequestMethod.GET)
	private ModelAndView form(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		return mav;
	}

	// 답글 폼
	@RequestMapping(value = "/board/replyForm.do", method = RequestMethod.GET)
	private ModelAndView form2(@RequestParam(value = "parentNO", required = false) int parentNO,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView();
		System.out.println("parentNO 확인: " + parentNO);
		mav.setViewName(viewName);

		mav.addObject("parentNO", parentNO);

		return mav;
	}

	// 수정 폼
	@RequestMapping(value = "/board/modForm.do", method = { RequestMethod.GET, RequestMethod.POST })
	private ModelAndView form3(@RequestParam(value = "articleNO", required = false) int articleNO,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		Map articleMap = boardService.viewArticle(articleNO);
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		mav.addObject("articleMap", articleMap);

		return mav;
	}

	// 한개 이미지 업로드하기
	private String upload(MultipartHttpServletRequest multipartRequest) throws Exception {
		String imageFileName = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();

		while (fileNames.hasNext()) {
			String fileName = fileNames.next();
			MultipartFile mFile = multipartRequest.getFile(fileName);
			imageFileName = mFile.getOriginalFilename();
			File file = new File(ARTICLE_IMAGE_REPO + "\\" + fileName);
			if (mFile.getSize() != 0) { // File Null Check
				if (!file.exists()) { // 경로상에 파일이 존재하지 않을 경우
					if (file.getParentFile().mkdirs()) { // 경로에 해당하는 디렉토리들을 생성
						file.createNewFile(); // 이후 파일 생성
					}
				}
				mFile.transferTo(new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName)); // 임시로 저장된
																										// multipartFile을
																										// 실제 파일로 전송
			}
		}
		return imageFileName;
	}
	
	
	//미디어 저장소, 이미지 파일 올리기. 다중이미지
	private List<String> multiUpload(MultipartHttpServletRequest multipartRequest) throws Exception{
		// 여러 이미지 파일 이름을 담는 박스 
		List<String> fileList= new ArrayList<String>();
		// 멀티 파트에 담겨진 이미지 파일들을 가져오는 로직.
		Iterator<String> fileNames = multipartRequest.getFileNames();
		// 여러 이미지를 반복문을 이용해서, 원본 이름을 가져오고, 실제 물리 저장소 저장 
		while(fileNames.hasNext()){
			// file1 의 이름에 담겨진 , 파일 이미지 값을 조회
			String fileName = fileNames.next();
			// file1 작업 하기위한 인스턴스
			MultipartFile mFile = multipartRequest.getFile(fileName);
			// file1의 원본 이름을 가져오기.
			String originalFileName=mFile.getOriginalFilename();
			// file1 에 담겨진 원본 이름을 임시 박스에(fileList) 담기. 
			fileList.add(originalFileName);
			// 실제 물리 저장소에 저장될 이미지 파일. 
			File file = new File(ARTICLE_IMAGE_REPO +"\\"+ fileName);
			// 이미지 있다면(크기가 0이 아니면)
			if(mFile.getSize()!=0){ //File Null Check
				// 파일 존재안하면
				if(! file.exists()){ 
					//  파일의 부모 폴더 만들고
					if(file.getParentFile().mkdirs()){
						// 해당 파일도 만들기. 
							file.createNewFile(); 
					}
				}
				// 임시 폴더 , temp에 저장후, 원래 경로로 이동할 예정. 
				mFile.transferTo(new File(ARTICLE_IMAGE_REPO +"\\"+"temp"+ "\\"+originalFileName)); 
			}
		}
		return fileList;
	}

}
