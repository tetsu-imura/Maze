package com.example.demo.domain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import lombok.Data;

/**
 * ダンジョンクラス
 * @author T.Imura
 *
 */
@Data
public class Dungeon {
	
	final static int PATH			= 0;	// 通路
	final static int BLOCK			= 1;	// 壁
	final static int GOAL			= 2;	// ゴール
	final static int UNKNOWN		= -1;	// 未知
	final static int VIEW_W		= 5;	// View領域の横幅
	final static int VIEW_H		= 4;	// View領域の立幅
	final static int MINIMAP_W		= 15;	// ミニマップの横幅
	final static int MINIMAP_H		= 15;	// ミニマップの縦幅
	
	private int			mapWidth;			// マップの横幅
	private int			mapHeight;			// マップの縦幅
	private int[]		map;				// マップ
	private boolean[]	walked;				// 歩いた箇所マップ
	private Event[]		event;				// イベントマップ
	private int[]		view				// 疑似3D描画用ビュー
						= new int[VIEW_W * VIEW_H];
	private boolean		goalFlg;			// ゴール設定フラグ
	
	/**
	 * コンストラクタ
	 * @param width
	 * @param height
	 */
	public Dungeon(int width, int height) {
		
		mapWidth	= width;
		mapHeight	= height;
		map			= new int[mapWidth * mapHeight];
		walked		= new boolean[mapWidth * mapHeight];
		event		= new Event[mapWidth * mapHeight];
		goalFlg		= false;
	}
	
	/**
	 * 範囲max内の奇数の乱数を生成する。
	 * @param max
	 * @return
	 */
	public int randOdd(int max) {
		
		int result;
		
		do {
			result = new Random().nextInt(max);
		} while(result % 2 == 0);
		
		return result;
	}
	
	/**
	 * 迷路を初期化する。
	 * @return
	 */
	public void init(int startX, int startY) {
		
		// ブロックで全部埋める。
		for(int i = 0; i < mapWidth * mapHeight; i++) {
			map[i]		= BLOCK;
			walked[i]	= false;
			event[i]	= Event.NONE;
		}
		
		// スタート地点の設定。
		event[startX + mapWidth * startY] = Event.START;
		
		// 再帰呼び出しで迷路を作成。
		map[startX + mapWidth * startY] = PATH;
		dig(startX, startY);
		
		// ランダムで適度(mapWidth/2)に通り道を作る。
		int i = 0;
		while(i < mapWidth / 2) {
			int rx = new Random().nextInt(mapWidth - 2) + 1;
			int ry = new Random().nextInt(mapHeight - 2) + 1;
			
			// 通路ならやり直し。
			if(map[rx + mapWidth * ry] == PATH) {
				continue;
			}
			
			// 左右がブロック、かつ上下が通路の場合
			if(map[rx - 1 + mapWidth * ry] == BLOCK && map[rx + 1 + mapWidth * ry] == BLOCK
			&& map[rx + mapWidth * (ry - 1)] == PATH && map[rx + mapWidth * (ry + 1)] == PATH) {
				map[rx + mapWidth * ry] = PATH;
				i++;
			} else
			// 上下がブロック、かつ左右が通路の場合
			if(map[rx + mapWidth * (ry - 1)] == BLOCK && map[rx + mapWidth * (ry + 1)] == BLOCK
			&& map[rx - 1 + mapWidth * ry] == PATH && map[rx + 1 + mapWidth * ry] == PATH) {
				map[rx + mapWidth * ry] = PATH;
				i++;
			}
		}
		
		// イベントを設置する。
		setEvent();
		
		return;
	}
	
