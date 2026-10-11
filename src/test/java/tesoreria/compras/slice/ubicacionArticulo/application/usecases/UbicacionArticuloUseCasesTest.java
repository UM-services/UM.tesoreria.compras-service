package tesoreria.compras.slice.ubicacionArticulo.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.ubicacionArticulo.UbicacionArticuloFixture;
import tesoreria.compras.slice.ubicacionArticulo.domain.ports.out.UbicacionArticuloGateway;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UbicacionArticuloUseCasesTest {

    @Mock
    private UbicacionArticuloGateway ubicacionArticuloGateway;

    @Test
    void getByArticuloDelegates() {
        when(ubicacionArticuloGateway.getByArticulo(101L))
                .thenReturn(List.of(UbicacionArticuloFixture.ubicacionArticulo()));

        var result = new GetUbicacionArticulosByArticuloUseCaseImpl(ubicacionArticuloGateway).getByArticulo(101L);

        assertThat(result).hasSize(1);
    }

    @Test
    void saveDelegates() {
        var vinculado = UbicacionArticuloFixture.ubicacionArticulo();
        when(ubicacionArticuloGateway.save(vinculado)).thenReturn(vinculado);

        assertThat(new SaveUbicacionArticuloUseCaseImpl(ubicacionArticuloGateway).save(vinculado)).isSameAs(vinculado);
        verify(ubicacionArticuloGateway).save(vinculado);
    }
}
