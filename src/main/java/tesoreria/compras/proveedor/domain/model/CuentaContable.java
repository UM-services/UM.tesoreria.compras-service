package tesoreria.compras.proveedor.domain.model;

public record CuentaContable(
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
