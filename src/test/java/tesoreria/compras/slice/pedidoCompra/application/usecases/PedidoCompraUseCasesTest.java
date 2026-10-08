package tesoreria.compras.slice.pedidoCompra.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.PedidoCompraFixture;
import tesoreria.compras.slice.pedidoCompra.domain.exception.DependenciaNoAsignadaException;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.Solicitante;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.AutorizanteGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.ContextoGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.HistorialGateway;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoCompraUseCasesTest {

    @Mock
    private CompraPedidoGateway compraPedidoGateway;

    @Mock
    private ContextoGateway contextoGateway;

    @Mock
    private AutorizanteGateway autorizanteGateway;

    @Mock
    private HistorialGateway historialGateway;

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
    void getContextoSinDependenciaDevuelveDependenciaNula() {
        when(contextoGateway.getSolicitante(74L))
                .thenReturn(new Solicitante(74L, "Desarrollo Tesoreria", "desarrollo", null));

        var useCase = new GetContextoInicioPedidoUseCaseImpl(contextoGateway);
        var contexto = useCase.getContexto(74L);

        assertThat(contexto.solicitante().login()).isEqualTo("desarrollo");
        assertThat(contexto.dependencia()).isNull();
    }

    @Test
    void crearCompletaLaIdentidadDesdeElUsuarioYSuDependencia() {
        when(contextoGateway.getSolicitante(10L)).thenReturn(PedidoCompraFixture.solicitante());
        when(contextoGateway.getDependencia(20)).thenReturn(PedidoCompraFixture.dependencia());
        when(compraPedidoGateway.crear(any())).thenReturn(PedidoCompraFixture.pedido());

        var useCase = new CrearPedidoCompraUseCaseImpl(contextoGateway, compraPedidoGateway);
        var creado = useCase.crear(10L, PedidoCompraFixture.pedidoSinIdentidad());

        assertThat(creado.compraPedidoId()).isEqualTo(1);
        var captor = org.mockito.ArgumentCaptor.forClass(tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra.class);
        verify(compraPedidoGateway).crear(captor.capture());
        assertThat(captor.getValue().solicitanteId()).isEqualTo(10);
        assertThat(captor.getValue().dependenciaId()).isEqualTo(20);
        assertThat(captor.getValue().facultadId()).isEqualTo(30);
        assertThat(captor.getValue().geograficaId()).isEqualTo(40);
    }

    @Test
    void crearSinDependenciaFalla() {
        when(contextoGateway.getSolicitante(74L))
                .thenReturn(new Solicitante(74L, "Desarrollo Tesoreria", "desarrollo", null));

        var useCase = new CrearPedidoCompraUseCaseImpl(contextoGateway, compraPedidoGateway);

        assertThatThrownBy(() -> useCase.crear(74L, PedidoCompraFixture.pedidoSinIdentidad()))
                .isInstanceOf(DependenciaNoAsignadaException.class);
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
        when(compraPedidoGateway.enviar(1, 10L)).thenReturn(PedidoCompraFixture.pedido());

        var useCase = new EnviarPedidoCompraUseCaseImpl(compraPedidoGateway);

        assertThat(useCase.enviar(10L, 1).numero()).isEqualTo("PC-2026-000001");
    }

    @Test
    void aprobarDelegaEnElGateway() {
        when(compraPedidoGateway.aprobar(1, 10L)).thenReturn(PedidoCompraFixture.pedido());

        var useCase = new AprobarPedidoCompraUseCaseImpl(compraPedidoGateway);

        assertThat(useCase.aprobar(1, 10L).compraPedidoId()).isEqualTo(1);
    }

    @Test
    void rechazarDelegaEnElGateway() {
        when(compraPedidoGateway.rechazar(1, 10L, "motivo")).thenReturn(PedidoCompraFixture.pedidoRechazado());

        var useCase = new RechazarPedidoCompraUseCaseImpl(compraPedidoGateway);

        assertThat(useCase.rechazar(1, 10L, "motivo").estado()).isEqualTo("RECHAZADO");
    }

    @Test
    void descartarDelegaEnElGateway() {
        when(compraPedidoGateway.descartar(1, 10L, "motivo")).thenReturn(PedidoCompraFixture.pedido());

        var useCase = new DescartarPedidoCompraUseCaseImpl(compraPedidoGateway);

        assertThat(useCase.descartar(1, 10L, "motivo").compraPedidoId()).isEqualTo(1);
    }

    @Test
    void bandejaFiltraPorLasDependenciasAutorizadas() {
        when(autorizanteGateway.getDependenciasAutorizadas(10L)).thenReturn(List.of(20, 21));
        when(compraPedidoGateway.listar(any())).thenReturn(List.of(PedidoCompraFixture.pedidoPendienteEnvio()));

        var useCase = new ListBandejaEnvioUseCaseImpl(autorizanteGateway, compraPedidoGateway);

        assertThat(useCase.listar(10L, "PENDIENTE_ENVIO")).hasSize(1);
        var captor = org.mockito.ArgumentCaptor.forClass(PedidoCompraFiltro.class);
        verify(compraPedidoGateway).listar(captor.capture());
        assertThat(captor.getValue().dependenciaIds()).containsExactly(20, 21);
        assertThat(captor.getValue().estado()).isEqualTo("PENDIENTE_ENVIO");
    }

    @Test
    void bandejaSinDependenciasNoConsultaCore() {
        when(autorizanteGateway.getDependenciasAutorizadas(10L)).thenReturn(List.of());

        var useCase = new ListBandejaEnvioUseCaseImpl(autorizanteGateway, compraPedidoGateway);

        assertThat(useCase.listar(10L, null)).isEmpty();
        verifyNoInteractions(compraPedidoGateway);
    }

    @Test
    void consultaDelegaEnElGateway() {
        PedidoCompraFiltro filtro = new PedidoCompraFiltro("ENVIADO", null, null, null, null, null);
        when(compraPedidoGateway.listar(filtro)).thenReturn(List.of(PedidoCompraFixture.pedido()));

        var useCase = new ListConsultaPedidosUseCaseImpl(compraPedidoGateway);

        assertThat(useCase.listar(filtro)).hasSize(1);
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
    void historialDelegaEnElGateway() {
        when(historialGateway.listar(1)).thenReturn(List.of(PedidoCompraFixture.historial()));

        var useCase = new GetHistorialPedidoCompraUseCaseImpl(historialGateway);

        assertThat(useCase.listar(1)).hasSize(1);
    }

    @Test
    void enriquecerResuelveLosNombresYCachaPorId() {
        when(contextoGateway.getDependencia(20)).thenReturn(PedidoCompraFixture.dependencia());
        when(contextoGateway.getSolicitante(10L)).thenReturn(PedidoCompraFixture.solicitante());
        var useCase = new EnriquecerPedidosUseCaseImpl(contextoGateway);

        var resumenes = useCase.enriquecer(
                List.of(PedidoCompraFixture.pedido(), PedidoCompraFixture.pedidoPendienteEnvio()));

        assertThat(resumenes).hasSize(2);
        assertThat(resumenes.get(0).dependenciaNombre()).isEqualTo("Dirección General de Administración");
        assertThat(resumenes.get(0).solicitanteNombre()).isEqualTo("Usuario Demo");
        verify(contextoGateway, times(1)).getDependencia(20);
        verify(contextoGateway, times(1)).getSolicitante(10L);
    }

    @Test
    void enriquecerListaVaciaNoConsultaCore() {
        var useCase = new EnriquecerPedidosUseCaseImpl(contextoGateway);

        assertThat(useCase.enriquecer(List.of())).isEmpty();
        assertThat(useCase.enriquecer(null)).isEmpty();
        verifyNoInteractions(contextoGateway);
    }

    @Test
    void enriquecerDegradaANullSiFallaLaResolucion() {
        when(contextoGateway.getDependencia(20)).thenThrow(new RuntimeException("down"));
        when(contextoGateway.getSolicitante(10L)).thenThrow(new RuntimeException("down"));
        var useCase = new EnriquecerPedidosUseCaseImpl(contextoGateway);

        var resumenes = useCase.enriquecer(List.of(PedidoCompraFixture.pedido()));

        assertThat(resumenes.get(0).dependenciaNombre()).isNull();
        assertThat(resumenes.get(0).solicitanteNombre()).isNull();
    }
}