	/**
	 * 迷路を掘る。
	 * @param x
	 * @param y
	 */
	private void dig(int x, int y) {
		
		// ４方向のランダムなリストを生成する。
		List<Dir> dirList =
				new ArrayList<Dir>(Arrays.asList(Dir.NORTH, Dir.EAST, Dir.SOUTH, Dir.WEST));
		Collections.shuffle(dirList);
		
		// 再帰呼び出しで４方向に掘り進める。
		for(Dir dir: dirList) {
			switch (dir) {
			case NORTH:
				if(y - 2 > 0) {
					// ２つ先が壁の場合、掘り進める。
					if(map[x + mapWidth * (y - 2)] == BLOCK) {
						map[x + mapWidth * (y - 1)] = PATH;
						map[x + mapWidth * (y - 2)] = PATH;
						dig(x, y - 2);
					}
				}
				break;
			case EAST:
				if(x + 2 < mapWidth) {
					// ２つ先が壁の場合、掘り進める。
					if(map[x + 2 + mapWidth * y] == BLOCK) {
						map[x + 1 + mapWidth * y] = PATH;
						map[x + 2 + mapWidth * y] = PATH;
						dig(x + 2, y);
					}
				}
				break;
			case SOUTH:
				if(y + 2 < mapHeight) {
					// ２つ先が壁の場合、掘り進める。
					if(map[x + mapWidth * (y + 2)] == BLOCK) {
						map[x + mapWidth * (y + 1)] = PATH;
						map[x + mapWidth * (y + 2)] = PATH;
						dig(x, y + 2);
					}
				}
				break;
			case WEST:
				if(x - 2 > 0) {
					// ２つ先が壁の場合、掘り進める。
					if(map[x - 2 + mapWidth * y] == BLOCK) {
						map[x - 1 + mapWidth * y] = PATH;
						map[x - 2 + mapWidth * y] = PATH;
						dig(x - 2, y);
					}
				}
				break;
			}
		}
		
		// 最初にここに来た時にゴールを設定する。
		if(!goalFlg) {
			goalFlg = true;
			event[x + mapWidth * y] = Event.GOAL;
		}
	}
	
	/**
	 * ランダムでイベントを設置する。
	 */
	private void setEvent() {
		
		// イベントリストをシャッフルする。
		List<Event> eventList = new ArrayList<Event>(Arrays.asList(
			Event.HIGH_STAMINA,
			Event.STAMINA, Event.STAMINA, Event.STAMINA, Event.STAMINA, Event.STAMINA,
			Event.DAMAGE, Event.DAMAGE, Event.DAMAGE, Event.DAMAGE,
			Event.KEY,
			Event.SWORD1,
			Event.JEWEL1, Event.JEWEL2, Event.JEWEL3, Event.JEWEL4, Event.JEWEL5,
			Event.ITEM1, Event.ITEM2, Event.ITEM3, Event.ITEM4, Event.ITEM5,
			Event.ITEM6, Event.ITEM7, Event.ITEM8, Event.ITEM9, Event.ITEM10
		));
		Collections.shuffle(eventList);
		
		// 設置不可(壁、イベント設置済み)でないなら設置する。
		for(Event ev: eventList) {
			// ランダムな地点を決める。
			int x;
			int y;
			while(true) {
				x = randOdd(mapWidth - 1);
				y = randOdd(mapWidth - 1);
				
				// ブロックならイベント設置不可。
				if(map[x + mapWidth * y] == BLOCK) {
					continue;
				}
				// すでにイベント設置済みなら不可。
				if(event[x + mapWidth * y] != Event.NONE) {
					continue;
				}
				break;
			}
			event[x + mapWidth * y] = ev;
		}
	}
	
