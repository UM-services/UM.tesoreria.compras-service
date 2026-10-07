package tesoreria.compras.slice.pedidoCompra.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.PedidoCompraFixture;
import tesoreria.compras.slice.pedidoCompra.domain.exception.IdentidadRequeridaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PermisoDenegadoException;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.*;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.PermisoGateway;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoCompraServiceTest {

    @Mock private GetContextoInicioPedidoUseCase getContextoInicioPedidoUseCase;
    @Mock private CrearPedidoCompraUseCase crearPedidoCompraUseCase;
    @Mock private ActualizarPedidoCompraUseCase actualizarPedidoCompraUseCase;
    @Mock private EnviarPedidoCompraUseCase enviarPedidoCompraUseCase;
    @Mock private GetPedidoCompraUseCase getPedidoCompraUseCase;
    @Mock private ListPedidosCompraUseCase listPedidosCompraUseCase;
    @Mock private PermisoGateway permisoGateway;

    @InjectMocks
    private PedidoCompraService service;

    @Test
    void getContextoConPermisoLoDevuelve() {
        when(permisoGateway.getPermisosEfectivos(10L)).thenReturn(List.of("compras.iniciar_pedido"));
        when(getContextoInicioPedidoUseCase.getContexto(10L)).thenReturn(PedidoCompraFixture.contexto());

        assertThat(service.getContexto(10L).solicitante().userId()).isEqualTo(10L);
    }

    @Test
    void sinPermisoLanzaPermisoDenegado() {
        when(permisoGateway.getPermisosEfectivos(10L)).thenReturn(List.of("otro.permiso"));

        assertThatThrownBy(() -> service.getContexto(10L)).isInstanceOf(PermisoDenegadoException.class);
    }

    @Test
    void sinIdentidadLanzaIdentidadRequerida() {
        assertThatThrownBy(() -> service.getContexto(null)).isInstanceOf(IdentidadRequeridaException.class);
    }

    @Test
    void sinBundleLanzaPermisoDenegado() {
        when(permisoGateway.getPermisosEfectivos(10L)).thenReturn(null);

        assertThatThrownBy(() -> service.crear(10L, PedidoCompraFixture.pedidoSinIdentidad(), false))
                .isInstanceOf(PermisoDenegadoException.class);
    }

    @Test
    void crearSinEnviarDevuelveElBorrador() {
        when(permisoGateway.getPermisosEfectivos(10L)).thenReturn(List.of("compras.iniciar_pedido"));
        when(crearPedidoCompraUseCase.crear(10L, PedidoCompraFixture.pedidoSinIdentidad()))
                .thenReturn(PedidoCompraFixture.pedido());

        PedidoCompra creado = service.crear(10L, PedidoCompraFixture.pedidoSinIdentidad(), false);

        assertThat(creado.compraPedidoId()).isEqualTo(1);
        verify(enviarPedidoCompraUseCase, never()).enviar(anyInt());
    }

    @Test
    void crearConEnviarDisparaElEnvio() {
        when(permisoGateway.getPermisosEfectivos(10L)).thenReturn(List.of("compras.iniciar_pedido"));
        when(crearPedidoCompraUseCase.crear(10L, PedidoCompraFixture.pedidoSinIdentidad()))
                .thenReturn(PedidoCompraFixture.pedido());
        when(enviarPedidoCompraUseCase.enviar(1)).thenReturn(PedidoCompraFixture.pedido());

        service.crear(10L, PedidoCompraFixture.pedidoSinIdentidad(), true);

        verify(enviarPedidoCompraUseCase).enviar(1);
    }

    @Test
    void actualizarSinEnviarDevuelveElActualizado() {
        when(permisoGateway.getPermisosEfectivos(10L)).thenReturn(List.of("compras.iniciar_pedido"));
        when(actualizarPedidoCompraUseCase.actualizar(1, PedidoCompraFixture.pedidoSinIdentidad()))
                .thenReturn(PedidoCompraFixture.pedido());

        service.actualizar(10L, 1, PedidoCompraFixture.pedidoSinIdentidad(), false);

        verify(enviarPedidoCompraUseCase, never()).enviar(anyInt());
    }

    @Test
    void actualizarConEnviarDisparaElEnvio() {
        when(permisoGateway.getPermisosEfectivos(10L)).thenReturn(List.of("compras.iniciar_pedido"));
        when(actualizarPedidoCompraUseCase.actualizar(1, PedidoCompraFixture.pedidoSinIdentidad()))
                .thenReturn(PedidoCompraFixture.pedido());
        when(enviarPedidoCompraUseCase.enviar(1)).thenReturn(PedidoCompraFixture.pedido());

        service.actualizar(10L, 1, PedidoCompraFixture.pedidoSinIdentidad(), true);

        verify(enviarPedidoCompraUseCase).enviar(1);
    }

    @Test
    void enviarDelegaEnElCasoDeUso() {
        when(permisoGateway.getPermisosEfectivos(10L)).thenReturn(List.of("compras.iniciar_pedido"));
        when(enviarPedidoCompraUseCase.enviar(1)).thenReturn(PedidoCompraFixture.pedido());

        assertThat(service.enviar(10L, 1).numero()).isEqualTo("PC-2026-000001");
    }

    @Test
    void getByIdDelegaEnElCasoDeUso() {
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedido());

        assertThat(service.getById(1).compraPedidoId()).isEqualTo(1);
    }

    @Test
    void listarDelegaEnElCasoDeUso() {
        when(listPedidosCompraUseCase.listar(10L)).thenReturn(List.of(PedidoCompraFixture.pedido()));

        assertThat(service.listar(10L)).hasSize(1);
    }

    @Test
    void listarSinIdentidadLanza() {
        assertThatThrownBy(() -> service.listar(null)).isInstanceOf(IdentidadRequeridaException.class);
    }
}
