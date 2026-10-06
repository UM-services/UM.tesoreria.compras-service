package tesoreria.compras.slice.proveedor.domain.model;

public record Proveedor(
        Integer proveedorId,
        String cuit,
        String nombreFantasia,
        String razonSocial,
        String ordenCheque,
        String domicilio,
        String telefono,
        String fax,
        String celular,
        String email,
        String emailInterno,
        Long numeroCuenta,
        Integer habilitado,
        String cbu,
        CuentaContable cuenta
) {
}
