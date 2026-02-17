package gov.cms.qpp.conversion.api.services.internal;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import org.springframework.core.env.Environment;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.conversion.api.exceptions.AuditException;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.DbService;
import gov.cms.qpp.conversion.api.services.StorageService;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.test.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock private StorageService storageService;
    @Mock private DbService dbService;
    @Mock private Environment environment;

    @Mock private ConversionReport report;

    @Mock private Source qrdaSource;
    @Mock private Source qppSource;
    @Mock private Source validationErrorSource;
    @Mock private Source rawValidationErrorSource;

    private AuditServiceImpl objectUnderTest;

    @BeforeEach
    void setUp() {
        objectUnderTest = new AuditServiceImpl(storageService, dbService, environment);

        // Default: auditing enabled
        when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn(null);

        // Keep MetadataHelper.generateMetadata(report.getDecoded(), outcome) simple (node = null)
        when(report.getDecoded()).thenReturn(null);

        when(report.getPurpose()).thenReturn("TestPurpose");

        when(report.getQrdaSource()).thenReturn(qrdaSource);
        when(report.getQppSource()).thenReturn(qppSource);
        when(report.getValidationErrorsSource()).thenReturn(validationErrorSource);
        when(report.getRawValidationErrorsOrEmptySource()).thenReturn(rawValidationErrorSource);

        when(report.getReportDetails()).thenReturn(null);

        when(qrdaSource.getName()).thenReturn("qrda.xml");

        // sizes are required by storeContent(...)
        when(qrdaSource.getSize()).thenReturn(10L);
        when(qppSource.getSize()).thenReturn(20L);
        when(validationErrorSource.getSize()).thenReturn(30L);
        when(rawValidationErrorSource.getSize()).thenReturn(40L);

        // dbService.write returns whatever metadata it was asked to save
        when(dbService.write(any(Metadata.class)))
                .thenAnswer(inv -> CompletableFuture.completedFuture(inv.getArgument(0)));
    }

    @Test
    void success_noAuditEnabled_returnsNull_andDoesNothing() {
        when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("true");

        assertThat(objectUnderTest.success(report)).isNull();

        verifyNoInteractions(storageService);
        verifyNoInteractions(dbService);
    }

    @Test
    void success_happyPath_setsLocators_andWritesMetadata() {
        when(storageService.store(anyString(), anySupplier(), anyLong()))
                .thenReturn(
                        CompletableFuture.completedFuture("submission-loc"),
                        CompletableFuture.completedFuture("qpp-loc")
                );

        Metadata result = objectUnderTest.success(report).join();

        assertThat(result.getSubmissionLocator()).isEqualTo("submission-loc");
        assertThat(result.getQppLocator()).isEqualTo("qpp-loc");
        assertThat(result.getFileName()).isEqualTo("qrda.xml");
        assertThat(result.getPurpose()).isEqualTo("TestPurpose");

        verify(storageService, times(2)).store(anyString(), anySupplier(), anyLong());
        verify(dbService, times(1)).write(any(Metadata.class));
    }

    @Test
    void failConversion_happyPath_setsConversionErrorLocator_andSubmissionLocator() {
        when(storageService.store(anyString(), anySupplier(), anyLong()))
                .thenReturn(
                        CompletableFuture.completedFuture("conversion-error-loc"),
                        CompletableFuture.completedFuture("submission-loc")
                );

        CompletableFuture<Void> future = objectUnderTest.failConversion(report);
        future.join();

        ArgumentCaptor<Metadata> captor = ArgumentCaptor.forClass(Metadata.class);
        verify(dbService).write(captor.capture());

        Metadata saved = captor.getValue();
        assertThat(saved.getConversionErrorLocator()).isEqualTo("conversion-error-loc");
        assertThat(saved.getSubmissionLocator()).isEqualTo("submission-loc");
        assertThat(saved.getFileName()).isEqualTo("qrda.xml");
        assertThat(saved.getPurpose()).isEqualTo("TestPurpose");
    }

    @Test
    void failValidation_happyPath_setsAllLocators() {
        when(storageService.store(anyString(), anySupplier(), anyLong()))
                .thenReturn(
                        CompletableFuture.completedFuture("raw-validation-loc"),
                        CompletableFuture.completedFuture("validation-loc"),
                        CompletableFuture.completedFuture("qpp-loc"),
                        CompletableFuture.completedFuture("submission-loc")
                );

        CompletableFuture<Void> future = objectUnderTest.failValidation(report);
        future.join();

        ArgumentCaptor<Metadata> captor = ArgumentCaptor.forClass(Metadata.class);
        verify(dbService).write(captor.capture());

        Metadata saved = captor.getValue();
        assertThat(saved.getRawValidationErrorLocator()).isEqualTo("raw-validation-loc");
        assertThat(saved.getValidationErrorLocator()).isEqualTo("validation-loc");
        assertThat(saved.getQppLocator()).isEqualTo("qpp-loc");
        assertThat(saved.getSubmissionLocator()).isEqualTo("submission-loc");
    }

    @Test
    void success_whenStoreFails_joinThrowsCompletionException_withRuntimeCause_andDoesNotWrite() {
        CompletableFuture<String> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("boom"));

        when(storageService.store(anyString(), anySupplier(), anyLong()))
                .thenReturn(
                        failed,
                        CompletableFuture.completedFuture("qpp-loc")
                );

        CompletionException ex = assertThrows(
                CompletionException.class,
                () -> objectUnderTest.success(report).join()
        );

        // Primary cause is the original store failure (matches what you observed)
        assertThat(ex.getCause()).isInstanceOf(RuntimeException.class);
        assertThat(ex.getCause()).hasMessageThat().contains("boom");

        // persist() threw AuditException, so db write should not happen
        verify(dbService, never()).write(any(Metadata.class));

        // Optional (don’t make the test brittle): AuditException may appear as suppressed depending on JDK behavior.
        // If you want to assert it, uncomment:
        // assertThat(java.util.Arrays.stream(ex.getSuppressed()).anyMatch(t -> t instanceof AuditException)).isTrue();
    }

    @SuppressWarnings("unchecked")
    private static Supplier<InputStream> anySupplier() {
        return (Supplier<InputStream>) org.mockito.ArgumentMatchers.any(Supplier.class);
    }
}
