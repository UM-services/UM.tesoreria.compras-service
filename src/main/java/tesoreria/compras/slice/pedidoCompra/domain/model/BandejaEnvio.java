package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.util.List;

/**
 * Resultado de la bandeja del autorizante: las dependencias que tiene habilitadas y los
 * pedidos que caen en ellas. Conserva las dependencias aunque no haya pedidos para que la
 * presentación pueda distinguir "sin dependencias" de "sin pedidos" sin volver a consultar
 * core.
 */
public record BandejaEnvio(
        List<Integer> dependenciaIds,
        List<PedidoCompra> pedidos
) {
}
