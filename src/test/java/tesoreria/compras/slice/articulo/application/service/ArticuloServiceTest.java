package tesoreria.compras.slice.articulo.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.articulo.ArticuloFixture;
import tesoreria.compras.slice.articulo.domain.ports.in.GetArticuloByIdUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.SearchArticulosUseCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticuloServiceTest {

    @Mock
    private GetArticuloByIdUseCase getArticuloByIdUseCase;

    @Mock
    private SearchArticulosUseCase searchArticulosUseCase;

    @InjectMocks
    private ArticuloService articuloService;

    @Test
    void delegatesGetById() {
        when(getArticuloByIdUseCase.getArticuloById(101L)).thenReturn(ArticuloFixture.articulo());

        var articulo = articuloService.getArticuloById(101L);

        assertThat(articulo.articuloId()).isEqualTo(101L);
        verify(getArticuloByIdUseCase).getArticuloById(101L);
    }

    @Test
    void delegatesSearch() {
        var conditions = List.of("resma");
        when(searchArticulosUseCase.searchArticulos(conditions)).thenReturn(List.of(ArticuloFixture.articuloSearch()));

        var articulos = articuloService.searchArticulos(conditions);

        assertThat(articulos).hasSize(1);
        verify(searchArticulosUseCase).searchArticulos(conditions);
    }
}
