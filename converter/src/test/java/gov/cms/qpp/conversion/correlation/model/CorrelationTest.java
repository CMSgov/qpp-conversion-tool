package gov.cms.qpp.conversion.correlation.model;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class CorrelationTest {

    @Test
    void gettersAndSetters_work() {
        Correlation correlation = new Correlation();

        correlation.setCorrelationId("corr-1");
        assertThat(correlation.getCorrelationId()).isEqualTo("corr-1");
    }

    @Test
    void getConfig_returnsDefensiveCopy_changesToReturnedListDontAffectInternalState() {
        Correlation correlation = new Correlation();

        List<CorrelationConfig> first = correlation.getConfig();
        assertThat(first).isEmpty();

        first.add(new CorrelationConfig());

        assertThat(correlation.getConfig()).isEmpty();
    }

    @Test
    void setConfig_copiesInputList_laterMutationsToInputDontAffectInternalState() {
        Correlation correlation = new Correlation();

        List<CorrelationConfig> input = new ArrayList<>();
        input.add(new CorrelationConfig());

        correlation.setConfig(input);
        assertThat(correlation.getConfig()).hasSize(1);

        input.add(new CorrelationConfig());

        assertThat(correlation.getConfig()).hasSize(1);
    }

    @Test
    void setConfig_null_throwsNpe() {
        Correlation correlation = new Correlation();

        assertThrows(NullPointerException.class, () -> correlation.setConfig(null));
    }
}
