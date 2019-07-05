package mocean.logs.domain;

import java.io.Serializable;

public class UserBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String username;
	public String password;
	public String registertime;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRegistertime() {
		return registertime;
	}
	public void setRegistertime(String registertime) {
		this.registertime = registertime;
	}
	
	

}
