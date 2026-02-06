package gov.cms.qpp.conversion.correlation.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

class TemplateTest {

    @Test
    void gettersAndSetters_work() {
        Template template = new Template();

        template.setTemplateId("2.16.840.1.113883.10.20.27.3.1");
        template.setCorrelationId("corr-123");

        assertThat(template.getTemplateId()).isEqualTo("2.16.840.1.113883.10.20.27.3.1");
        assertThat(template.getCorrelationId()).isEqualTo("corr-123");
    }

    @Test
    void allowsNulls() {
        Template template = new Template();

        template.setTemplateId(null);
        template.setCorrelationId(null);

        assertThat(template.getTemplateId()).isNull();
        assertThat(template.getCorrelationId()).isNull();
    }
}
