package com.myspring.pro30.board.service;

import java.util.List;
import java.util.Map;

import com.myspring.pro30.board.vo.ArticleVO;

public interface BoardService {
	public List<ArticleVO> listArticles() throws Exception;
	
	//다중 이미지 글쓰기
	public int addNewArticle(Map articleMap) throws Exception;
	
	//이미지만 업로드, 새 게시글 번호 사용
	public void addOnlyImage(Map articleMap) throws Exception;
	
	//이미지만 업로드, 기존 게시글 번호 사용
	public void addOnlyImage2(Map articleMap, int articleVO) throws Exception;
	
	//단일 이미지 상세 뷰 보기
	//	public ArticleVO viewArticle(int articleNO) throws Exception;

	//다중 이미지 상세 뷰 보기
	public Map viewArticle(int articleNO) throws Exception;

	//단일 이미지 수정
	public void modArticle(Map articleMap) throws Exception;

	//다중 이미지 수정
	public void modArticle2(Map articleMap) throws Exception;

	//답글 작성
	public int addReplyNewArticle(Map articleMap) throws Exception;
	
	//게시글 삭제
	public void removeArticle(int articleNO) throws Exception;
	
	//이미지만 삭제(db)
	public void removeImage(int imageFileNO) throws Exception;
	
	
}