	/**
	 * Viewを設定する。
	 * 		疑似3D画面は設定したViewを元に描画する。
	 * 		Viewは常に北向きに補正して設定される。
	 * @param x
	 * @param y
	 * @param dir
	 */
	public void setView(int x, int y, Dir dir) {
		
		// 範囲外はエラー。
		if(x < 1 || mapWidth < x) {
			return;
		}
		if(x < 1 || mapHeight < y) {
			return;
		}
		
		// 北向きの時のXとYの差分
		int[] diffNX = {-2, -1,  0,  1,  2,
						-2, -1,  0,  1,  2,
						-2, -1,  0,  1,  2,
						-2, -1,  0,  1,  2};
		int[] diffNY = {-3, -3, -3, -3, -3,
						-2, -2, -2, -2, -2,
						-1, -1, -1, -1, -1,
						 0,  0,  0,  0,  0};
		// 東向きの時のXとYの差分
		int[] diffEX = { 3,  3,  3,  3,  3,
						 2,  2,  2,  2,  2,
						 1,  1,  1,  1,  1,
						 0,  0,  0,  0,  0};
		int[] diffEY = {-2, -1,  0,  1,  2,
						-2, -1,  0,  1,  2,
						-2, -1,  0,  1,  2,
						-2, -1,  0,  1,  2};
		// 南向きの時のXとYの差分
		int[] diffSX = { 2,  1,  0, -1, -2,
						 2,  1,  0, -1, -2,
						 2,  1,  0, -1, -2,
						 2,  1,  0, -1, -2};
		int[] diffSY = { 3,  3,  3,  3,  3,
						 2,  2,  2,  2,  2,
						 1,  1,  1,  1,  1,
						 0,  0,  0,  0,  0};
		// 西向きの時のXとYの差分
		int[] diffWX = {-3, -3, -3, -3, -3,
						-2, -2, -2, -2, -2,
						-1, -1, -1, -1, -1,
						 0,  0,  0,  0,  0};
		int[] diffWY = { 2,  1,  0, -1, -2,
						 2,  1,  0, -1, -2,
						 2,  1,  0, -1, -2,
						 2,  1,  0, -1, -2};
		
		for(int i = 0; i < VIEW_H * VIEW_W; i++) {
			int currentX = 0;
			int currentY = 0;
			
			// XとYを方向毎に差分をとって設定。
			switch(dir) {
			case NORTH:
				currentX = x + diffNX[i];
				currentY = y + diffNY[i];
				break;
			case EAST:
				currentX = x + diffEX[i];
				currentY = y + diffEY[i];
				break;
			case SOUTH:
				currentX = x + diffSX[i];
				currentY = y + diffSY[i];
				break;
			case WEST:
				currentX = x + diffWX[i];
				currentY = y + diffWY[i];
				break;
			}
			
			// 範囲外は壁にする。
			if(currentX < 0 || mapWidth <= currentX
			|| currentY < 0 || mapHeight <= currentY) {
				view[i] = BLOCK;
			}
			// Viewにマップ情報をコピーする。
			else {
				view[i] = map[currentX + mapWidth * currentY];
			}
		}
	}
	
	/**
	 * 指定の位置がブロックか確認する。
	 * @param pos
	 * @return
	 */
	public boolean isBlock(int pos) {
		
		if(view[pos] == BLOCK) {
			return true;
		}
		return false;
	}
	
	/**
	 * １歩前に進めるか確認する。
	 * @param x	現在地のX座標
	 * @param y	現在地のY座標
	 * @param dir	方向
	 * @return
	 */
	public boolean isAdvanceable(int x, int y, Dir dir) {
		
		switch(dir) {
		case NORTH:
			if(y == 0) {
				return false;
			}
			// 一歩北が壁の場合、進めない。
			if(map[x + mapWidth * (y - 1)] == BLOCK) {
				return false;
			}
			break;
		case EAST:
			if(x == mapWidth - 1) {
				return false;
			}
			// 一歩東が壁の場合、進めない。
			if(map[x + 1 + mapWidth * y] == BLOCK) {
				return false;
			}
			break;
		case SOUTH:
			if(y == mapHeight - 1) {
				return false;
			}
			// 一歩南が壁の場合、進めない。
			if(map[x + mapWidth * (y + 1)] == BLOCK) {
				return false;
			}
			break;
		case WEST:
			if(x == 0) {
				return false;
			}
			// 一歩西が壁の場合、進めない。
			if(map[x - 1 + mapWidth * y] == BLOCK) {
				return false;
			}
			break;
		}
		
		return true;
	}
	
