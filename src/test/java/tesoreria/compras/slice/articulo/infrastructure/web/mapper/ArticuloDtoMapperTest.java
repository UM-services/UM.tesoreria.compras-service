package tesoreria.compras.slice.articulo.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import tesoreria.compras.slice.articulo.ArticuloFixture;
import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.model.ArticuloSearch;

import static org.assertj.core.api.Assertions.assertThat;

class ArticuloDtoMapperTest {

    private final ArticuloDtoMapper mapper = new ArticuloDtoMapper();

    @Test
    void mapsDomainModelToResponse() {
        var response = mapper.toResponse(ArticuloFixture.articulo());

        assertThat(response.articuloId()).isEqualTo(101L);
        assertThat(response.nombre()).isEqualTo("Resma A4");
        assertThat(response.habilitado()).isEqualTo((byte) 1);
        assertThat(response.cuenta().nombre()).isEqualTo("Obligaciones a Pagar");
    }

    @Test
    void mapsArticuloWithoutCuenta() {
        var articulo = new Articulo(
                102L, "Sin cuenta", "", "", null, (byte) 0, null, null, "", (byte) 0, (byte) 0, null
        );

        var response = mapper.toResponse(articulo);

        assertThat(response.cuenta()).isNull();
    }

    @Test
    void mapsSearchDomainModelToResponse() {
        ArticuloSearch search = ArticuloFixture.articuloSearch();

        var response = mapper.toSearchResponse(search);

        assertThat(response.articuloId()).isEqualTo(101L);
        assertThat(response.search()).isEqualTo("Resma A4 Resma papel A4 80g");
        assertThat(response.usuarioAuditoria()).isEqualTo("ddq");
        assertThat(response.cuenta().cuentaContableId()).isEqualTo(2133L);
    }
}
