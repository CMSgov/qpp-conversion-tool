package model;

public class EnvironmentDataHolder {
	String serverUrl;
	String serverCookie;
	String environment;

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

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(final String environment) {
		this.environment = environment;
	}
}
