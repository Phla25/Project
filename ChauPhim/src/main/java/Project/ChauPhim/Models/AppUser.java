package Project.ChauPhim.Models;

public abstract class AppUser {
	protected String username;
    protected String password;
    protected String email;
    public AppUser(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
