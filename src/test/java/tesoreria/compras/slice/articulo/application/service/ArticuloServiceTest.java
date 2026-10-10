package tesoreria.compras.slice.articulo.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.articulo.ArticuloFixture;
import tesoreria.compras.slice.articulo.domain.ports.in.CreateArticuloUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.DeleteArticuloUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.GetArticuloByIdUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.GetNewArticuloUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.GetPaginatedArticulosUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.SearchArticulosUseCase;
import tesoreria.compras.slice.articulo.domain.ports.in.UpdateArticuloUseCase;

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

    @Mock
    private GetPaginatedArticulosUseCase getPaginatedArticulosUseCase;

    @Mock
    private GetNewArticuloUseCase getNewArticuloUseCase;

    @Mock
    private CreateArticuloUseCase createArticuloUseCase;

    @Mock
    private UpdateArticuloUseCase updateArticuloUseCase;

    @Mock
    private DeleteArticuloUseCase deleteArticuloUseCase;

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

    @Test
    void delegatesGetPaginated() {
        var page = new PaginatedResponse<>(List.of(ArticuloFixture.articulo()), 1L, 1, 0, 10);
        when(getPaginatedArticulosUseCase.getPaginated("gasto", 0, 10)).thenReturn(page);

        assertThat(articuloService.getPaginated("gasto", 0, 10)).isSameAs(page);
    }

    @Test
    void delegatesGetNew() {
        when(getNewArticuloUseCase.getNewArticulo()).thenReturn(ArticuloFixture.articulo());

        assertThat(articuloService.getNewArticulo().articuloId()).isEqualTo(101L);
    }

    @Test
    void delegatesCreate() {
        var articulo = ArticuloFixture.articulo();
        when(createArticuloUseCase.createArticulo(articulo)).thenReturn(articulo);

        assertThat(articuloService.createArticulo(articulo)).isSameAs(articulo);
    }

    @Test
    void delegatesUpdate() {
        var articulo = ArticuloFixture.articulo();
        when(updateArticuloUseCase.updateArticulo(101L, articulo)).thenReturn(articulo);

        assertThat(articuloService.updateArticulo(101L, articulo)).isSameAs(articulo);
    }

    @Test
    void delegatesDelete() {
        articuloService.deleteArticulo(101L);

        verify(deleteArticuloUseCase).deleteArticulo(101L);
    }
}
