package tesoreria.compras.slice.pedidoCompra.domain.ports.out;

import tesoreria.compras.slice.pedidoCompra.domain.model.Formalizacion;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionSolicitud;

import java.util.List;
import java.util.Optional;

/** Órdenes de Compra (resueltas en {@code core}). */
public interface FormalizacionGateway {

    /** Adjudicaciones del ejercicio pendientes de emitir la Orden de Compra. */
    List<FormalizacionPendiente> pendientes(Integer ejercicioId);

    List<Formalizacion> listar(FormalizacionFiltro filtro);

    Formalizacion getById(Long compraFormalizacionId);

    /** La OC emitida para un proceso de cotización, si existe. */
    Optional<Formalizacion> porCotizacion(Long compraCotizacionId);

    Formalizacion formalizar(FormalizacionSolicitud solicitud, Long usuarioId);
}
