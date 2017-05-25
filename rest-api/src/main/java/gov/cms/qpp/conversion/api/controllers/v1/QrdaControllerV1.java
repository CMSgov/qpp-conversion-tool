package gov.cms.qpp.conversion.api.controllers.v1;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/qrda3")
@CrossOrigin
public class QrdaControllerV1 {

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Map<String, String> createResource(@RequestBody final Map<String, String> qrdaRequest) {
		qrdaRequest.put("received", "true");
		return qrdaRequest;
	}
}
