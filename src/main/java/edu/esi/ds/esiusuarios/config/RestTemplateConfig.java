package edu.esi.ds.esiusuarios.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        try {
            // Crear contexto SSL que confía en certificados autofirmados
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            }, new java.security.SecureRandom());

            // Crear cliente HTTP que use el contexto SSL
            ClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(
                    new SimpleClientHttpRequestFactory() {
                        @Override
                        protected void prepareConnection(java.net.HttpURLConnection connection, String httpMethod) throws java.io.IOException {
                            if (connection instanceof HttpsURLConnection) {
                                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                                httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                                httpsConnection.setHostnameVerifier((hostname, session) -> true);
                            }
                            super.prepareConnection(connection, httpMethod);
                        }
                    });

            return builder.requestFactory(() -> requestFactory).build();
        } catch (Exception e) {
            throw new RuntimeException("Error configurando RestTemplate con certificados autofirmados", e);
        }
    }
}
