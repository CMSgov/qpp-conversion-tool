package gov.cms.qpp.conversion.api.helper;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.model.UnprocessedFileData;

class AdvancedApmHelperTest {

    @AfterEach
    void tearDown() {
        System.clearProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE);
    }

    @Test
    void isPcfFile_nullMetadata_false() {
        assertThat(AdvancedApmHelper.isPcfFile(null)).isFalse();
    }

    @Test
    void isPcfFile_pcfNull_false() {
        Metadata m = new Metadata();
        m.setPcf(null);

        assertThat(AdvancedApmHelper.isPcfFile(m)).isFalse();
    }

    @Test
    void isPcfFile_pcfSet_true() {
        Metadata m = new Metadata();
        m.setPcf("PCF");

        assertThat(AdvancedApmHelper.isPcfFile(m)).isTrue();
    }

    @Test
    void isAValidUnprocessedFile_notPcf_false() {
        Metadata m = new Metadata();
        m.setPcf(null);
        m.setCpcProcessed(false);
        m.setRtiProcessed(false);

        assertThat(AdvancedApmHelper.isAValidUnprocessedFile(m)).isFalse();
    }

    @Test
    void isAValidUnprocessedFile_pcf_andEitherFlagFalse_true() {
        Metadata m = new Metadata();
        m.setPcf("PCF");
        m.setCpcProcessed(true);
        m.setRtiProcessed(false);

        assertThat(AdvancedApmHelper.isAValidUnprocessedFile(m)).isTrue();
    }

    @Test
    void isAValidUnprocessedFile_pcf_andBothTrue_false() {
        Metadata m = new Metadata();
        m.setPcf("PCF");
        m.setCpcProcessed(true);
        m.setRtiProcessed(true);

        assertThat(AdvancedApmHelper.isAValidUnprocessedFile(m)).isFalse();
    }

    @Test
    void transformMetaDataToUnprocessedFileData_mapsAllItems() {
        Metadata m1 = new Metadata();
        Metadata m2 = new Metadata();

        List<UnprocessedFileData> result =
                AdvancedApmHelper.transformMetaDataToUnprocessedFileData(Arrays.asList(m1, m2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isInstanceOf(UnprocessedFileData.class);
        assertThat(result.get(1)).isInstanceOf(UnprocessedFileData.class);
    }

    @Test
    void blockAdvancedApmApis_whenEnvVarNotPresent_false() {
        System.clearProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE);

        assertThat(AdvancedApmHelper.blockAdvancedApmApis()).isFalse();
    }

    @Test
    void blockAdvancedApmApis_whenEnvVarPresent_true() {
        System.setProperty(Constants.NO_CPC_PLUS_API_ENV_VARIABLE, "true");

        assertThat(AdvancedApmHelper.blockAdvancedApmApis()).isTrue();
    }
}
