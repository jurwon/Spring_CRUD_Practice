package com.myspring.pro28.ex01;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/*@Controller*/
public class FileDownloadController {
	private static String CURR_IMAGE_REPO_PATH = "c:\\spring\\image_repo";

	@RequestMapping("/download")
	protected void download(@RequestParam("imageFileName") String imageFileName,
			                 HttpServletResponse response)throws Exception {
		//출력
		OutputStream out = response.getOutputStream();
		//저장위치 (이미 이미지가 로컬에 있다고 가정)
		String downFile = CURR_IMAGE_REPO_PATH + "\\" + imageFileName;
		File file = new File(downFile); 

		//response : webbrowser에 응답
		response.setHeader("Cache-Control", "no-cache"); //캐시 저장 안하고 매번 똑같이 파일 출력. 재사용 안함
		response.addHeader("Content-disposition", "attachment; fileName=" + imageFileName); //파일이름 첨부
		
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
	}

}
