package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.util.List;

/**
 * Bandeja del autorizante ya enriquecida para presentación: dependencias habilitadas del
 * usuario y pedidos con los nombres de dependencia y solicitante resueltos.
 */
public record BandejaEnvioResumen(
        List<Integer> dependenciaIds,
        List<PedidoCompraResumen> pedidos
) {
}
