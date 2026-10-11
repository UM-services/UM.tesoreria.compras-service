package tesoreria.compras.slice.proveedor.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoreProveedorResponse(
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
        CoreCuentaContableResponse cuenta
) {
}
