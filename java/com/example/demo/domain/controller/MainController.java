package com.example.demo.domain.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.domain.Dir;
import com.example.demo.domain.Dungeon;
import com.example.demo.domain.Event;
import com.example.demo.domain.Item;
import com.example.demo.domain.Player;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * コントローラークラス
 * @author T.Imura
 *
 */
@Controller
@RequestMapping("/maze")
@Slf4j
public class MainController {
	
	final static String VERSION = "1.1.0";
	
	// マップサイズの定義(必ず奇数)
	final static int MAP_WIDTH  = 17;
	final static int MAP_HEIGHT = 17;
	
	private Dungeon dungeon;
	private Player player;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private Item item;
	
	@Autowired
	private UserService userService;
	
	/**
	 * タイトル画面
	 * @return
	 */
	@GetMapping("")
	public String getTitle(
			Model model) {
		
		model.addAttribute("version", VERSION);
		
		return "title";
	}
	
	/**
	 * タイトル画面でStart押下
	 * @return
	 */
	@PostMapping("")
	public String postTitle() {
		
		// 迷路クラス生成。
		dungeon = new Dungeon(MAP_WIDTH, MAP_HEIGHT);
		
		// スタート地点を決める。(x、yは必ず奇数)
		int x = dungeon.randOdd(MAP_WIDTH - 1);
		int y = dungeon.randOdd(MAP_WIDTH - 1);
		
		// 迷路をランダム生成する。
		dungeon.init(x, y);
		
		// プレイヤークラス生成。
		player = new Player(x, y, Dir.NORTH);
		dungeon.walked(x, y);
		
		// 迷路画面へ遷移
		return "redirect:/maze/maze";
	}
	
	/**
	 * Maze(疑似3D)画面
	 * @param model
	 * @return
	 */
	@GetMapping("/maze")
	public String maze(
			Model model,
			Locale locale
			) {
		
		// Viewに位置と方向を設定する。
		dungeon.setView(player.getX(), player.getY(), player.getDir());
		
		// 画像ファイルと描画フラグを初期化。
		String area[] = {"", "", "", "", "", "", "", "", ""};
		boolean[] flg = {true, true, true, true, true, true, true, true, true};
		
		// エリア０の描画判断
		if(dungeon.isBlock(16)) {
			area[0] = "/img/14.wall_side_near_l_0.png";
		} else if(dungeon.isBlock(11)) {
			area[0] = "/img/01.wall_face_near_l_0.png";
		} else if(dungeon.isBlock(6)) {
			area[0] = "/img/04.wall_face_mid_l_0.png";
		}
		// エリア１の描画判断
		if(dungeon.isBlock(12)) {
			area[1] = "/img/02.wall_face_near_c_1-7.png";
			flg[2] = false;
			flg[3] = false;
			flg[4] = false;
			flg[5] = false;
			flg[6] = false;
			flg[7] = false;
		} else if(dungeon.isBlock(11)) {
			area[1] = "/img/16.wall_side_mid_l_1-2.png";
			flg[2] = false;
		} else if(dungeon.isBlock(6)) {
			area[1] = "/img/05.wall_face_mid_l_1-2.png";
			flg[2] = false;
		}
		// エリア２の描画判断
		if(flg[2]) {
			if(dungeon.isBlock(1)) {
				area[2] = "/img/09.wall_face_far_l_2.png";
			}
		}
		// エリア３の描画判断
		if(flg[3]) {
			if(dungeon.isBlock(7)) {
				area[3] = "/img/06.wall_face_mid_c_3-5.png";
				flg[4] = false;
				flg[5] = false;
			} else if(dungeon.isBlock(6)) {
				area[3] = "/img/20.wall_side_far_l_3.png";
			} else if(dungeon.isBlock(1)) {
				area[3] = "/img/10.wall_face_far_l_3.png";
			}
		}
		// エリア４の描画判断
		if(flg[4]) {
			if(dungeon.isBlock(2)) {
				area[4] = "/img/11.wall_face_far_c_4.png";
			}
		}
		// エリア５の描画判断
		if(flg[5]) {
			if(dungeon.isBlock(8)) {
				area[5] = "/img/21.wall_side_far_r_5.png";
			} else if(dungeon.isBlock(3)) {
				area[5] = "/img/12.wall_face_far_r_5.png";
			}
		}
		// エリア６の描画判断
		if(flg[6]) {
			if(dungeon.isBlock(13)) {
				area[6] = "/img/17.wall_side_mid_r_6-7.png";
				flg[7] = false;
			} else if(dungeon.isBlock(8)) {
				area[6] = "/img/07.wall_face_mid_r_6-7.png";
				flg[7] = false;
			} else if(dungeon.isBlock(3)) {
				area[6] = "/img/13.wall_face_far_r_6.png";
			}
		}
		// エリア７の描画判断
		// なし。
		
		// エリア８の描画判断
		if(flg[8]) {
			if(dungeon.isBlock(18)) {
				area[8] = "/img/15.wall_side_near_r_8.png";
			} else if(dungeon.isBlock(13)) {
				area[8] = "/img/03.wall_face_near_r_8.png";
			} else if(dungeon.isBlock(8)) {
				area[8] = "/img/08.wall_face_mid_r_8.png";
			}
		}
		
		// モデルに画像を設定する。
		model.addAttribute("area0", area[0]);
		model.addAttribute("area1", area[1]);
		model.addAttribute("area2", area[2]);
		model.addAttribute("area3", area[3]);
		model.addAttribute("area4", area[4]);
		model.addAttribute("area5", area[5]);
		model.addAttribute("area6", area[6]);
		model.addAttribute("area7", area[7]);
		model.addAttribute("area8", area[8]);
		model.addAttribute("dir", player.getDir());
		
		// スタミナ表示
		String message;
		message = messageSource.getMessage("stamina",
				new String[] {Integer.toString(player.getStamina())}, locale);
		model.addAttribute("stamina", message);
		
		// スコア表示
		message = messageSource.getMessage("pts",
				new String[] {Integer.toString(player.getScore())}, locale);
		model.addAttribute("score", message);
		
		// バージョン表示
		model.addAttribute("version", VERSION);
		
		return "maze";
	}
	
