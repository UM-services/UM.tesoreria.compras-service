package tesoreria.compras.ordencompra.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.ordencompra.OrdenCompraFixture;
import tesoreria.compras.ordencompra.application.service.OrdenCompraService;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraCriteria;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraInvalidaException;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;
import tesoreria.compras.ordencompra.domain.model.PaginaOrdenCompra;
import tesoreria.compras.ordencompra.infrastructure.web.dto.OrdenCompraItemRequest;
import tesoreria.compras.ordencompra.infrastructure.web.dto.OrdenCompraRequest;
import tesoreria.compras.ordencompra.infrastructure.web.mapper.OrdenCompraDtoMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdenCompraControllerTest {

    @Mock
    private OrdenCompraService service;

    private final OrdenCompraDtoMapper mapper = new OrdenCompraDtoMapper();

    private OrdenCompraController controller() {
        return new OrdenCompraController(service, mapper);
    }

    private OrdenCompraRequest request() {
        var item = new OrdenCompraItemRequest(10, "Papel", new BigDecimal("2"), new BigDecimal("12.50"), 20L);
        return new OrdenCompraRequest(OrdenCompraFixture.FECHA, 8, 2, "notas", List.of(item));
    }

    @Test
    void createsOrderAndReturnsLocationOfTheNewResource() {
        when(service.create(any(), any(), any(), any(), any())).thenReturn(OrdenCompraFixture.persistida());

        var response = controller().create(request());

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getHeaders().getLocation())
                .hasToString("/api/tesoreria/compras/ordenCompra/1");
        assertThat(response.getBody().numero()).isEqualTo(OrdenCompraFixture.NUMERO);
        assertThat(response.getBody().total()).isEqualByComparingTo("25.00");
        assertThat(response.getBody().items()).singleElement()
                .satisfies(item -> assertThat(item.id()).isEqualTo(77L));
    }

    @Test
    void readsOrderByIdAndByNumero() {
        when(service.get(1L)).thenReturn(OrdenCompraFixture.persistida());
        when(service.getByNumero(OrdenCompraFixture.NUMERO)).thenReturn(OrdenCompraFixture.persistida());

        assertThat(controller().get(1L).id()).isEqualTo(1L);
        assertThat(controller().getByNumero(OrdenCompraFixture.NUMERO).numero())
                .isEqualTo(OrdenCompraFixture.NUMERO);
    }

    @Test
    void passesEveryOptionalFilterAndThePagingThroughToTheCriteria() {
        when(service.list(any()))
                .thenReturn(new PaginaOrdenCompra(List.of(OrdenCompraFixture.persistida()), 2, 20, 45L));

        var respuesta = controller().list(OrdenCompraEstado.APROBADA, 8, 2,
                OrdenCompraFixture.FECHA, OrdenCompraFixture.FECHA, 2, 20);

        assertThat(respuesta.contenido()).hasSize(1);
        assertThat(respuesta.pagina()).isEqualTo(2);
        assertThat(respuesta.tamano()).isEqualTo(20);
        assertThat(respuesta.totalElementos()).isEqualTo(45L);
        assertThat(respuesta.totalPaginas()).isEqualTo(3);
        verify(service).list(new OrdenCompraCriteria(OrdenCompraEstado.APROBADA, 8, 2,
                OrdenCompraFixture.FECHA, OrdenCompraFixture.FECHA, 2, 20));
    }

    @Test
    void refusesAPageSizeThatWouldPullTheWholeTable() {
        assertThatThrownBy(() -> controller().list(null, null, null, null, null, 0, 5_000))
                .isInstanceOf(OrdenCompraInvalidaException.class)
                .hasMessageContaining("entre 1 y 200");
        assertThatThrownBy(() -> controller().list(null, null, null, null, null, -1, 50))
                .isInstanceOf(OrdenCompraInvalidaException.class);
    }

    @Test
    void refusesAnInvertedDateRange() {
        assertThatThrownBy(() -> controller().list(null, null, null,
                OrdenCompraFixture.FECHA, OrdenCompraFixture.FECHA.minusDays(1), 0, 50))
                .isInstanceOf(OrdenCompraInvalidaException.class)
                .hasMessageContaining("invertido");
    }

    @Test
    void updatesOrderFromRequestBody() {
        when(service.update(any(), any(), any(), any(), any(), any())).thenReturn(OrdenCompraFixture.persistida());

        assertThat(controller().update(1L, request()).id()).isEqualTo(1L);

        verify(service).update(1L, OrdenCompraFixture.FECHA, 8, 2, "notas", mapper.toItems(request().items()));
    }

    @Test
    void mapsEveryActionOfTheRouteToItsTargetState() {
        when(service.transition(any(), any())).thenReturn(OrdenCompraFixture.persistida());

        controller().transition(1L, "aprobar");
        controller().transition(1L, "enviar");
        controller().transition(1L, "factura-parcial");
        controller().transition(1L, "cumplida-parcial");
        controller().transition(1L, "cumplida");
        controller().transition(1L, "anular");

        verify(service).transition(1L, OrdenCompraEstado.APROBADA);
        verify(service).transition(1L, OrdenCompraEstado.ENVIADA);
        verify(service).transition(1L, OrdenCompraEstado.FACTURA_PARCIAL);
        verify(service).transition(1L, OrdenCompraEstado.CUMPLIDA_PARCIAL);
        verify(service).transition(1L, OrdenCompraEstado.CUMPLIDA);
        verify(service).transition(1L, OrdenCompraEstado.ANULADA);
    }
}
