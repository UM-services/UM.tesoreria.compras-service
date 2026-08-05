package tesoreria.compras.proveedor.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import tesoreria.compras.proveedor.ProveedorFixture;
import tesoreria.compras.proveedor.domain.model.Proveedor;

import static org.assertj.core.api.Assertions.assertThat;

class ProveedorDtoMapperTest {

    private final ProveedorDtoMapper mapper = new ProveedorDtoMapper();

    @Test
    void mapsDomainModelToResponse() {
        var response = mapper.toResponse(ProveedorFixture.proveedor());

        assertThat(response.proveedorId()).isEqualTo(8);
        assertThat(response.cuit()).isEqualTo("20-10564397-8");
        assertThat(response.habilitado()).isEqualTo(1);
        assertThat(response.cuenta().nombre()).isEqualTo("Obligaciones a Pagar ");
    }

    @Test
    void mapsProveedorWithoutCuenta() {
        var proveedor = new Proveedor(
                9, "", "", "Sin cuenta", "", "", "", "", "", "", "", null, 0, "", null
        );

        var response = mapper.toResponse(proveedor);

        assertThat(response.cuenta()).isNull();
    }
}