	/**
	 * 前に進む。
	 * @param model
	 * @return
	 */
	@PostMapping(value="/maze", params="forward")
	public String forward(
			Model model,
			Locale locale) {
		log.info("----- forward() -----");
		
		// １歩進むことができる場合
		if(dungeon.isAdvanceable(player.getX(), player.getY(), player.getDir())) {
			// 進む。
			player.step();
			
			// オートマップを更新。
			dungeon.walked(player.getX(), player.getY());
			
			// 「スタミナが尽きた！」「Game Over」ボタン表示
			if(player.getStamina() <= 0) {
				String message = messageSource.getMessage("outOfStamina", null, locale);
				model.addAttribute("msg", message);
				/*
				 * inGameBtn使用箇所
				 */
				model.addAttribute("inGameBtn", "Game Over");
				model.addAttribute("disabled", "true");
			}
			else {
				// イベント処理。
				eventProcess(player.getX(), player.getY(), model, locale);
			}
		}
		
		return maze(model, locale);
	}
	
	/**
	 * イベント処理
	 * @param x
	 * @param y
	 * @param model
	 */
	private void eventProcess(int x, int y, Model model, Locale locale) {
		
		String message;
		
		switch(dungeon.getEvent(x, y)) {
		case NONE:
		case START:
			// なにもしない。
			break;
		case GOAL:
			// カギを入手済みの場合
			if(player.isKey()) {
				message = messageSource.getMessage("goal", null, locale);
				model.addAttribute("msg", message);
				/*
				 * inGameBtn使用箇所
				 * モーダルウィンドウのボタンをView Resultにする？
				 * inGameBtnをもうちょい上に表示する？
				 */
				model.addAttribute("inGameBtn", "View Results");
				model.addAttribute("disabled", "true");
				// ゴールフラグON
				player.setGoal(true);
			}
			// カギを未入手の場合
			else {
				message = messageSource.getMessage("noKey", null, locale);
				model.addAttribute("msg", message);
			}
			break;
		case KEY:
			message = messageSource.getMessage("getKey", null, locale);
			model.addAttribute("msg", message);
			// カギ入手フラグON
			player.setKey(true);
			dungeon.clearEvent(x, y);
			break;
		case HIGH_STAMINA:
			message = messageSource.getMessage("heal", new String[] {"100"}, locale);
			model.addAttribute("msg", message);
			// 100pts回復
			player.heal(100);
			dungeon.clearEvent(x, y);
			break;
		case STAMINA:
			message = messageSource.getMessage("heal", new String[] {"30"}, locale);
			model.addAttribute("msg", message);
			// 30pts回復
			player.heal(30);
			dungeon.clearEvent(x, y);
			break;
		case DAMAGE:
			message = messageSource.getMessage("damage", new String[] {"50"}, locale);
			model.addAttribute("msg", message);
			// 50ptsダメージ
			player.damage(50);
			dungeon.clearEvent(x, y);
			break;
		default:	// アイテム取得処理
			Event ev = dungeon.getEvent(x, y);
			
			// "～を手にいれた！"表示
			int pts = item.getPts(ev);
			message = messageSource.getMessage("getItem",
					new String[] {item.getName(ev), Integer.toString(pts)}, locale);
			model.addAttribute("msg", message);
			
			// ポイント加算
			player.addScore(pts);
			dungeon.clearEvent(x, y);
			break;
		}
	}
	
