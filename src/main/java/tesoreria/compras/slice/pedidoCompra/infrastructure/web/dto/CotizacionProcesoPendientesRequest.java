package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.util.List;

/** Filtro de la bandeja de ítems pendientes de cotizar: ejercicio y dependencias. */
public record CotizacionProcesoPendientesRequest(
        Integer ejercicioId,
        List<Integer> dependenciaIds) {

}
