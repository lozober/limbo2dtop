

public class User implements Comparable<User> {

	private String login;
	private String pw;
	private long time;
	private Domain site;
	private String name;

	public User(String login, String pw, Domain site) {
		this.login = login;
		this.pw = pw;
		this.time = 0;
		this.site = site;
		this.name = null;
	}
	
	public User(User user){
		this.login = user.login;
		this.pw = user.pw;
		this.time = user.time;
		this.site = user.site;
		this.name = user.name;
	}

	public String getLogin() {
		return login;
	}

	public String getPw() {
		return pw;
	}

	public long getTime() {
		return time;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Domain getSite() {
		return site;
	}

	public int compareTo(User another) {
		if (this.getTime() < another.getTime()) {
			return -1;
		}
		if (this.getTime() > another.getTime()) {
			return 1;
		}
		return 0;
	}
}
