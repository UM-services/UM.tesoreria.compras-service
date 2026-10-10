package tesoreria.compras.slice.ubicacion.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import tesoreria.compras.slice.ubicacion.UbicacionFixture;

import static org.assertj.core.api.Assertions.assertThat;

class UbicacionDtoMapperTest {

    private final UbicacionDtoMapper mapper = new UbicacionDtoMapper();

    @Test
    void mapsDomainToResponse() {
        var response = mapper.toResponse(UbicacionFixture.ubicacion());

        assertThat(response.ubicacionId()).isEqualTo(1);
        assertThat(response.nombre()).isEqualTo("Rectorado");
        assertThat(response.dependenciaId()).isEqualTo(10);
        assertThat(response.geograficaId()).isEqualTo(20);
    }
}
