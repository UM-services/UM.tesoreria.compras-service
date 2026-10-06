package tesoreria.compras.slice.proveedor;

import tesoreria.compras.slice.proveedor.domain.model.CuentaContable;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;
import tesoreria.compras.slice.proveedor.infrastructure.client.CoreCuentaContableResponse;
import tesoreria.compras.slice.proveedor.infrastructure.client.CoreProveedorResponse;

public final class ProveedorFixture {

    private ProveedorFixture() {
    }

    public static Proveedor proveedor() {
        return new Proveedor(
                8, "20-10564397-8", "", "Proveedor de Prueba", "", "", "", "", "", "", "",
                20101090099L, 1, "", cuenta()
        );
    }

    public static CuentaContable cuenta() {
        return new CuentaContable(
                20101090099L, "Obligaciones a Pagar ", 0, 5,
                20000000000L, 20100000000L, 20101000000L, 20101090000L,
                null, null, 1, 2133
        );
    }

    public static CoreProveedorResponse coreProveedorResponse() {
        return new CoreProveedorResponse(
                8, "20-10564397-8", "", "Proveedor de Prueba", "", "", "", "", "", "", "",
                20101090099L, 1, "",
                new CoreCuentaContableResponse(
                        20101090099L, "Obligaciones a Pagar ", 0, 5,
                        20000000000L, 20100000000L, 20101000000L, 20101090000L,
                        null, null, 1, 2133
                )
        );
    }
}
