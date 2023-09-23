package com.spring.account;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//@Transactional(propagation=Propagation.REQUIRED)
public class AccountService {
	//DAO 선언 & 초기화
	private AccountDAO accDAO;

	public void setAccDAO(AccountDAO accDAO) {
		this.accDAO = accDAO;
	}

	// 하나의 기능에 2개 SQL 묶음(트랜잭션)
	public void sendMoney() throws Exception {
		accDAO.updateBalance1();
		accDAO.updateBalance2();
	}
}


