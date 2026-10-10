package tesoreria.compras.slice.pedidoCompra.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.PedidoCompraFixture;
import tesoreria.compras.slice.pedidoCompra.domain.exception.AccesoPedidoDenegadoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.DependenciaNoAutorizadaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.IdentidadRequeridaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.LimiteAutorizacionExcedidoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraEstadoInvalidoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PermisoDenegadoException;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.*;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.AutoridadGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.AutorizanteGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.PermisoGateway;

import java.math.BigDecimal;
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
    @Mock private AprobarPedidoCompraUseCase aprobarPedidoCompraUseCase;
    @Mock private RechazarPedidoCompraUseCase rechazarPedidoCompraUseCase;
    @Mock private DescartarPedidoCompraUseCase descartarPedidoCompraUseCase;
    @Mock private GetPedidoCompraUseCase getPedidoCompraUseCase;
    @Mock private ListPedidosCompraUseCase listPedidosCompraUseCase;
    @Mock private ListBandejaEnvioUseCase listBandejaEnvioUseCase;
    @Mock private ListConsultaPedidosUseCase listConsultaPedidosUseCase;
    @Mock private GetHistorialPedidoCompraUseCase getHistorialPedidoCompraUseCase;
    @Mock private EnriquecerPedidosUseCase enriquecerPedidosUseCase;
    @Mock private AutorizanteGateway autorizanteGateway;
    @Mock private PermisoGateway permisoGateway;
    @Mock private EstimarPedidoCompraUseCase estimarPedidoCompraUseCase;
    @Mock private AutorizarPresupuestoPedidoCompraUseCase autorizarPresupuestoPedidoCompraUseCase;
    @Mock private RechazarPresupuestoPedidoCompraUseCase rechazarPresupuestoPedidoCompraUseCase;
    @Mock private AutoridadGateway autoridadGateway;

    @InjectMocks
    private PedidoCompraService service;

    private void conPermisos(String... claves) {
        when(permisoGateway.getPermisosEfectivos(10L)).thenReturn(List.of(claves));
    }

    @Test
    void getContextoConPermisoLoDevuelve() {
        conPermisos(PedidoCompraService.PERMISO_INICIAR_PEDIDO);
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
        conPermisos(PedidoCompraService.PERMISO_INICIAR_PEDIDO);
        when(crearPedidoCompraUseCase.crear(10L, PedidoCompraFixture.pedidoSinIdentidad()))
                .thenReturn(PedidoCompraFixture.pedido());

        PedidoCompra creado = service.crear(10L, PedidoCompraFixture.pedidoSinIdentidad(), false);

        assertThat(creado.compraPedidoId()).isEqualTo(1);
        verify(enviarPedidoCompraUseCase, never()).enviar(anyLong(), anyInt());
    }

    @Test
    void crearConEnviarDisparaElEnvio() {
        conPermisos(PedidoCompraService.PERMISO_INICIAR_PEDIDO);
        when(crearPedidoCompraUseCase.crear(10L, PedidoCompraFixture.pedidoSinIdentidad()))
                .thenReturn(PedidoCompraFixture.pedido());
        when(enviarPedidoCompraUseCase.enviar(10L, 1)).thenReturn(PedidoCompraFixture.pedido());

        service.crear(10L, PedidoCompraFixture.pedidoSinIdentidad(), true);

        verify(enviarPedidoCompraUseCase).enviar(10L, 1);
    }

    @Test
    void actualizarConEnviarDisparaElEnvio() {
        conPermisos(PedidoCompraService.PERMISO_INICIAR_PEDIDO);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedido());
        when(actualizarPedidoCompraUseCase.actualizar(1, PedidoCompraFixture.pedidoSinIdentidad()))
                .thenReturn(PedidoCompraFixture.pedido());
        when(enviarPedidoCompraUseCase.enviar(10L, 1)).thenReturn(PedidoCompraFixture.pedido());

        service.actualizar(10L, 1, PedidoCompraFixture.pedidoSinIdentidad(), true);

        verify(enviarPedidoCompraUseCase).enviar(10L, 1);
    }

    @Test
    void actualizarUnPedidoAjenoLanzaAccesoDenegado() {
        conPermisos(PedidoCompraService.PERMISO_INICIAR_PEDIDO);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedidoAjeno());

        assertThatThrownBy(() -> service.actualizar(10L, 1, PedidoCompraFixture.pedidoSinIdentidad(), false))
                .isInstanceOf(AccesoPedidoDenegadoException.class);
    }

    @Test
    void enviarDelegaEnElCasoDeUso() {
        conPermisos(PedidoCompraService.PERMISO_INICIAR_PEDIDO);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedido());
        when(enviarPedidoCompraUseCase.enviar(10L, 1)).thenReturn(PedidoCompraFixture.pedido());

        assertThat(service.enviar(10L, 1).numero()).isEqualTo("PC-2026-000001");
    }

    @Test
    void enviarUnPedidoAjenoLanzaAccesoDenegado() {
        conPermisos(PedidoCompraService.PERMISO_INICIAR_PEDIDO);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedidoAjeno());

        assertThatThrownBy(() -> service.enviar(10L, 1)).isInstanceOf(AccesoPedidoDenegadoException.class);
    }

    @Test
    void descartarDelegaEnElCasoDeUso() {
        conPermisos(PedidoCompraService.PERMISO_INICIAR_PEDIDO);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedido());
        when(descartarPedidoCompraUseCase.descartar(1, 10L, "No hace falta"))
                .thenReturn(PedidoCompraFixture.pedido());

        service.descartar(10L, 1, "No hace falta");

        verify(descartarPedidoCompraUseCase).descartar(1, 10L, "No hace falta");
    }

    @Test
    void aprobarConPermisoYDependenciaAutorizadaDelega() {
        conPermisos(PedidoCompraService.PERMISO_ENVIAR_PEDIDO);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedidoPendienteEnvio());
        when(autorizanteGateway.getDependenciasAutorizadas(10L)).thenReturn(List.of(20));
        when(aprobarPedidoCompraUseCase.aprobar(1, 10L)).thenReturn(PedidoCompraFixture.pedido());

        service.aprobar(10L, 1);

        verify(aprobarPedidoCompraUseCase).aprobar(1, 10L);
    }

    @Test
    void aprobarSinPermisoLanzaDenegado() {
        when(permisoGateway.getPermisosEfectivos(10L)).thenReturn(List.of(PedidoCompraService.PERMISO_INICIAR_PEDIDO));

        assertThatThrownBy(() -> service.aprobar(10L, 1)).isInstanceOf(PermisoDenegadoException.class);
    }

    @Test
    void aprobarUnaDependenciaNoAutorizadaLanza() {
        conPermisos(PedidoCompraService.PERMISO_ENVIAR_PEDIDO);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedidoPendienteEnvio());
        when(autorizanteGateway.getDependenciasAutorizadas(10L)).thenReturn(List.of(99));

        assertThatThrownBy(() -> service.aprobar(10L, 1)).isInstanceOf(DependenciaNoAutorizadaException.class);
    }

    @Test
    void rechazarConPermisoYDependenciaAutorizadaDelega() {
        conPermisos(PedidoCompraService.PERMISO_ENVIAR_PEDIDO);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedidoPendienteEnvio());
        when(autorizanteGateway.getDependenciasAutorizadas(10L)).thenReturn(List.of(20));
        when(rechazarPedidoCompraUseCase.rechazar(1, 10L, "Falta cotización"))
                .thenReturn(PedidoCompraFixture.pedidoRechazado());

        service.rechazar(10L, 1, "Falta cotización");

        verify(rechazarPedidoCompraUseCase).rechazar(1, 10L, "Falta cotización");
    }

    @Test
    void bandejaDelegaYEnriquece() {
        conPermisos(PedidoCompraService.PERMISO_ENVIAR_PEDIDO);
        when(listBandejaEnvioUseCase.listar(10L, "PENDIENTE_ENVIO"))
                .thenReturn(List.of(PedidoCompraFixture.pedidoPendienteEnvio()));
        when(enriquecerPedidosUseCase.enriquecer(any())).thenReturn(List.of(PedidoCompraFixture.resumen()));

        assertThat(service.bandeja(10L, "PENDIENTE_ENVIO")).hasSize(1);
    }

    @Test
    void consultaDelegaYEnriquece() {
        conPermisos(PedidoCompraService.PERMISO_CONSULTAR_PEDIDOS);
        PedidoCompraFiltro filtro = new PedidoCompraFiltro("ENVIADO", null, null, null, null, null);
        when(listConsultaPedidosUseCase.listar(filtro)).thenReturn(List.of(PedidoCompraFixture.pedido()));
        when(enriquecerPedidosUseCase.enriquecer(any())).thenReturn(List.of(PedidoCompraFixture.resumen()));

        assertThat(service.consulta(10L, filtro)).hasSize(1);
    }

    @Test
    void getByIdConConsultarDelega() {
        conPermisos(PedidoCompraService.PERMISO_CONSULTAR_PEDIDOS);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedido());

        assertThat(service.getById(10L, 1).compraPedidoId()).isEqualTo(1);
    }

    @Test
    void getByIdDeUnPedidoAjenoSinConsultaNiDependenciaLanzaAccesoDenegado() {
        conPermisos(PedidoCompraService.PERMISO_INICIAR_PEDIDO);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedidoAjeno());

        assertThatThrownBy(() -> service.getById(10L, 1)).isInstanceOf(AccesoPedidoDenegadoException.class);
    }

    @Test
    void getByIdPermitidoAlAutorizanteDeLaDependencia() {
        conPermisos(PedidoCompraService.PERMISO_ENVIAR_PEDIDO);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedidoPendienteEnvio());
        when(autorizanteGateway.getDependenciasAutorizadas(10L)).thenReturn(List.of(20));

        assertThat(service.getById(10L, 1).compraPedidoId()).isEqualTo(1);
    }

    @Test
    void historialConAlgunPermisoDelega() {
        conPermisos(PedidoCompraService.PERMISO_CONSULTAR_PEDIDOS);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedido());
        when(getHistorialPedidoCompraUseCase.listar(1)).thenReturn(List.of(PedidoCompraFixture.historial()));

        assertThat(service.historial(10L, 1)).hasSize(1);
    }

    @Test
    void historialSinAccesoLanzaDenegado() {
        conPermisos(PedidoCompraService.PERMISO_INICIAR_PEDIDO);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedidoAjeno());

        assertThatThrownBy(() -> service.historial(10L, 1)).isInstanceOf(AccesoPedidoDenegadoException.class);
    }

    @Test
    void listarDelegaEnElCasoDeUso() {
        conPermisos(PedidoCompraService.PERMISO_INICIAR_PEDIDO);
        when(listPedidosCompraUseCase.listar(10L)).thenReturn(List.of(PedidoCompraFixture.pedido()));

        assertThat(service.listar(10L)).hasSize(1);
    }

    @Test
    void listarSinIdentidadLanza() {
        assertThatThrownBy(() -> service.listar(null)).isInstanceOf(IdentidadRequeridaException.class);
    }

    @Test
    void revisionConPermisoDelegaYEnriqueceConEstadoPorDefecto() {
        conPermisos(PedidoCompraService.PERMISO_ESTIMAR);
        when(listConsultaPedidosUseCase.listar(any())).thenReturn(List.of(PedidoCompraFixture.pedido()));
        when(enriquecerPedidosUseCase.enriquecer(any())).thenReturn(List.of(PedidoCompraFixture.resumen()));

        assertThat(service.revision(10L, null)).hasSize(1);

        ArgumentCaptor<PedidoCompraFiltro> captor = ArgumentCaptor.forClass(PedidoCompraFiltro.class);
        verify(listConsultaPedidosUseCase).listar(captor.capture());
        assertThat(captor.getValue().estado()).isEqualTo("EN_REVISION_COMPRAS");
    }

    @Test
    void presupuestoBandejaListaLosPendientesDeAutorizar() {
        conPermisos(PedidoCompraService.PERMISO_PRESUPUESTO_AUTORIZAR);
        when(listConsultaPedidosUseCase.listar(any())).thenReturn(List.of(PedidoCompraFixture.pedidoPendientePresupuesto()));
        when(enriquecerPedidosUseCase.enriquecer(any())).thenReturn(List.of(PedidoCompraFixture.resumen()));

        assertThat(service.presupuestoBandeja(10L)).hasSize(1);

        ArgumentCaptor<PedidoCompraFiltro> captor = ArgumentCaptor.forClass(PedidoCompraFiltro.class);
        verify(listConsultaPedidosUseCase).listar(captor.capture());
        assertThat(captor.getValue().estado()).isEqualTo("PENDIENTE_AUTORIZACION_PRESUPUESTO");
    }

    @Test
    void limiteDelegaEnElGatewayDeAutoridad() {
        conPermisos(PedidoCompraService.PERMISO_PRESUPUESTO_AUTORIZAR);
        var limite = PedidoCompraFixture.limite(new BigDecimal("4500000.00"), false, true);
        when(autoridadGateway.getLimite(10L, 7)).thenReturn(limite);

        assertThat(service.limite(10L, 7)).isEqualTo(limite);
    }

    @Test
    void estimarDelegaEnElCasoDeUso() {
        conPermisos(PedidoCompraService.PERMISO_ESTIMAR);
        when(estimarPedidoCompraUseCase.estimar(1, 10L, new BigDecimal("4500000.00"), "fuente"))
                .thenReturn(PedidoCompraFixture.pedido());

        service.estimar(10L, 1, new BigDecimal("4500000.00"), "fuente");

        verify(estimarPedidoCompraUseCase).estimar(1, 10L, new BigDecimal("4500000.00"), "fuente");
    }

    @Test
    void autorizarPresupuestoDenegadoSiElMontoSuperaElLimite() {
        conPermisos(PedidoCompraService.PERMISO_PRESUPUESTO_AUTORIZAR);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedidoPendientePresupuesto());
        when(autoridadGateway.getLimite(10L, 7))
                .thenReturn(PedidoCompraFixture.limite(new BigDecimal("1500000.00"), false, true));

        assertThatThrownBy(() -> service.autorizarPresupuesto(10L, 1))
                .isInstanceOf(LimiteAutorizacionExcedidoException.class);
    }

    @Test
    void autorizarPresupuestoDenegadoSiNoTieneAutoridad() {
        conPermisos(PedidoCompraService.PERMISO_PRESUPUESTO_AUTORIZAR);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedidoPendientePresupuesto());
        when(autoridadGateway.getLimite(10L, 7))
                .thenReturn(PedidoCompraFixture.limite(BigDecimal.ZERO, false, false));

        assertThatThrownBy(() -> service.autorizarPresupuesto(10L, 1))
                .isInstanceOf(LimiteAutorizacionExcedidoException.class);
    }

    @Test
    void autorizarPresupuestoDenegadoSiNoHayReferencia() {
        conPermisos(PedidoCompraService.PERMISO_PRESUPUESTO_AUTORIZAR);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedidoPendientePresupuesto());
        when(autoridadGateway.getLimite(10L, 7)).thenReturn(PedidoCompraFixture.limite(null, false, true));

        assertThatThrownBy(() -> service.autorizarPresupuesto(10L, 1))
                .isInstanceOf(LimiteAutorizacionExcedidoException.class);
    }

    @Test
    void autorizarPresupuestoConLimiteSuficienteDelega() {
        conPermisos(PedidoCompraService.PERMISO_PRESUPUESTO_AUTORIZAR);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedidoPendientePresupuesto());
        when(autoridadGateway.getLimite(10L, 7))
                .thenReturn(PedidoCompraFixture.limite(new BigDecimal("6000000.00"), false, true));
        when(autorizarPresupuestoPedidoCompraUseCase.autorizar(1, 10L))
                .thenReturn(PedidoCompraFixture.pedidoPendientePresupuesto());

        service.autorizarPresupuesto(10L, 1);

        verify(autorizarPresupuestoPedidoCompraUseCase).autorizar(1, 10L);
    }

    @Test
    void autorizarPresupuestoIlimitadoDelega() {
        conPermisos(PedidoCompraService.PERMISO_PRESUPUESTO_AUTORIZAR);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedidoPendientePresupuesto());
        when(autoridadGateway.getLimite(10L, 7)).thenReturn(PedidoCompraFixture.limite(null, true, true));
        when(autorizarPresupuestoPedidoCompraUseCase.autorizar(1, 10L))
                .thenReturn(PedidoCompraFixture.pedidoPendientePresupuesto());

        service.autorizarPresupuesto(10L, 1);

        verify(autorizarPresupuestoPedidoCompraUseCase).autorizar(1, 10L);
    }

    @Test
    void autorizarPresupuestoConEstadoInvalidoLanza() {
        conPermisos(PedidoCompraService.PERMISO_PRESUPUESTO_AUTORIZAR);
        when(getPedidoCompraUseCase.getById(1)).thenReturn(PedidoCompraFixture.pedido());

        assertThatThrownBy(() -> service.autorizarPresupuesto(10L, 1))
                .isInstanceOf(PedidoCompraEstadoInvalidoException.class);
    }

    @Test
    void rechazarPresupuestoDelegaEnElCasoDeUso() {
        conPermisos(PedidoCompraService.PERMISO_PRESUPUESTO_AUTORIZAR);
        when(rechazarPresupuestoPedidoCompraUseCase.rechazar(1, 10L, "fuera de política"))
                .thenReturn(PedidoCompraFixture.pedidoRechazado());

        service.rechazarPresupuesto(10L, 1, "fuera de política");

        verify(rechazarPresupuestoPedidoCompraUseCase).rechazar(1, 10L, "fuera de política");
    }

    @Test
    void estimarSinPermisoLanzaDenegado() {
        when(permisoGateway.getPermisosEfectivos(10L)).thenReturn(List.of("otro.permiso"));

        assertThatThrownBy(() -> service.estimar(10L, 1, new BigDecimal("1"), null))
                .isInstanceOf(PermisoDenegadoException.class);
    }
}
