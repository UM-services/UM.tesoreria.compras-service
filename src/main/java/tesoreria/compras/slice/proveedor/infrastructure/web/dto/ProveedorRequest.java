package tesoreria.compras.slice.proveedor.infrastructure.web.dto;

/**
 * Cuerpo de alta/edición de proveedor (mismo contrato que core). El id viaja por el
 * path en el PUT.
 */
public record ProveedorRequest(
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
        Byte habilitado,
        String cbu
) {
}
