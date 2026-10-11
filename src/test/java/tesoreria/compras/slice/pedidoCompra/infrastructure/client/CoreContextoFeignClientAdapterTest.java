package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.PedidoCompraFixture;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreContextoFeignClientAdapterTest {

    @Mock
    private CoreUsuarioFeignClient coreUsuarioFeignClient;

    @Mock
    private CoreDependenciaFeignClient coreDependenciaFeignClient;

    @InjectMocks
    private CoreContextoFeignClientAdapter adapter;

    @Test
    void mapsSolicitante() {
        when(coreUsuarioFeignClient.getMe(10L)).thenReturn(PedidoCompraFixture.coreUsuarioResponse());

        var solicitante = adapter.getSolicitante(10L);

        assertThat(solicitante.nombre()).isEqualTo("Usuario Demo");
        assertThat(solicitante.dependenciaId()).isEqualTo(20);
    }

    @Test
    void mapsSolicitanteUnavailable() {
        when(coreUsuarioFeignClient.getMe(10L))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.getSolicitante(10L))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    @Test
    void mapsDependenciaWithNames() {
        when(coreDependenciaFeignClient.getById(20)).thenReturn(PedidoCompraFixture.coreDependenciaResponse());

        var dependencia = adapter.getDependencia(20);

        assertThat(dependencia.nombre()).isEqualTo("Dirección General de Administración");
        assertThat(dependencia.facultadNombre()).isEqualTo("Rectorado");
        assertThat(dependencia.sedeNombre()).isEqualTo("Mendoza");
    }

    @Test
    void mapsDependenciaWithoutNestedObjects() {
        when(coreDependenciaFeignClient.getById(21))
                .thenReturn(new CoreDependenciaResponse(21, "Sin facultad", null, null, null, null));

        var dependencia = adapter.getDependencia(21);

        assertThat(dependencia.facultadNombre()).isNull();
        assertThat(dependencia.sedeNombre()).isNull();
    }

    @Test
    void mapsDependenciaUnavailable() {
        when(coreDependenciaFeignClient.getById(20))
                .thenThrow(new FeignException.ServiceUnavailable("down", request(), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.getDependencia(20))
                .isInstanceOf(PedidoCompraSourceUnavailableException.class);
    }

    private Request request() {
        return Request.create(Request.HttpMethod.GET, "http://core-service.test/api/tesoreria/core",
                Map.of(), null, StandardCharsets.UTF_8);
    }
}