	/**
	 * 左を向く。
	 * @param model
	 * @return
	 */
	@PostMapping(value="/maze", params="left")
	public String left(
			Model model,
			Locale locale) {
		log.info("----- left() -----");
		
		player.turnLeft();
		
		return maze(model, locale);
	}
	
	/**
	 * 右を向く。
	 * @param model
	 * @return
	 */
	@PostMapping(value="/maze", params="right")
	public String right(
			Model model,
			Locale locale) {
		log.info("----- right() -----");
		
		player.turnRight();
		
		return maze(model, locale);
	}
	
	/**
	 * 後ろを向く。
	 * @param model
	 * @return
	 */
	@PostMapping(value="/maze", params="turn")
	public String turn(
			Model model,
			Locale locale) {
		log.info("----- turn() -----");
		
		player.turn();
		
		return maze(model, locale);
	}
	
	/**
	 * Exitボタン押下
	 * @param model
	 * @return
	 */
	@PostMapping(value="/maze", params="exit")
	public String exit() {
		
		// リザルト画面へ遷移
		return "redirect:/maze/results";
	}
	
	/**
	 * Map(ミニマップ)画面
	 * @param model
	 * @return
	 */
	@GetMapping("/map")
	public String map(Model model) {
		log.info("----- map() -----");
		
		// マップ情報を取得してモデルに設定する。
		int[] miniMap = dungeon.getMiniMap(player.getX(), player.getY());
		model.addAttribute("miniMap", miniMap);
		
		// 方向の画像を設定する。
		String dirPng = "";
		switch(player.getDir()) {
		case NORTH:
			dirPng = "/img/north.png";
			break;
		case EAST:
			dirPng = "/img/east.png";
			break;
		case SOUTH:
			dirPng = "/img/south.png";
			break;
		case WEST:
			dirPng = "/img/west.png";
			break;
		}
		model.addAttribute("dirPng", dirPng);
		
		model.addAttribute("version", VERSION);
		
		return "map";
	}
	
	/**
	 * メニュー画面
	 * @param model
	 * @return
	 */
	@GetMapping("/menu")
	public String menu(
			Model model,
			Locale locale
			) {
		String str = "";
		
		// Ｘ座標、Ｙ座標
		str = messageSource.getMessage("menuPos",
				new String[] {Integer.toString(player.getX()), Integer.toString(player.getY())}, locale);
		model.addAttribute("pos", str);
		// 歩数
		str = messageSource.getMessage("menuSteps",
				new String[] {Integer.toString(player.getSteps())}, locale);
		model.addAttribute("steps", str);
		// スタミナ
		str = messageSource.getMessage("menuStamina",
				new String[] {Integer.toString(player.getStamina())}, locale);
		model.addAttribute("stamina", str);
		// スコア
		str = messageSource.getMessage("menuScore",
				new String[] {Integer.toString(player.getScore())}, locale);
		model.addAttribute("score", str);
		
		// カギ入手状況
		if(player.isKey()) {
			str = messageSource.getMessage("menuKey", null, locale);
		} else {
			str = messageSource.getMessage("menuNoKey", null, locale);
		}
		model.addAttribute("key", str);
		
		model.addAttribute("version", VERSION);
		
		return "menu";
	}
	
	/**
	 * メニュー画面　Give Upボタン押下
	 * @param model
	 * @return
	 */
	@PostMapping("/menu")
	public String giveup(
			Model model
			) {
		
		// タイトル画面へ遷移
		return "redirect:/maze";
	}
	
