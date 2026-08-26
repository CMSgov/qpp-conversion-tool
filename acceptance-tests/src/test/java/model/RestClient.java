package model;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestClient extends RestTemplate {

	public RestClient(String username, String password) {
		BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
			new AuthScope(null, -1),
			new UsernamePasswordCredentials(username, password.toCharArray()));
		HttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
	}
}
