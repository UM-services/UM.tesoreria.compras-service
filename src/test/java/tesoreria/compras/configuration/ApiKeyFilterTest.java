package tesoreria.compras.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class ApiKeyFilterTest {

    private ApiKeyFilter filter;

    @BeforeEach
    void setUp() {
        filter = new ApiKeyFilter();
        ReflectionTestUtils.setField(filter, "apiKey", "default-secret-key");
    }

    private MockHttpServletRequest request(String path, String apiKey) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        if (apiKey != null) {
            request.addHeader("X-API-Key", apiKey);
        }
        return request;
    }

    private int doFilter(MockHttpServletRequest request) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response.getStatus();
    }

    @Test
    void allowsRequestWithValidApiKey() throws Exception {
        assertThat(doFilter(request("/api/tesoreria/compras/ping/8", "default-secret-key"))).isEqualTo(200);
    }

    @Test
    void rejectsMissingApiKey() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request("/api/tesoreria/compras/ping/8", null), response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Missing or invalid X-API-Key header");
    }

    @Test
    void rejectsInvalidApiKey() throws Exception {
        assertThat(doFilter(request("/api/tesoreria/compras/ping/8", "otra-clave"))).isEqualTo(401);
    }

    @Test
    void doesNotFilterActuator() throws Exception {
        assertThat(doFilter(request("/actuator/health", null))).isEqualTo(200);
    }
}
