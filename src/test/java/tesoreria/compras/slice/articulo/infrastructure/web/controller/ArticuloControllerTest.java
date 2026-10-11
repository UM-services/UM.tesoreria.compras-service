package tesoreria.compras.slice.articulo.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.model.PageRequest;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.articulo.ArticuloFixture;
import tesoreria.compras.slice.articulo.application.service.ArticuloService;
import tesoreria.compras.slice.articulo.infrastructure.web.dto.ArticuloRequest;
import tesoreria.compras.slice.articulo.infrastructure.web.mapper.ArticuloDtoMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticuloControllerTest {

    @Mock
    private ArticuloService articuloService;

    private ArticuloController controller() {
        return new ArticuloController(articuloService, new ArticuloDtoMapper());
    }

    @Test
    void returnsArticuloFetchedFromCore() {
        when(articuloService.getArticuloById(101L)).thenReturn(ArticuloFixture.articulo());

        var response = controller().getArticuloById(101L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().nombre()).isEqualTo("Resma A4");
        verify(articuloService).getArticuloById(101L);
    }

    @Test
    void returnsSearchResults() {
        var conditions = List.of("resma");
        when(articuloService.searchArticulos(conditions)).thenReturn(List.of(ArticuloFixture.articuloSearch()));

        var response = controller().searchArticulos(conditions);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().nombre()).isEqualTo("Resma A4");
        verify(articuloService).searchArticulos(conditions);
    }

    @Test
    void returnsPaginatedByTipo() {
        var page = new PaginatedResponse<>(List.of(ArticuloFixture.articulo()), 1L, 1, 0, 10);
        when(articuloService.getPaginated("gasto", 0, 10)).thenReturn(page);

        var response = controller().getPaginatedByTipo("gasto", new PageRequest(0, 10));

        assertThat(response.getBody().data()).hasSize(1);
        assertThat(response.getBody().totalElements()).isEqualTo(1L);
    }

    @Test
    void returnsNewArticulo() {
        when(articuloService.getNewArticulo()).thenReturn(ArticuloFixture.articulo());

        var response = controller().getNewArticulo();

        assertThat(response.getBody().articuloId()).isEqualTo(101L);
    }

    @Test
    void createsArticuloWith201() {
        when(articuloService.createArticulo(any())).thenReturn(ArticuloFixture.articulo());

        var response = controller().createArticulo(request());

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody().nombre()).isEqualTo("Resma A4");
    }

    @Test
    void updatesArticulo() {
        when(articuloService.updateArticulo(eq(101L), any())).thenReturn(ArticuloFixture.articulo());

        var response = controller().updateArticulo(101L, request());

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().articuloId()).isEqualTo(101L);
    }

    @Test
    void deletesArticuloWith204() {
        var response = controller().deleteArticulo(101L);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(articuloService).deleteArticulo(101L);
    }

    private ArticuloRequest request() {
        return new ArticuloRequest(
                101L, "Resma A4", "desc", "Un.", new BigDecimal("12500.00"),
                (byte) 1, 10L, new BigDecimal("20101090099"), "gasto", (byte) 0, (byte) 1);
    }
}
