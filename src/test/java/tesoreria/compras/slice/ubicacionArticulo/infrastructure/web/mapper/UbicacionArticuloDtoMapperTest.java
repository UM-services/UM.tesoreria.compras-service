package tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import tesoreria.compras.slice.ubicacionArticulo.UbicacionArticuloFixture;
import tesoreria.compras.slice.ubicacionArticulo.domain.model.UbicacionArticulo;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.dto.UbicacionArticuloRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class UbicacionArticuloDtoMapperTest {

    private final UbicacionArticuloDtoMapper mapper = new UbicacionArticuloDtoMapper();

    @Test
    void mapsDomainToResponse() {
        var response = mapper.toResponse(UbicacionArticuloFixture.ubicacionArticulo());

        assertThat(response.ubicacionArticuloId()).isEqualTo(50L);
        assertThat(response.ubicacion().nombre()).isEqualTo("Rectorado");
        assertThat(response.cuenta().nombre()).isEqualTo("Obligaciones a Pagar");
        assertThat(response.numeroCuenta()).isEqualByComparingTo("20101090099");
    }

    @Test
    void mapsEmptyProjectionToNullRefs() {
        var vacio = new UbicacionArticulo(null, null, null, null, null, null);

        var response = mapper.toResponse(vacio);

        assertThat(response.ubicacion()).isNull();
        assertThat(response.cuenta()).isNull();
    }

    @Test
    void mapsRequestToDomain() {
        var request = new UbicacionArticuloRequest(1, 101L, new BigDecimal("20101090099"));

        var domain = mapper.toDomain(request);

        assertThat(domain.ubicacionId()).isEqualTo(1);
        assertThat(domain.articuloId()).isEqualTo(101L);
        assertThat(domain.numeroCuenta()).isEqualByComparingTo("20101090099");
        assertThat(domain.ubicacionArticuloId()).isNull();
    }
}
