

public class LimudNaim extends Domain {

	private static final String baseURL = "http://www.limudnaim.co.il.";
	public static final String domain = "LimudNaim";
	private static LimudNaim instance = null;
	
	private LimudNaim(){}

	public static LimudNaim getInstance() {
		if (instance == null) {instance = new LimudNaim();}
		return instance;
	}
	
	@Override
	public int bump(User user) {
		getBrowser().setBaseUrl(baseURL);
		try{
			login(user.getLogin(), user.getPw());
			if (!isProfilePage()){throw new WrongLoginException();}
			if(user.getName() == null){user.setName(getUsername());}
			if(hasBumpButton()){
				getBrowser().clickLinkWithExactText("הקפצת פרופיל");
				getBrowser().closeBrowser();
				login(user.getLogin(), user.getPw());
			}
			if(!hasNewTime()){throw new NoTimeException();}
			user.setTime(futureTime(getNewTime()));
			return Domain.OK;
			
		}catch(WrongLoginException e){
			return Domain.WRONG_LOGIN;
		}catch(NoTimeException e){
			return Domain.NO_TIME_VISIBLE;
		}catch(Exception e){
			return Domain.NO_INTERNET;
		}catch(AssertionError e){
			return Domain.WRONG_XPATH;
		}finally{
			getBrowser().closeBrowser();
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return domain;
	}

	private boolean isProfilePage() {
		return getBrowser().hasElementsByXPath(
				//"//*[contains(text(),'פרופיל מורה פרטי') and contains(@id, content-profile-title-teacher_profile)]"
				"//*[contains(@title,'View member profile')]"
				);
	}
	
	private boolean hasBumpButton() {
		return getBrowser().hasElementsByXPath("//*[contains(text(),'הקפצת פרופיל')]");
	}
	
	private boolean hasNewTime() {
		return getBrowser().hasElementsByXPath("//*[contains(text(),'ניתן להקפיץ את הפרופיל בשעה')]");
	}
	
	private String getNewTime() {
		String[] data = getBrowser().getElementTextByXPath("//*[contains(text(),'ניתן להקפיץ את הפרופיל בשעה')]").split(" ");
		return data[data.length - 1] + ":59";
	}
	
	private String getUsername() {
		return getBrowser().getElementTextByXPath("//*[contains(@title,'View member profile')]");
	}

	private void login(String login, String pw) {
		getBrowser().beginAt("/user");
		getBrowser().setTextField("name", login);
		getBrowser().setTextField("pass", pw);
		getBrowser().submit("op");
	}
}