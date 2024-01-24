package gov.cms.qpp.conversion.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI myOpenAPI() {
        Info info = new Info()
                .title("Conversion Tool API")
                .version("v2023.5.0")
                .description("This interactive documentation describes the API for submitting data to the CMS Quality Payment Program - <a href='https://qpp.cms.gov/developers#qrda-iii-conversion-tool-open-source-package'>Conversion Tool</a>.");
        return new OpenAPI().info(info);
    }
}