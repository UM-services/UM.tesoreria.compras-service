package tesoreria.compras.slice.proveedor.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.proveedor.ProveedorFixture;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;
import tesoreria.compras.slice.proveedor.infrastructure.web.dto.ProveedorRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProveedorDtoMapperTest {

    private final ProveedorDtoMapper mapper = new ProveedorDtoMapper();

    @Test
    void mapsDomainModelToResponse() {
        var response = mapper.toResponse(ProveedorFixture.proveedor());

        assertThat(response.proveedorId()).isEqualTo(8);
        assertThat(response.razonSocial()).isEqualTo("Proveedor de Prueba");
        assertThat(response.numeroCuenta()).isEqualTo(20101090099L);
        assertThat(response.cuenta().nombre()).isEqualTo("Obligaciones a Pagar ");
    }

    @Test
    void mapsProveedorWithoutCuenta() {
        var proveedor = new Proveedor(
                9, "20-1-1", "", "Sin cuenta", "", "", "", "", "", "", "", null, 1, "", null);

        var response = mapper.toResponse(proveedor);

        assertThat(response.cuenta()).isNull();
    }

    @Test
    void mapsRequestToDomain() {
        var request = new ProveedorRequest(
                "20-10564397-8", "fantasia", "Proveedor de Prueba", "OC", "dom", "tel", "fax",
                "cel", "mail", "mail2", 20101090099L, (byte) 1, "cbu");

        var proveedor = mapper.toDomain(request);

        assertThat(proveedor.proveedorId()).isNull();
        assertThat(proveedor.cuit()).isEqualTo("20-10564397-8");
        assertThat(proveedor.numeroCuenta()).isEqualTo(20101090099L);
        assertThat(proveedor.habilitado()).isEqualTo(1);
        assertThat(proveedor.cuenta()).isNull();
    }

    @Test
    void mapsRequestWithNullHabilitadoToNull() {
        var request = new ProveedorRequest(
                "20-10564397-8", "", "Proveedor", "", "", "", "", "", "", "", null, null, "");

        assertThat(mapper.toDomain(request).habilitado()).isNull();
    }

    @Test
    void mapsPageToResponsePage() {
        var page = new PaginatedResponse<>(List.of(ProveedorFixture.proveedor()), 1L, 1, 0, 20);

        var response = mapper.toPaginatedResponse(page);

        assertThat(response.data()).hasSize(1);
        assertThat(response.data().getFirst().cuit()).isEqualTo("20-10564397-8");
        assertThat(response.pageSize()).isEqualTo(20);
    }

    @Test
    void mapsPageWithNullDataToEmptyResponsePage() {
        var page = new PaginatedResponse<Proveedor>(null, 0L, 0, 0, 20);

        assertThat(mapper.toPaginatedResponse(page).data()).isEmpty();
    }
}
