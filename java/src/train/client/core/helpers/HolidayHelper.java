/*******************************************************************************
 * Copyright (c) 2013 Mrbrutal. All rights reserved.
 * 
 * @name Traincraft
 * @author Mrbrutal
 ******************************************************************************/

package src.train.client.core.helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class HolidayHelper extends Thread {

	private static boolean checkBoth = false;
	private static boolean isChristmas = false;

	public static boolean isCheckBoth() {
		return checkBoth;
	}

	public static boolean isChristmas() {
		return isChristmas;
	}

	@Override
	public void run() {
		try {
			URL url = new URL("https://dl.dropbox.com/u/25602663/Traincraft/Holidays/holidays.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			int sub = 3;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("CB: ")) {
					if (line.contains("true")) {
						checkBoth = true;
					}
					else {
						checkBoth = false;
					}
				}
				else if (line.startsWith("IC: ")) {
					if (line.contains("true")) {
						isChristmas = true;
					}
					else {
						isChristmas = false;
					}
				}
			}
			reader.close();
		} catch (Exception e) {}
	}
	
	public static boolean isHoliday() {
		Calendar cal = Calendar.getInstance();
		if(isCheckBoth()) {
			if(isChristmas()) {
				if(cal.get(Calendar.MONTH) == Calendar.DECEMBER && (cal.get(Calendar.DATE)>=5 && cal.get(Calendar.DATE)<=31)) {
					return true;
				}
			}
		}
		else {
			if(cal.get(Calendar.MONTH) == Calendar.DECEMBER && (cal.get(Calendar.DATE)>=5 && cal.get(Calendar.DATE)<=31)) {
				return true;
			}
		}
		return false;
	}
}
