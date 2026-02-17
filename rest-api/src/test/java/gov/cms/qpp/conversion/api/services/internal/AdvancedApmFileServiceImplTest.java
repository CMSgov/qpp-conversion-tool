package gov.cms.qpp.conversion.api.services.internal;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import gov.cms.qpp.conversion.api.exceptions.InvalidFileTypeException;
import gov.cms.qpp.conversion.api.exceptions.NoFileInDatabaseException;
import gov.cms.qpp.conversion.api.helper.AdvancedApmHelper;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.FileStatusUpdateRequest;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.DbService;
import gov.cms.qpp.conversion.api.services.StorageService;
import gov.cms.qpp.test.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdvancedApmFileServiceImplTest {

    @Mock
    private DbService dbService;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private AdvancedApmFileServiceImpl objectUnderTest;

    @Test
    void getMetadataById_nullMetadata_throwsNoFileInDatabase() {
        when(dbService.getMetadataById("id1")).thenReturn(null);

        assertThrows(NoFileInDatabaseException.class, () -> objectUnderTest.getMetadataById("id1"));
    }

    @Test
    void getMetadataById_notPcf_throwsInvalidFileType() {
        Metadata metadata = new Metadata();

        when(dbService.getMetadataById("id1")).thenReturn(metadata);

        assertThrows(InvalidFileTypeException.class, () -> objectUnderTest.getMetadataById("id1"));
    }

    @Test
    void getMetadataById_validPcf_returnsMetadata() {
        Metadata metadata = new Metadata();
        metadata.setPcf("PCF_0");

        when(dbService.getMetadataById("id1")).thenReturn(metadata);

        Metadata result = objectUnderTest.getMetadataById("id1");

        assertThat(result).isSameInstanceAs(metadata);
    }

    @Test
    void updateFileStatus_processedFalse_unprocessesForCpc() {
        Metadata metadata = new Metadata();
        metadata.setPcf("PCF_0");

        when(dbService.getMetadataById("file1")).thenReturn(metadata);
        when(dbService.write(any(Metadata.class))).thenReturn(CompletableFuture.completedFuture(metadata));

        FileStatusUpdateRequest req = new FileStatusUpdateRequest();
        req.setProcessed(false);

        String msg = objectUnderTest.updateFileStatus("file1", Constants.CPC_ORG, req);

        assertThat(metadata.getCpcProcessed()).isFalse();
        assertThat(msg).isEqualTo(AdvancedApmHelper.FILE_FOUND_UNPROCESSED);
        verify(dbService).write(any(Metadata.class));
    }

    @Test
    void updateFileStatus_processedTrue_processesForRti() {
        Metadata metadata = new Metadata();
        metadata.setPcf("PCF_0");

        when(dbService.getMetadataById("file1")).thenReturn(metadata);
        when(dbService.write(any(Metadata.class))).thenReturn(CompletableFuture.completedFuture(metadata));

        FileStatusUpdateRequest req = new FileStatusUpdateRequest();
        req.setProcessed(true);

        String msg = objectUnderTest.updateFileStatus("file1", Constants.RTI_ORG, req);

        assertThat(metadata.getRtiProcessed()).isTrue();
        assertThat(msg).isEqualTo(AdvancedApmHelper.FILE_FOUND_PROCESSED);
        verify(dbService).write(any(Metadata.class));
    }

    @Test
    void updateFileStatus_unknownOrg_returnsNotFound_andDoesNotWrite() {
        Metadata metadata = new Metadata();
        metadata.setPcf("PCF_0");

        when(dbService.getMetadataById("file1")).thenReturn(metadata);

        FileStatusUpdateRequest req = new FileStatusUpdateRequest();
        req.setProcessed(true);

        String msg = objectUnderTest.updateFileStatus("file1", "SOME_OTHER_ORG", req);

        assertThat(msg).isEqualTo(AdvancedApmHelper.FILE_NOT_FOUND);
        verify(dbService, never()).write(any(Metadata.class));
    }
}
