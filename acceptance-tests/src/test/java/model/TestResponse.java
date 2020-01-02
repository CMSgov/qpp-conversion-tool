package model;

public class TestResponse {
	int status;
	String jsonResponse;

	public TestResponse() {
	}

	public int getStatus(){
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getJsonResponse() {
		return jsonResponse;
	}

	public void setJsonResponse(final String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}
}
