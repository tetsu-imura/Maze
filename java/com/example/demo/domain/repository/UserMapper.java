package com.example.demo.domain.repository;

import java.util.Date;
/**
 * ユーザーマッパーインターフェース
 * @author T.Imura
 * 
 */
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.domain.entity.User;

@Mapper
public interface UserMapper {
	
	public List<User> getBest5();
	
	public void addNewRanker(String name, int score, Date date);
}
