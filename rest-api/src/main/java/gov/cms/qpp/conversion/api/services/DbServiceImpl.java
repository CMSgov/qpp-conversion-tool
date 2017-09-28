package gov.cms.qpp.conversion.api.services;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import gov.cms.qpp.conversion.api.model.Metadata;
import org.springframework.beans.factory.annotation.Autowired;

public class DbServiceImpl extends InOrderAsyncActionService<Metadata, Metadata>
		implements DbService {

	@Autowired
	private DynamoDBMapper mapper;

	public Metadata write(Metadata meta) {
		return asynchronousAction(meta);
	}

	@Override
	protected Metadata asynchronousAction(Metadata meta) {
		mapper.save(meta);
		return meta;
	}
}
