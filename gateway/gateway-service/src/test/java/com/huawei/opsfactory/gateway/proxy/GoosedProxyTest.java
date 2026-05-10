package com.huawei.opsfactory.gateway.proxy;

import com.huawei.opsfactory.gateway.config.GatewayProperties;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test coverage for Goosed Proxy.
 *
 * @author x00000000
 * @since 2026-05-09
 */
public class GoosedProxyTest {
    private GoosedProxy proxy;
    private GoosedProxy proxyTls;

    /**
     * Sets the up.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @Before
    public void setUp() {
        GatewayProperties properties = new GatewayProperties();
        properties.setSecretKey("test-key");
        properties.setGooseTls(false);
        proxy = new GoosedProxy(properties);

        GatewayProperties tlsProps = new GatewayProperties();
        tlsProps.setSecretKey("test-key");
        tlsProps.setGooseTls(true);
        proxyTls = new GoosedProxy(tlsProps);
    }

    /**
     * Tests web client not null.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @Test
    public void testWebClientNotNull() {
        assertNotNull(proxy.getWebClient());
    }

    /**
     * Tests goosed base url tls disabled uses http.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @Test
    public void testGoosedBaseUrl_tlsDisabled_usesHttp() {
        assertEquals("http://127.0.0.1:9999", proxy.goosedBaseUrl(9999));
    }

    /**
     * Tests goosed base url tls enabled uses https.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @Test
    public void testGoosedBaseUrl_tlsEnabled_usesHttps() {
        assertEquals("https://127.0.0.1:9999", proxyTls.goosedBaseUrl(9999));
    }

    /**
     * Tests web client not null tls enabled.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @Test
    public void testWebClientNotNull_tlsEnabled() {
        assertNotNull(proxyTls.getWebClient());
    }
}
