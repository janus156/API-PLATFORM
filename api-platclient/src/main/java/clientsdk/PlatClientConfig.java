package clientsdk;

import clientsdk.client.PlatClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConfigurationProperties("plat.client")
@Data
@ComponentScan
public class PlatClientConfig {

    private String accessKey;

    private String secretKey;

    @Bean
    public PlatClient platClient() {
        return new PlatClient(accessKey, secretKey);
    }

}
