package com.example.demo.domain;

import lombok.Data;

/**
 * プレイヤークラス
 * @author T.Imura
 *
 */
@Data
public class Player {
	
	private int		x;			// X座標
	private int		y;			// Y座標
	private Dir		dir;		// 方向
	private int		steps;		// 歩数
	private int		stamina;	// スタミナ
	private boolean	key;		// カギ入手フラグ
	private int		score;		// スコア
	private boolean	goal;		// ゴール到達フラグ
	
	/**
	 * コンストラクタ
	 * @param x
	 * @param y
	 * @param dir
	 */
	public Player(int x, int y, Dir dir) {
		
		this.x		= x;
		this.y		= y;
		this.dir	= dir;
		
		steps		= 0;
		stamina		= 200;
		key			= false;
		score		= 0;
		goal		= false;
	}
	
	/**
	 * 一歩前に進む。
	 */
	public void step() {
		
		switch(dir) {
		case NORTH:
			y--;
			break;
		case EAST:
			x++;
			break;
		case SOUTH:
			y++;
			break;
		case WEST:
			x--;
			break;
		}
		
		// 歩数カウントアップ。
		steps++;
		
		// スタミナカウントダウン。
		stamina = --stamina < 0 ? 0 : stamina;
	}
	
	/**
	 * 左を向く。
	 */
	public void turnLeft() {
		
		switch(dir) {
		case NORTH:
			dir = Dir.WEST;
			break;
		case EAST:
			dir = Dir.NORTH;
			break;
		case SOUTH:
			dir = Dir.EAST;
			break;
		case WEST:
			dir = Dir.SOUTH;
			break;
		}
	}
	
	/**
	 * 右を向く。
	 */
	public void turnRight() {
		
		switch(dir) {
		case NORTH:
			dir = Dir.EAST;
			break;
		case EAST:
			dir = Dir.SOUTH;
			break;
		case SOUTH:
			dir = Dir.WEST;
			break;
		case WEST:
			dir = Dir.NORTH;
			break;
		}
	}
	
	/**
	 * 後ろを向く。
	 */
	public void turn() {
		
		switch(dir) {
		case NORTH:
			dir = Dir.SOUTH;
			break;
		case EAST:
			dir = Dir.WEST;
			break;
		case SOUTH:
			dir = Dir.NORTH;
			break;
		case WEST:
			dir = Dir.EAST;
			break;
		}
	}
	
	/**
	 * スタミナ回復
	 * @param pts
	 */
	public void heal(int pts) {
		
		stamina += pts;
	}
	
	/**
	 * スタミナダメージ
	 * @param pts
	 */
	public void damage(int pts) {
		
		stamina -= pts;
		
		if(stamina < 0) {
			stamina = 0;
		}
	}
	
	/**
	 * スコア加算
	 * @param pts
	 */
	public void addScore(int pts) {
		
		score += pts;
	}
}
