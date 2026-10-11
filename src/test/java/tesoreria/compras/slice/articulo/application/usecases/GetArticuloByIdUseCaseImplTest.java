package tesoreria.compras.slice.articulo.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.articulo.ArticuloFixture;
import tesoreria.compras.slice.articulo.domain.ports.out.ArticuloGateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetArticuloByIdUseCaseImplTest {

    @Mock
    private ArticuloGateway articuloGateway;

    @InjectMocks
    private GetArticuloByIdUseCaseImpl useCase;

    @Test
    void getsArticuloFromGateway() {
        when(articuloGateway.getArticuloById(101L)).thenReturn(ArticuloFixture.articulo());

        var articulo = useCase.getArticuloById(101L);

        assertThat(articulo.nombre()).isEqualTo("Resma A4");
        verify(articuloGateway).getArticuloById(101L);
    }
}
