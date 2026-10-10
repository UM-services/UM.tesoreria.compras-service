package tesoreria.compras.slice.articulo.infrastructure.web.dto;

import java.math.BigDecimal;

/**
 * Cuerpo de alta/edición de artículo. Espeja el contrato de core; el id en el PUT
 * se ignora (gana el del path) y un campo nulo significa "sin cambios".
 */
public record ArticuloRequest(
        Long articuloId,
        String nombre,
        String descripcion,
        String unidad,
        BigDecimal precio,
        Byte inventariable,
        Long stockMinimo,
        BigDecimal numeroCuenta,
        String tipo,
        Byte directo,
        Byte habilitado
) {
}
