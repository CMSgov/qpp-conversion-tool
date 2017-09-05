package gov.cms.qpp.conversion.api.repositories;

import gov.cms.qpp.conversion.api.entities.QppFileEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QppFileRepository extends CrudRepository<QppFileEntity, String> {
}
