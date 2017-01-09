
import java.util.Date;

import net.sourceforge.jwebunit.junit.WebTester;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

public abstract class Domain {

	public static class WrongLoginException extends Exception{private static final long serialVersionUID = 1L;};
	public static class NoTimeException extends Exception{private static final long serialVersionUID = 1L;};
	public static final int OK = 0;
	public static final int WRONG_LOGIN = 1;
	public static final int NO_TIME_VISIBLE = 2;
	public static final int NO_INTERNET = 3;
	public static final int WRONG_XPATH = 4;
	
	public static final int SEC_IN_MIN = 60;
	public static final int SEC_IN_HOUR = 3600;
	public static final int HOUR_IN_DAY = 24;
	public static final int MSEC_IN_SEC = 1000;
	public static final int MSEC_IN_MIN = 60000;
	private static WebTester browser;

	protected static WebTester getBrowser() {
		if (browser == null) {
			browser = new WebTester();
			browser.setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
		}
		return browser;
	}
	
	public static Domain parse (String s){
		Domain site = null;
		if(s == null) s = "";
		switch(s){
		case LimudNaim.domain:
			site = LimudNaim.getInstance();break;
		default:break;
		}
		return site;
	}
	
	public static String formatTime(Date date){ // format = hh:mm:ss
		return date.toString().split(" ")[3];
	}

	public static long futureTime(String future) { // future format = hh:mm:ss
		String[] futArr = future.split(":");
		String[] nowArr = new Date().toString().split(" ")[3].split(":");
		long futSec = Integer.parseInt(futArr[2]) + Integer.parseInt(futArr[1]) * SEC_IN_MIN
				+ Integer.parseInt(futArr[0]) * SEC_IN_HOUR;
		long nowSec = Integer.parseInt(nowArr[2]) + Integer.parseInt(nowArr[1]) * SEC_IN_MIN
				+ Integer.parseInt(nowArr[0]) * SEC_IN_HOUR;
		long secDiff = (Integer.parseInt(futArr[0]) < Integer.parseInt(nowArr[0])) ? 
				HOUR_IN_DAY * SEC_IN_HOUR + futSec - nowSec : futSec - nowSec;
		return new Date().getTime()
				+ (secDiff + org.apache.commons.lang3.RandomUtils.nextLong(1, 5 * SEC_IN_MIN)) * MSEC_IN_SEC;
	}

	public abstract int bump(User user);
}
