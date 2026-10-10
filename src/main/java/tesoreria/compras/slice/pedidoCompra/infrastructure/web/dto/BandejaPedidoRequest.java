package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

/** Filtro de estado opcional en el cuerpo de las bandejas (bandeja y revisión). */
public record BandejaPedidoRequest(String estado) {
}
