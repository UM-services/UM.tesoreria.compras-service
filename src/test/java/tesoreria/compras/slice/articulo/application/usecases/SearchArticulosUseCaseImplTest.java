package tesoreria.compras.slice.articulo.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.articulo.ArticuloFixture;
import tesoreria.compras.slice.articulo.domain.ports.out.ArticuloGateway;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchArticulosUseCaseImplTest {

    @Mock
    private ArticuloGateway articuloGateway;

    @InjectMocks
    private SearchArticulosUseCaseImpl useCase;

    @Test
    void searchesArticulosFromGateway() {
        var conditions = List.of("resma", "a4");
        when(articuloGateway.searchArticulos(conditions)).thenReturn(List.of(ArticuloFixture.articuloSearch()));

        var articulos = useCase.searchArticulos(conditions);

        assertThat(articulos).hasSize(1);
        assertThat(articulos.getFirst().nombre()).isEqualTo("Resma A4");
        verify(articuloGateway).searchArticulos(conditions);
    }
}