	/**
	 * リザルト画面
	 * @param model
	 * @param locale
	 * @return
	 */
	@GetMapping("/results")
	public String results(
			Model model,
			Locale locale) {
		
		String str = "";
		
		// 取得アイテムポイント
		int itemScore = player.getScore();
		str = messageSource.getMessage("resultItem",
				new String[] {Integer.toString(itemScore)}, locale);
		model.addAttribute("itemScore", str);
		
		// 残スタミナ＊100ポイント
		int staminaScore = player.getStamina() * 100;
		str = messageSource.getMessage("resultStamina",
				new String[] {Integer.toString(staminaScore)}, locale);
		model.addAttribute("staminaScore", str);
		
		// 踏破率＊100ポイント
		int walkedScore = dungeon.getWalkedTotal() * 100;
		str = messageSource.getMessage("resultWalked",
				new String[] {Integer.toString(walkedScore)}, locale);
		model.addAttribute("walkedScore", str);
		
		// カギ入手
		int keyScore = 0;
		if(player.isKey()) {
			keyScore = 5000;
			str = messageSource.getMessage("resultKey",
					new String[] {Integer.toString(keyScore)}, locale);
			model.addAttribute("keyScore", str);
		}
		
		// ゴール到達
		int goalScore = 0;
		if(player.isGoal()) {
			goalScore = 10000;
			str = messageSource.getMessage("resultGoal",
					new String[] {Integer.toString(goalScore)}, locale);
			model.addAttribute("goalScore", str);
		}
		
		// トータルスコア
		int totalScore = itemScore + staminaScore + walkedScore + keyScore + goalScore;
		model.addAttribute("totalScore", totalScore);
		
		// DBからランキングを取得する。
		List<User> best5List = userService.getBest5();
		
		// ランキング内に入っているかチェックする。
		int rank = 1;
		for(User user: best5List) {
			if(totalScore >= user.getScore()) {
				// 名前入力テキストボックスを表示する。
				model.addAttribute("rankIn", "true");
				// プレイヤーの順位
				model.addAttribute("rank", rank);
				break;
			}
			rank++;
		}
		
		// バージョン表示
		model.addAttribute("version", VERSION);
		
		return "results";
	}
	
	/**
	 * リザルト画面　Finボタン押下
	 * @param model
	 * @return
	 */
	@PostMapping("/results")
	public String fin(
			@RequestParam(name="name", required=false) String name,
			@RequestParam(name="score", required=false) String score,
			@RequestParam(name="number", required=false) String number,
			RedirectAttributes redirectAttributes
			) {
		
		// ランキング内にはいった場合
		if(name != null) {
			
			// DBを更新する。
			Date date = new Date();
			userService.addNewRanker(name, Integer.parseInt(score), date);
			
			// リダイレクト先にパラメータを送る。
			redirectAttributes.addFlashAttribute("number", number);
		}
		
		// ランキング画面へ遷移
		return "redirect:/maze/ranking";
	}
	
	/**
	 * ランキング画面
	 * @param model
	 * @param locale
	 * @return
	 */
	@GetMapping("/ranking")
	public String ranking(
			Model model,
			Locale locale) {
		log.info("----- fin() -----");
		
		// DBからランキングを取得し、Modelに登録する。
		List<User> best5List = userService.getBest5();
		model.addAttribute("ranking", best5List);
		
		// ランキングリストを作成し、Modelに登録する。
		List<String> rankList = new ArrayList<String>();
		int index = 1;
		for(User user : best5List) {
			String rank = "";
			switch(index) {
			case 1:
				rank = "1st";
				break;
			case 2:
				rank = "2nd";
				break;
			case 3:
				rank = "3rd";
				break;
			default:
				rank = index + "th";
			}
			String str = messageSource.getMessage("rank",
					new String[] {rank, user.getName(), Integer.toString(user.getScore())}, locale);
			
			rankList.add(str);
			index++;
		}
		model.addAttribute("rankList", rankList);
		
		model.addAttribute("version", VERSION);
		
		// ランキング画面を表示
		return "ranking";
	}
	
	/**
	 * ランキング画面　Go To Titleボタン押下
	 * @param model
	 * @return
	 */
	@PostMapping("/ranking")
	public String ranking() {
		
		// タイトル画面へ遷移
		return "redirect:/maze";
	}
}
