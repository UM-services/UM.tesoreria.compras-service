package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Documento de proveedor en estado {@code ENVIADA} pendiente de cargar su respuesta, con su
 * proceso de origen y los ítems del proceso (bandeja "Cotizaciones pedidas").
 */
public record CotizacionProcesoProveedorPendiente(
        Long compraCotizacionId,
        String numero,
        Integer ejercicioId,
        LocalDateTime fechaLimiteRespuesta,
        CotizacionProcesoProveedor proveedor,
        List<CotizacionProcesoItem> items) {
}
