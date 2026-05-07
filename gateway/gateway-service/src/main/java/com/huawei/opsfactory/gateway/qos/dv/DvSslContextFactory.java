package com.huawei.opsfactory.gateway.qos.dv;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.SecureRandom;

@Component
public class DvSslContextFactory {

    private static final Logger log = LoggerFactory.getLogger(DvSslContextFactory.class);

    public SslContext createSslContext(String crtContent, String fileName) {
        if (crtContent == null || crtContent.isBlank()) {
            return createInsecureSslContext();
        }
        Path tempFile = null;
        try {
            byte[] certBytes = java.util.Base64.getDecoder().decode(crtContent);
            tempFile = Files.createTempFile("dv_", fileName != null ? fileName : "cert");
            Files.write(tempFile, certBytes);

            String type = (fileName != null && fileName.endsWith(".p12")) ? "PKCS12" : "JKS";
            KeyStore keyStore = KeyStore.getInstance(type);
            try (InputStream is = new FileInputStream(tempFile.toFile())) {
                keyStore.load(is, "".toCharArray());
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "".toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

            return SslContextBuilder.forClient()
                    .sslContextProvider(null)
                    .trustManager(tmf)
                    .keyManager(kmf)
                    .build();
        } catch (Exception e) {
            log.warn("Failed to create SSL context with certificate, falling back to insecure: {}", e.getMessage());
            return createInsecureSslContext();
        } finally {
            if (tempFile != null) {
                try { Files.deleteIfExists(tempFile); } catch (Exception ignored) {}
            }
        }
    }

    public SslContext createInsecureSslContext() {
        try {
            return SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create insecure SSL context", e);
        }
    }
}
