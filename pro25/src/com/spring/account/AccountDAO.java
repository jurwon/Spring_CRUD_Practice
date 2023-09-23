package com.spring.account;

import org.apache.ibatis.session.SqlSession;
import org.springframework.dao.DataAccessException;

public class AccountDAO {
	private SqlSession sqlSession;

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	//SQL 1 : 송금
	public void updateBalance1() throws DataAccessException {
		sqlSession.update("mapper.account.updateBalance1");
	}
	
	//SQL 2 : 수신
	public void updateBalance2() throws DataAccessException {
		sqlSession.update("mapper.account.updateBalance2");
	}
	

}
