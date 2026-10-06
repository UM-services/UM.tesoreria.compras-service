package tesoreria.compras.slice.articulo.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.articulo.ArticuloFixture;
import tesoreria.compras.slice.articulo.application.service.ArticuloService;
import tesoreria.compras.slice.articulo.infrastructure.web.mapper.ArticuloDtoMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticuloControllerTest {

    @Mock
    private ArticuloService articuloService;

    @Test
    void returnsArticuloFetchedFromCore() {
        when(articuloService.getArticuloById(101L)).thenReturn(ArticuloFixture.articulo());

        var controller = new ArticuloController(articuloService, new ArticuloDtoMapper());
        var response = controller.getArticuloById(101L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().nombre()).isEqualTo("Resma A4");
        verify(articuloService).getArticuloById(101L);
    }

    @Test
    void returnsSearchResults() {
        var conditions = List.of("resma");
        when(articuloService.searchArticulos(conditions)).thenReturn(List.of(ArticuloFixture.articuloSearch()));

        var controller = new ArticuloController(articuloService, new ArticuloDtoMapper());
        var response = controller.searchArticulos(conditions);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().nombre()).isEqualTo("Resma A4");
        verify(articuloService).searchArticulos(conditions);
    }
}
