package model;

public class EnvironmentDataHolder {
	String serverUrl;
	String serverCookie;
	String hivvsUser;
	String hivvsPass;

	public EnvironmentDataHolder() {

	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(final String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getServerCookie() {
		return serverCookie;
	}

	public void setServerCookie(final String serverCookie) {
		this.serverCookie = serverCookie;
	}

	public String getHivvsUser() {
		return hivvsUser;
	}

	public void setHivvsUser(final String hivvsUser) {
		this.hivvsUser = hivvsUser;
	}

	public String getHivvsPass() {
		return hivvsPass;
	}

	public void setHivvsPass(final String hivvsPass) {
		this.hivvsPass = hivvsPass;
	}
}
