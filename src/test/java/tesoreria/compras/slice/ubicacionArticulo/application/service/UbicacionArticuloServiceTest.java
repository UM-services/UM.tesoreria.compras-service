package tesoreria.compras.slice.ubicacionArticulo.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.ubicacionArticulo.UbicacionArticuloFixture;
import tesoreria.compras.slice.ubicacionArticulo.domain.ports.in.GetUbicacionArticulosByArticuloUseCase;
import tesoreria.compras.slice.ubicacionArticulo.domain.ports.in.SaveUbicacionArticuloUseCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UbicacionArticuloServiceTest {

    @Mock
    private GetUbicacionArticulosByArticuloUseCase getUbicacionArticulosByArticuloUseCase;

    @Mock
    private SaveUbicacionArticuloUseCase saveUbicacionArticuloUseCase;

    @InjectMocks
    private UbicacionArticuloService ubicacionArticuloService;

    @Test
    void delegatesGetByArticulo() {
        when(getUbicacionArticulosByArticuloUseCase.getByArticulo(101L))
                .thenReturn(List.of(UbicacionArticuloFixture.ubicacionArticulo()));

        assertThat(ubicacionArticuloService.getByArticulo(101L)).hasSize(1);
    }

    @Test
    void delegatesSave() {
        var vinculado = UbicacionArticuloFixture.ubicacionArticulo();
        when(saveUbicacionArticuloUseCase.save(vinculado)).thenReturn(vinculado);

        assertThat(ubicacionArticuloService.save(vinculado)).isSameAs(vinculado);
    }
}
