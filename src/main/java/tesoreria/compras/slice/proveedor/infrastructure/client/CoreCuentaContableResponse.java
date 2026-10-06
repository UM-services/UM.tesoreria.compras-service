package tesoreria.compras.slice.proveedor.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoreCuentaContableResponse(
        Long numeroCuenta,
        String nombre,
        Integer integradora,
        Integer grado,
        Long grado1,
        Long grado2,
        Long grado3,
        Long grado4,
        Integer geograficaId,
        String fechaBloqueo,
        Integer visible,
        Integer cuentaContableId
) {
}
