package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

/** Cierre manual de la recepción de una OC con saldo abierto. */
public record CerrarRecepcionRequest(
        String motivo) {
}
