package gov.cms.qpp.conversion.correlation.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

class GoodsTest {

    @Test
    void gettersAndSetters_work() {
        Goods goods = new Goods();

        goods.setRelativeXPath("/ClinicalDocument/id");
        goods.setXmltype("QRDA-III");

        assertThat(goods.getRelativeXPath()).isEqualTo("/ClinicalDocument/id");
        assertThat(goods.getXmltype()).isEqualTo("QRDA-III");
    }

    @Test
    void allowsNulls() {
        Goods goods = new Goods();

        goods.setRelativeXPath(null);
        goods.setXmltype(null);

        assertThat(goods.getRelativeXPath()).isNull();
        assertThat(goods.getXmltype()).isNull();
    }
}
