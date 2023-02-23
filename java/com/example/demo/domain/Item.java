package com.example.demo.domain;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * アイテムクラス
 * @author T.Imura
 *
 */
@Component
public class Item {
	
	@Autowired
	MessageSource messageSource;
	
	/**
	 * アイテムの名前を取得する。
	 * @param ev
	 * @return
	 */
	public String getName(Event ev) {
		
		String name = "";
		
		switch(ev) {
		case SWORD1:
			name = messageSource.getMessage("sword1", null, Locale.JAPAN);
			break;
		case JEWEL1:
			name = messageSource.getMessage("jewel1", null, Locale.JAPAN);
			break;
		case JEWEL2:
			name = messageSource.getMessage("jewel2", null, Locale.JAPAN);
			break;
		case JEWEL3:
			name = messageSource.getMessage("jewel3", null, Locale.JAPAN);
			break;
		case JEWEL4:
			name = messageSource.getMessage("jewel4", null, Locale.JAPAN);
			break;
		case JEWEL5:
			name = messageSource.getMessage("jewel5", null, Locale.JAPAN);
			break;
		case ITEM1:
			name = messageSource.getMessage("item1", null, Locale.JAPAN);
			break;
		case ITEM2:
			name = messageSource.getMessage("item2", null, Locale.JAPAN);
			break;
		case ITEM3:
			name = messageSource.getMessage("item3", null, Locale.JAPAN);
			break;
		case ITEM4:
			name = messageSource.getMessage("item4", null, Locale.JAPAN);
			break;
		case ITEM5:
			name = messageSource.getMessage("item5", null, Locale.JAPAN);
			break;
		case ITEM6:
			name = messageSource.getMessage("item6", null, Locale.JAPAN);
			break;
		case ITEM7:
			name = messageSource.getMessage("item7", null, Locale.JAPAN);
			break;
		case ITEM8:
			name = messageSource.getMessage("item8", null, Locale.JAPAN);
			break;
		case ITEM9:
			name = messageSource.getMessage("item9", null, Locale.JAPAN);
			break;
		case ITEM10:
			name = messageSource.getMessage("item10", null, Locale.JAPAN);
			break;
		default:
			break;
		}
		
		return name;
	}
	
	/**
	 * アイテムの得点を取得する。
	 * @param ev
	 * @return
	 */
	public int getPts(Event ev) {
		int pts = 0;
		
		switch(ev) {
		case SWORD1:
			pts = 5000;
			break;
		case JEWEL1:
			pts = 2000;
			break;
		case JEWEL2:
		case JEWEL3:
		case JEWEL4:
		case JEWEL5:
			pts = 1000;
			break;
		case ITEM1:
		case ITEM2:
		case ITEM3:
		case ITEM4:
		case ITEM5:
		case ITEM6:
		case ITEM7:
		case ITEM8:
		case ITEM9:
		case ITEM10:
			pts = 500;
			break;
		default:
			break;
		}
		
		return pts;
	}
}
