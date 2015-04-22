package org.mybatis.jpetstore.service;

import org.mybatis.jpetstore.domain.Account;
import org.mybatis.jpetstore.persistence.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kieker.monitoring.annotation.OperationExecutionMonitoringProbe;

@Service
public class AccountService {

	private final ComplexityService complexityService = ComplexityService
			.getInstance();
	
	@Autowired
	private AccountMapper accountMapper;

	@OperationExecutionMonitoringProbe
	public Account getAccount(String username) {
		this.complexityService.compute("AccountService.getAccount");
		return accountMapper.getAccountByUsername(username);
	}

	@OperationExecutionMonitoringProbe
	public Account getAccount(String username, String password) {
		this.complexityService.compute("AccountService.getAccount");
		Account account = new Account();
		account.setUsername(username);
		account.setPassword(password);
		return accountMapper.getAccountByUsernameAndPassword(account);
	}

	@Transactional
	public void insertAccount(Account account) {
		accountMapper.insertAccount(account);
		accountMapper.insertProfile(account);
		accountMapper.insertSignon(account);
	}

	@Transactional
	public void updateAccount(Account account) {
		accountMapper.updateAccount(account);
		accountMapper.updateProfile(account);

		if (account.getPassword() != null && account.getPassword().length() > 0) {
			accountMapper.updateSignon(account);
		}
	}

}
