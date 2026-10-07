package tesoreria.compras.slice.pedidoCompra.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.PedidoCompraFixture;
import tesoreria.compras.slice.pedidoCompra.domain.exception.DependenciaNoAsignadaException;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.Solicitante;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.ContextoGateway;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoCompraUseCasesTest {

    @Mock
    private CompraPedidoGateway compraPedidoGateway;

    @Mock
    private ContextoGateway contextoGateway;

    @Test
    void getContextoResuelveSolicitanteYDependencia() {
        when(contextoGateway.getSolicitante(10L)).thenReturn(PedidoCompraFixture.solicitante());
        when(contextoGateway.getDependencia(20)).thenReturn(PedidoCompraFixture.dependencia());

        var useCase = new GetContextoInicioPedidoUseCaseImpl(contextoGateway);
        var contexto = useCase.getContexto(10L);

        assertThat(contexto.solicitante().userId()).isEqualTo(10L);
        assertThat(contexto.dependencia().sedeNombre()).isEqualTo("Mendoza");
    }

    @Test
    void crearCompletaLaIdentidadDesdeElUsuarioYSuDependencia() {
        when(contextoGateway.getSolicitante(10L)).thenReturn(PedidoCompraFixture.solicitante());
        when(contextoGateway.getDependencia(20)).thenReturn(PedidoCompraFixture.dependencia());
        when(compraPedidoGateway.crear(org.mockito.ArgumentMatchers.any(PedidoCompra.class)))
                .thenReturn(PedidoCompraFixture.pedido());

        var useCase = new CrearPedidoCompraUseCaseImpl(contextoGateway, compraPedidoGateway);
        var creado = useCase.crear(10L, PedidoCompraFixture.pedidoSinIdentidad());

        assertThat(creado.compraPedidoId()).isEqualTo(1);
        var captor = org.mockito.ArgumentCaptor.forClass(PedidoCompra.class);
        verify(compraPedidoGateway).crear(captor.capture());
        assertThat(captor.getValue().solicitanteId()).isEqualTo(10);
        assertThat(captor.getValue().dependenciaId()).isEqualTo(20);
        assertThat(captor.getValue().facultadId()).isEqualTo(30);
        assertThat(captor.getValue().geograficaId()).isEqualTo(40);
    }

    @Test
    void actualizarDelegaEnElGateway() {
        when(compraPedidoGateway.actualizar(1, PedidoCompraFixture.pedidoSinIdentidad()))
                .thenReturn(PedidoCompraFixture.pedido());

        var useCase = new ActualizarPedidoCompraUseCaseImpl(compraPedidoGateway);

        assertThat(useCase.actualizar(1, PedidoCompraFixture.pedidoSinIdentidad()).compraPedidoId()).isEqualTo(1);
    }

    @Test
    void enviarDelegaEnElGateway() {
        when(compraPedidoGateway.enviar(1)).thenReturn(PedidoCompraFixture.pedido());

        var useCase = new EnviarPedidoCompraUseCaseImpl(compraPedidoGateway);

        assertThat(useCase.enviar(1).numero()).isEqualTo("PC-2026-000001");
    }

    @Test
    void getByIdDelegaEnElGateway() {
        when(compraPedidoGateway.getById(1)).thenReturn(PedidoCompraFixture.pedido());

        var useCase = new GetPedidoCompraUseCaseImpl(compraPedidoGateway);

        assertThat(useCase.getById(1).items()).hasSize(1);
    }

    @Test
    void listarDelegaEnElGateway() {
        when(compraPedidoGateway.listarPorSolicitante(10L)).thenReturn(List.of(PedidoCompraFixture.pedido()));

        var useCase = new ListPedidosCompraUseCaseImpl(compraPedidoGateway);

        assertThat(useCase.listar(10L)).hasSize(1);
    }

    @Test
    void getContextoSinDependenciaDevuelveDependenciaNula() {
        when(contextoGateway.getSolicitante(74L))
                .thenReturn(new Solicitante(74L, "Desarrollo Tesoreria", "desarrollo", null));

        var useCase = new GetContextoInicioPedidoUseCaseImpl(contextoGateway);
        var contexto = useCase.getContexto(74L);

        assertThat(contexto.solicitante().login()).isEqualTo("desarrollo");
        assertThat(contexto.dependencia()).isNull();
    }

    @Test
    void crearSinDependenciaFalla() {
        when(contextoGateway.getSolicitante(74L))
                .thenReturn(new Solicitante(74L, "Desarrollo Tesoreria", "desarrollo", null));

        var useCase = new CrearPedidoCompraUseCaseImpl(contextoGateway, compraPedidoGateway);

        assertThatThrownBy(() -> useCase.crear(74L, PedidoCompraFixture.pedidoSinIdentidad()))
                .isInstanceOf(DependenciaNoAsignadaException.class);
    }
}