	/**
	 * ミニマップを取得する。
	 * 		ここで得たminiMapを元にhtmlでマップ描画を行う。
	 * @param x	現在地のX座標(ミニマップの中心)
	 * @param y	現在地のY座標(ミニマップの中心)
	 * @return
	 */
	public int[] getMiniMap(int x, int y)
	{
		int[] miniMap = new int[MINIMAP_W * MINIMAP_H];
		
		for(int j = 0; j < MINIMAP_H; j++) {
			for(int i = 0; i < MINIMAP_W; i++) {
				// mapから取得するX、Y(現在地からの差分)
				int diffX = x - (MINIMAP_W - 1) / 2 + i;
				int diffY = y - (MINIMAP_H - 1) / 2 + j;
				
				// マップ範囲外は通路にする。
				if(diffX < 0 || mapWidth <= diffX) {
					miniMap[i + MINIMAP_W * j] = UNKNOWN;
				}
				else if(diffY < 0 || mapHeight <= diffY) {
					miniMap[i + MINIMAP_W * j] = UNKNOWN;
				}
				// マップ情報をミニマップにコピーする。
				else {
					// まだ歩いてない箇所はUNKNOWNにする。
					if(!walked[diffX + mapWidth * diffY]) {
						miniMap[i + MINIMAP_W * j] = UNKNOWN;
					} else {
						if(event[diffX + mapWidth * diffY] == Event.GOAL) {
							// ゴール地点はゴールの画像を表示する。
							miniMap[i + MINIMAP_W * j] = GOAL;
						} else {
							miniMap[i + MINIMAP_W * j] = map[diffX + mapWidth * diffY];
						}
					}
				}
			}
		}
		
		return miniMap;
	}

	/**
	 * ｘ、ｙ地点を歩いたことにする。
	 * @param x
	 * @param y
	 */
	public void walked(int x, int y) {
		
		// 現在地(x,y)のフラグをONにする。
		walked[x + mapWidth * y] = true;
		
		// 上下左右がブロックならフラグをONにする。
		if(map[x - 1 + mapWidth * y] == BLOCK) {
			walked[x - 1 + mapWidth * y] = true;
		}
		if(map[x + 1 + mapWidth * y] == BLOCK) {
			walked[x + 1 + mapWidth * y] = true;
		}
		if(map[x + mapWidth * (y - 1)] == BLOCK) {
			walked[x + mapWidth * (y - 1)] = true;
		}
		if(map[x + mapWidth * (y + 1)] == BLOCK) {
			walked[x + mapWidth * (y + 1)] = true;
		}
		
		// 左と上がブロックなら左上のフラグON
		if(map[x - 1 + mapWidth * y] == BLOCK
		&& map[x + mapWidth * (y - 1)] == BLOCK) {
			walked[x - 1 + mapWidth * (y - 1)] = true;
		}
		// 右と上がブロックなら右上のフラグON
		if(map[x + 1 + mapWidth * y] == BLOCK
		&& map[x + mapWidth * (y - 1)] == BLOCK) {
			walked[x + 1 + mapWidth * (y - 1)] = true;
		}
		// 左と下がブロックなら左下のフラグON
		if(map[x - 1 + mapWidth * y] == BLOCK
		&& map[x + mapWidth * (y + 1)] == BLOCK) {
			walked[x - 1 + mapWidth * (y + 1)] = true;
		}
		// 右と下がブロックなら右下のフラグON
		if(map[x + 1 + mapWidth * y] == BLOCK
		&& map[x + mapWidth * (y + 1)] == BLOCK) {
			walked[x + 1 + mapWidth * (y + 1)] = true;
		}
	}
	
	/**
	 * イベント情報を取得する。
	 * @param x
	 * @param y
	 * @return
	 */
	public Event getEvent(int x, int y) {
		
		return event[x + mapWidth * y];
	}
	
	/**
	 * イベント情報をクリアする。
	 * @param x
	 * @param y
	 */
	public void clearEvent(int x, int y) {
		
		event[x + mapWidth * y] = Event.NONE;
	}
	
	/**
	 * オートマップ開示済みの合計箇所を返す。
	 * @return
	 */
	public int getWalkedTotal() {
		
		int total = 0;
		for(int y = 0; y < mapHeight; y++) {
			for(int x = 0; x < mapWidth; x++) {
				if(walked[x + mapWidth * y] == true) {
					total++;
				}
			}
		}
		return total;
	}
}
