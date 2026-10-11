package tesoreria.compras.slice.pedidoCompra.domain.ports.out;

import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionAdjudicacion;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProceso;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedor;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedorPendiente;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Procesos de cotización (resueltos en {@code core}). */
public interface CotizacionProcesoGateway {

    List<CotizacionPendiente> pendientes(Integer ejercicioId, List<Integer> dependenciaIds);

    /** Documentos de proveedor en {@code ENVIADA} (bandeja "Cotizaciones pedidas"). */
    List<CotizacionProcesoProveedorPendiente> respuestasPendientes(Integer ejercicioId);

    List<CotizacionProceso> listar(CotizacionProcesoFiltro filtro);

    CotizacionProceso getById(Long compraCotizacionId);

    CotizacionProceso crear(CotizacionProceso proceso);

    CotizacionProceso actualizar(Long compraCotizacionId, CotizacionProceso proceso);

    CotizacionProceso enviar(Long compraCotizacionId);

    CotizacionProceso anular(Long compraCotizacionId, String motivo);

    // ---- Etapa 6C ----

    CotizacionProcesoProveedor registrarRespuesta(Long compraCotizacionId, Long compraCotizacionProveedorId,
                                                  CotizacionProcesoProveedor respuesta);

    CotizacionProcesoProveedor marcarNoCotiza(Long compraCotizacionId, Long compraCotizacionProveedorId,
                                              String observacion);

    CotizacionProceso comparacion(Long compraCotizacionId);

    CotizacionProceso recomendar(Long compraCotizacionId, Long compraCotizacionProveedorId, String fundamento);

    CotizacionProceso cerrar(Long compraCotizacionId);

    // ---- Etapa 7: adjudicación ----

    CotizacionAdjudicacion adjudicar(Long compraCotizacionId, String decision, Long compraCotizacionProveedorId,
                                     Integer proveedorAdjudicadoId, BigDecimal monto, String fundamento,
                                     Long autoridadUsuarioId, BigDecimal limiteAplicado);

    Optional<CotizacionAdjudicacion> adjudicacion(Long compraCotizacionId);
}
