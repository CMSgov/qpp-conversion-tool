package model;

import org.openqa.selenium.NotFoundException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

public class ErrorHandler implements ResponseErrorHandler {
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return response.getStatusCode().is4xxClientError()
			|| response.getStatusCode().is5xxServerError();
	}

	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse httpResponse)
		throws IOException {

		if (httpResponse.getStatusCode().is5xxServerError()) {
		} else if (httpResponse.getStatusCode().is4xxClientError()) {
			if (httpResponse.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
				throw new NotFoundException();
			}
		}
	}
}
