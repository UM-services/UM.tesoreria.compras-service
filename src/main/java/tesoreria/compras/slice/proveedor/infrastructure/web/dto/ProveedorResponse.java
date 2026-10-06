package tesoreria.compras.slice.proveedor.infrastructure.web.dto;

public record ProveedorResponse(
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
        CuentaContableResponse cuenta
) {
}
