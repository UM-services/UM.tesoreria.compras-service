package tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionTramoRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionTramoResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CotizacionTramoDtoMapperTest {

    private final CotizacionTramoDtoMapper mapper = new CotizacionTramoDtoMapper();

    @Test
    void toDomainCopiaCampos() {
        CotizacionTramo domain = mapper.toDomain(new CotizacionTramoRequest("N1", 2, 1, (byte) 1));

        assertThat(domain.cotizacionTramoId()).isNull();
        assertThat(domain.nombre()).isEqualTo("N1");
        assertThat(domain.topeMultiplico()).isEqualTo(2);
        assertThat(domain.cantidadMinima()).isEqualTo(1);
        assertThat(domain.abierto()).isFalse();
    }

    @Test
    void toDomainMarcaAbiertoSinTope() {
        assertThat(mapper.toDomain(new CotizacionTramoRequest("Sin tope", null, 3, null)).abierto()).isTrue();
    }

    @Test
    void toResponseCopiaCampos() {
        CotizacionTramoResponse response = mapper.toResponse(new CotizacionTramo(1L, "N1", 2, 1, false, (byte) 1));

        assertThat(response.cotizacionTramoId()).isEqualTo(1L);
        assertThat(response.nombre()).isEqualTo("N1");
        assertThat(response.abierto()).isFalse();
    }

    @Test
    void mapeaNulos() {
        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.toResponse(null)).isNull();
    }
}
