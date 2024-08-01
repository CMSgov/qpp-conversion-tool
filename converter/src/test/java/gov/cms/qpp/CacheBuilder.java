package gov.cms.qpp;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import gov.cms.qpp.model.CacheType;

import java.util.concurrent.TimeUnit;

public class CacheBuilder {
    private static Cache<CacheType, ApmEntityIds> entityIdsCache;

    CacheBuilder() {}

    public static void buildEntityIdsCache() {
        entityIdsCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();
    }

    public static ApmEntityIds getEntityIds(CacheType value) {
        if (entityIdsCache == null) buildEntityIdsCache();
        if (entityIdsCache.getIfPresent(value) == null) {
            ApmEntityIds entityData = null;
            switch(value) {
                case ApmEntityId -> entityData = new ApmEntityIds("test_apm_entity_ids.json");
                case ApmEntityIds -> entityData = new ApmEntityIds("test_apm_entity_ids.json","test_apm_entity_ids.json");
                case ApmPcfEntityIds -> entityData = new ApmEntityIds("test_apm_entity_ids.json","test_pcf_apm_entity_ids.json");
            }
            entityIdsCache.put(value, entityData);
        }
        return entityIdsCache.getIfPresent(value);
    }
}
