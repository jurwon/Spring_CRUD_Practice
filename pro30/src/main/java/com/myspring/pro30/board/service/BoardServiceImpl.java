package com.myspring.pro30.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.myspring.pro30.board.dao.BoardDAO;
import com.myspring.pro30.board.vo.ArticleVO;
import com.myspring.pro30.board.vo.ImageVO;


@Service("boardService")
@Transactional(propagation = Propagation.REQUIRED)
public class BoardServiceImpl  implements BoardService{
	@Autowired
	BoardDAO boardDAO;
	
	//전체 게시글 보기
	public List<ArticleVO> listArticles() throws Exception{
		List<ArticleVO> articlesList =  boardDAO.selectAllArticlesList();
        return articlesList;
	}

	
	//단일 이미지 게시글작성
	/*
	 * @Override public int addNewArticle(Map articleMap) throws Exception{ 
	 * return boardDAO.insertNewArticle(articleMap); 
	 * }
	 */
	
	
	//단일 이미지 답글쓰기
	@Override
	public int addReplyNewArticle(Map articleMap) throws Exception {
		return boardDAO.insertReplyNewArticle(articleMap); 
	}
	
	 //다중 이미지 게시글작성
	@Override
	public int addNewArticle(Map articleMap) throws Exception{
		//articleNO : 새 게시글 번호
		int articleNO = boardDAO.insertNewArticle(articleMap);
		
		//새 게시글 번호 등록
		articleMap.put("articleNO", articleNO);
		
		// t_imageFile에 이미지 저장 (imageFileName,articleNO)
		//boardDAO.insertNewImage(articleMap);
		return articleNO;
	}
	
	//다중 이미지 이미지만 db저장
	@Override
	public void addOnlyImage(Map articleMap) throws Exception {
		int articleNO = boardDAO.insertNewArticle(articleMap);
		articleMap.put("articleNO", articleNO);
		boardDAO.insertNewImage(articleMap);
	}
	
	//이미지만 업로드, 기존 게시글에 추가
	@Override
	public void addOnlyImage2(Map articleMap, int articleNO) throws Exception{
		articleMap.put("articleNO", articleNO);
		boardDAO.insertNewImage(articleMap);
	}
	
	
	//다중 파일 보이기
	@Override
	public Map viewArticle(int articleNO) throws Exception {
		Map articleMap = new HashMap();
		ArticleVO articleVO = boardDAO.selectArticle(articleNO);
		List<ImageVO> imageFileList = boardDAO.selectImageFileList(articleNO);
		articleMap.put("article", articleVO);
		articleMap.put("imageFileList", imageFileList);
		return articleMap;
	}
   
	
	
	
	//단일 파일 보이기
	/*
	 * @Override public ArticleVO viewArticle(int articleNO) throws Exception {
	 * 	ArticleVO articleVO = boardDAO.selectArticle(articleNO); 
	 * return articleVO; }
	 */
	
	
	//단일 이미지 수정
	@Override
	public void modArticle(Map articleMap) throws Exception {
		boardDAO.updateArticle(articleMap);
	}
	
	//다중 이미지 수정
	@Override
	public void modArticle2(Map articleMap) throws Exception {
		boardDAO.updateArticle2(articleMap);
	}
	
	@Override
	public void removeArticle(int articleNO) throws Exception {
		boardDAO.deleteArticle(articleNO);
	}
	
	//이미지만 삭제
	@Override
	public void removeImage(int articleNO) throws Exception {
		boardDAO.deleteImage(articleNO);
	}
	
	


	
}
