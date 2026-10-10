package tesoreria.compras.configuration.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.method.HandlerMethod;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.PermisoGateway;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequierePermisoInterceptorTest {

    @Mock
    private PermisoGateway permisoGateway;

    private RequierePermisoInterceptor interceptor(boolean enforce) {
        return new RequierePermisoInterceptor(() -> permisoGateway, enforce);
    }

    static class Handler {
        @RequierePermiso("compras.gastos")
        void anotado() {
        }

        void sinAnotacion() {
        }
    }

    private HandlerMethod handler(String method) throws NoSuchMethodException {
        return new HandlerMethod(new Handler(), Handler.class.getDeclaredMethod(method));
    }

    private MockHttpServletRequest request(String userId) {
        var request = new MockHttpServletRequest();
        if (userId != null) {
            request.addHeader(RequierePermisoInterceptor.USER_ID_HEADER, userId);
        }
        return request;
    }

    @Test
    void skipsWhenNotEnforced() throws Exception {
        var interceptor = interceptor(false);

        assertThat(interceptor.preHandle(request(null), null, handler("anotado"))).isTrue();
    }

    @Test
    void allowsHandlerWithoutAnnotation() throws Exception {
        var interceptor = interceptor(true);

        assertThat(interceptor.preHandle(request(null), null, handler("sinAnotacion"))).isTrue();
    }

    @Test
    void allowsNonHandlerObject() {
        var interceptor = interceptor(true);

        assertThat(interceptor.preHandle(request(null), null, new Object())).isTrue();
    }

    @Test
    void requiresIdentity() throws Exception {
        var interceptor = interceptor(true);

        assertThatThrownBy(() -> interceptor.preHandle(request(null), null, handler("anotado")))
                .isInstanceOf(IdentidadRequeridaException.class);
    }

    @Test
    void deniesWhenUserLacksPermission() throws Exception {
        when(permisoGateway.getPermisosEfectivos(7L)).thenReturn(List.of("compras.proveedores"));
        var interceptor = interceptor(true);

        assertThatThrownBy(() -> interceptor.preHandle(request("7"), null, handler("anotado")))
                .isInstanceOf(PermisoDenegadoException.class)
                .hasMessage("Permiso requerido: compras.gastos");
    }

    @Test
    void allowsWhenUserHasPermission() throws Exception {
        when(permisoGateway.getPermisosEfectivos(7L)).thenReturn(List.of("compras.gastos"));
        var interceptor = interceptor(true);

        assertThat(interceptor.preHandle(request("7"), null, handler("anotado"))).isTrue();
    }

    @Test
    void failsClosedWhenGatewayThrows() throws Exception {
        when(permisoGateway.getPermisosEfectivos(7L)).thenThrow(new RuntimeException("core down"));
        var interceptor = interceptor(true);

        assertThatThrownBy(() -> interceptor.preHandle(request("7"), null, handler("anotado")))
                .isInstanceOf(PermisoDenegadoException.class);
    }

    @Test
    void invalidUserIdHeaderIsTreatedAsMissingIdentity() throws Exception {
        var interceptor = interceptor(true);

        assertThatThrownBy(() -> interceptor.preHandle(request("abc"), null, handler("anotado")))
                .isInstanceOf(IdentidadRequeridaException.class);
    }
}
