package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.util.List;

/** Filtro de la bandeja de ítems pendientes de cotizar en core. */
public record CoreCompraCotizacionPendientesRequest(
        Integer ejercicioId,
        List<Integer> dependenciaIds) {

}
