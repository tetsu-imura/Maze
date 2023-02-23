package com.example.demo.domain.entity;

import java.util.Date;

import lombok.Data;

@Data
public class User {
	
	private String	name;
	private int		score;
	private Date	date;
}
