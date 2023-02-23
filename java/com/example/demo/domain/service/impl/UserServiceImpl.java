package com.example.demo.domain.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserMapper;
import com.example.demo.domain.service.UserService;

/**
 * ユーザーサービス実装クラス
 * @author T.Imura
 *
 */
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserMapper mapper;
	
	@Override
	public List<User> getBest5() {
		return mapper.getBest5();
	}
	
	@Override
	public void addNewRanker(String name, int score, Date date) {
		mapper.addNewRanker(name, score, date);
	}
}
