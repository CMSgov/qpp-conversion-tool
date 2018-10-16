package gov.cms.qpp.conversion.api.services;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.cms.qpp.conversion.api.model.Metadata;

public interface MetadataRepository extends CrudRepository<Metadata, String> {

	@Query("SELECT m FROM Metadata m WHERE (m.cpc IS TRUE) AND (m.cpcProcessed IS FALSE) AND (m.createDate >= ?1) ORDER BY m.createDate ASC")
	List<Metadata> getUnprocessedCpcPlusMetadata(Long createDate);

}
