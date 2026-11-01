package tesoreria.compras.slice.pedidoCompra.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.slice.pedidoCompra.domain.model.Recepcion;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionSolicitud;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.EjercicioActualGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.RecepcionGateway;

import java.util.List;
import java.util.Optional;

/**
 * Fachada de la recepción y conformidad (etapa 9): compone contra core y no persiste. El permiso
 * {@code compras.recepcionar} lo exige el PEP declarativo del controller. El ejercicio, si no llega
 * ni se discrimina por Orden de Compra, se resuelve con el ejercicio en curso.
 */
@Service
@RequiredArgsConstructor
public class RecepcionService {

    /** Permiso que gobierna la recepción y conformidad de la Orden de Compra (etapa 9). */
    public static final String PERMISO_RECEPCIONAR = "compras.recepcionar";

    private final RecepcionGateway recepcionGateway;
    private final EjercicioActualGateway ejercicioActualGateway;

    /** Órdenes de Compra con recepción pendiente. */
    public List<RecepcionPendiente> pendientes(Integer ejercicioId) {
        return recepcionGateway.pendientes(
                ejercicioId != null ? ejercicioId : ejercicioActualGateway.getEjercicioActualId());
    }

    public List<Recepcion> listar(RecepcionFiltro filtro) {
        return recepcionGateway.listar(conEjercicio(filtro));
    }

    public Recepcion getById(Long compraRecepcionId) {
        return recepcionGateway.getById(compraRecepcionId);
    }

    /** Recepciones y saldos de una Orden de Compra. */
    public Optional<RecepcionPendiente> porFormalizacion(Long compraFormalizacionId) {
        return recepcionGateway.porFormalizacion(compraFormalizacionId);
    }

    public Recepcion registrar(RecepcionSolicitud solicitud, Long usuarioId) {
        return recepcionGateway.registrar(solicitud, usuarioId);
    }

    /** Cierre manual de la recepción de una OC con saldo abierto. */
    public void cerrar(Long compraFormalizacionId, String motivo, Long usuarioId) {
        recepcionGateway.cerrar(compraFormalizacionId, motivo, usuarioId);
    }

    private RecepcionFiltro conEjercicio(RecepcionFiltro filtro) {
        if (filtro == null || filtro.ejercicioId() != null || filtro.compraFormalizacionId() != null) {
            return filtro;
        }
        Integer ejercicioId = ejercicioActualGateway.getEjercicioActualId();
        return new RecepcionFiltro(ejercicioId, null, filtro.resultado(), filtro.fechaDesde(), filtro.fechaHasta());
    }
}
