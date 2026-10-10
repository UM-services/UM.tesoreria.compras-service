package tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import tesoreria.compras.slice.pedidoCompra.PedidoCompraFixture;
import tesoreria.compras.slice.pedidoCompra.domain.model.ContextoInicioPedido;
import tesoreria.compras.slice.pedidoCompra.domain.model.Solicitante;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.PedidoCompraItemRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.PedidoCompraRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PedidoCompraDtoMapperTest {

    private final PedidoCompraDtoMapper mapper = new PedidoCompraDtoMapper();

    @Test
    void requestToDomainConItems() {
        var request = new PedidoCompraRequest("Renovación", null, false, null, true,
                new BigDecimal("4500000.00"), null,
                List.of(new PedidoCompraItemRequest(1, new BigDecimal("5.00"), "Unidad", "Notebook", "16 GB", "url")),
                false);

        var domain = mapper.toDomain(request);

        assertThat(domain.necesidad()).isEqualTo("Renovación");
        assertThat(domain.solicitanteId()).isNull();
        assertThat(domain.items()).hasSize(1);
        assertThat(domain.items().get(0).descripcion()).isEqualTo("Notebook");
    }

    @Test
    void requestToDomainSinItems() {
        var request = new PedidoCompraRequest("x", null, null, null, null, null, null, null, false);

        assertThat(mapper.toDomain(request).items()).isEmpty();
    }

    @Test
    void nullRequestDevuelveNull() {
        assertThat(mapper.toDomain((PedidoCompraRequest) null)).isNull();
    }

    @Test
    void domainToResponseConItems() {
        var response = mapper.toResponse(PedidoCompraFixture.pedido());

        assertThat(response.compraPedidoId()).isEqualTo(1);
        assertThat(response.numero()).isEqualTo("PC-2026-000001");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).unidad()).isEqualTo("Unidad");
    }

    @Test
    void domainToResponseCopiaLosCamposDeDecision() {
        var response = mapper.toResponse(PedidoCompraFixture.pedidoRechazado());

        assertThat(response.estado()).isEqualTo("RECHAZADO");
        assertThat(response.rechazoMotivo()).isEqualTo("Falta cotización");
        assertThat(response.dependenciaNombre()).isNull();
        assertThat(response.solicitanteNombre()).isNull();
    }

    @Test
    void resumenToResponseIncluyeLosNombres() {
        var response = mapper.toResponse(PedidoCompraFixture.resumen());

        assertThat(response.compraPedidoId()).isEqualTo(1);
        assertThat(response.dependenciaNombre()).isEqualTo("Dirección General de Administración");
        assertThat(response.solicitanteNombre()).isEqualTo("Usuario Demo");
    }

    @Test
    void historialToResponse() {
        var responses = mapper.toResponseHistorial(List.of(PedidoCompraFixture.historial()));

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).estado()).isEqualTo("ENVIADO");
        assertThat(responses.get(0).compraPedidoId()).isEqualTo(1);
    }

    @Test
    void nullHistorialDevuelveListaVacia() {
        assertThat(mapper.toResponseHistorial(null)).isEmpty();
    }

    @Test
    void nullDomainDevuelveNull() {
        assertThat(mapper.toResponse((tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra) null)).isNull();
    }

    @Test
    void contextoToResponse() {
        var response = mapper.toResponse(PedidoCompraFixture.contexto());

        assertThat(response.solicitante().userId()).isEqualTo(10L);
        assertThat(response.dependencia().facultadNombre()).isEqualTo("Rectorado");
        assertThat(response.dependencia().sedeNombre()).isEqualTo("Mendoza");
    }

    @Test
    void contextoSinDependenciaMapeaDependenciaNula() {
        var contexto = new ContextoInicioPedido(
                new Solicitante(74L, "Desarrollo Tesoreria", "desarrollo", null), null);

        var response = mapper.toResponse(contexto);

        assertThat(response.solicitante().login()).isEqualTo("desarrollo");
        assertThat(response.dependencia()).isNull();
    }

    @Test
    void nullContextoDevuelveNull() {
        assertThat(mapper.toResponse((tesoreria.compras.slice.pedidoCompra.domain.model.ContextoInicioPedido) null)).isNull();
    }

    @Test
    void limiteToResponse() {
        var response = mapper.toResponse(PedidoCompraFixture.limite(new BigDecimal("4500000.00"), false, true));

        assertThat(response.usuarioId()).isEqualTo(10L);
        assertThat(response.multiplico()).isEqualTo(3);
        assertThat(response.limite()).isEqualByComparingTo("4500000.00");
        assertThat(response.tieneAutoridad()).isTrue();
        assertThat(response.ilimitado()).isFalse();
    }

    @Test
    void nullLimiteDevuelveNull() {
        assertThat(mapper.toResponse((tesoreria.compras.slice.pedidoCompra.domain.model.LimiteAutorizacion) null)).isNull();
    }
}
