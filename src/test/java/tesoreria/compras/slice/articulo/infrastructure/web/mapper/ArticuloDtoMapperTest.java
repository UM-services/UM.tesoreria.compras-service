package tesoreria.compras.slice.articulo.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.articulo.ArticuloFixture;
import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.model.ArticuloSearch;
import tesoreria.compras.slice.articulo.infrastructure.web.dto.ArticuloRequest;

import java.math.BigDecimal;
import java.util.List;

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

    @Test
    void mapsRequestToDomain() {
        var request = new ArticuloRequest(
                101L, "Resma A4", "desc", "Un.", new BigDecimal("12500.00"),
                (byte) 1, 10L, new BigDecimal("20101090099"), "gasto", (byte) 0, (byte) 1);

        var articulo = mapper.toDomain(request);

        assertThat(articulo.articuloId()).isEqualTo(101L);
        assertThat(articulo.numeroCuenta()).isEqualByComparingTo("20101090099");
        assertThat(articulo.tipo()).isEqualTo("gasto");
        assertThat(articulo.cuenta()).isNull();
    }

    @Test
    void mapsPageToResponsePage() {
        var page = new PaginatedResponse<>(List.of(ArticuloFixture.articulo()), 1L, 1, 0, 10);

        var response = mapper.toPaginatedResponse(page);

        assertThat(response.data()).hasSize(1);
        assertThat(response.data().getFirst().nombre()).isEqualTo("Resma A4");
        assertThat(response.totalElements()).isEqualTo(1L);
        assertThat(response.currentPage()).isZero();
    }

    @Test
    void mapsPageWithNullDataToEmptyResponsePage() {
        var page = new PaginatedResponse<Articulo>(null, 0L, 0, 0, 10);

        var response = mapper.toPaginatedResponse(page);

        assertThat(response.data()).isEmpty();
    }
}
