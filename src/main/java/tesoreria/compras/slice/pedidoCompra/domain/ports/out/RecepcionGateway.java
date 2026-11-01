package tesoreria.compras.slice.pedidoCompra.domain.ports.out;

import tesoreria.compras.slice.pedidoCompra.domain.model.Recepcion;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionSolicitud;

import java.util.List;
import java.util.Optional;

/** Recepciones y conformidad de Órdenes de Compra (resueltas en {@code core}). */
public interface RecepcionGateway {

    /** Órdenes de Compra del ejercicio con recepción pendiente. */
    List<RecepcionPendiente> pendientes(Integer ejercicioId);

    List<Recepcion> listar(RecepcionFiltro filtro);

    Recepcion getById(Long compraRecepcionId);

    /** Recepciones y saldos de una Orden de Compra. */
    Optional<RecepcionPendiente> porFormalizacion(Long compraFormalizacionId);

    Recepcion registrar(RecepcionSolicitud solicitud, Long usuarioId);

    /** Cierre manual de la recepción de una OC con saldo abierto (motivo obligatorio). */
    void cerrar(Long compraFormalizacionId, String motivo, Long usuarioId);
}
