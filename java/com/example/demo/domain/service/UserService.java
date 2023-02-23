package com.example.demo.domain.service;

import java.util.Date;
import java.util.List;

import com.example.demo.domain.entity.User;

/**
 * ユーザーサービスインターフェース
 * @author T.Imura
 *
 */
public interface UserService {
	
	public List<User> getBest5();
	
	public void addNewRanker(String name, int score, Date date);
}
