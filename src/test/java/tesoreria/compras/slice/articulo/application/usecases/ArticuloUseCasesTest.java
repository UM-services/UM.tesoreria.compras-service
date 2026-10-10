package tesoreria.compras.slice.articulo.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.articulo.ArticuloFixture;
import tesoreria.compras.slice.articulo.domain.ports.out.ArticuloGateway;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticuloUseCasesTest {

    @Mock
    private ArticuloGateway articuloGateway;

    @Test
    void getPaginatedDelegates() {
        var page = new PaginatedResponse<>(List.of(ArticuloFixture.articulo()), 1L, 1, 0, 10);
        when(articuloGateway.getPaginatedByTipo("gasto", 0, 10)).thenReturn(page);

        var result = new GetPaginatedArticulosUseCaseImpl(articuloGateway).getPaginated("gasto", 0, 10);

        assertThat(result).isSameAs(page);
        verify(articuloGateway).getPaginatedByTipo("gasto", 0, 10);
    }

    @Test
    void getNewArticuloDelegates() {
        when(articuloGateway.getNewArticulo()).thenReturn(ArticuloFixture.articulo());

        var result = new GetNewArticuloUseCaseImpl(articuloGateway).getNewArticulo();

        assertThat(result.articuloId()).isEqualTo(101L);
        verify(articuloGateway).getNewArticulo();
    }

    @Test
    void createDelegates() {
        var articulo = ArticuloFixture.articulo();
        when(articuloGateway.createArticulo(articulo)).thenReturn(articulo);

        var result = new CreateArticuloUseCaseImpl(articuloGateway).createArticulo(articulo);

        assertThat(result).isSameAs(articulo);
        verify(articuloGateway).createArticulo(articulo);
    }

    @Test
    void updateDelegates() {
        var articulo = ArticuloFixture.articulo();
        when(articuloGateway.updateArticulo(101L, articulo)).thenReturn(articulo);

        var result = new UpdateArticuloUseCaseImpl(articuloGateway).updateArticulo(101L, articulo);

        assertThat(result).isSameAs(articulo);
        verify(articuloGateway).updateArticulo(101L, articulo);
    }

    @Test
    void deleteDelegates() {
        new DeleteArticuloUseCaseImpl(articuloGateway).deleteArticulo(101L);

        verify(articuloGateway).deleteArticulo(101L);
    }
}
