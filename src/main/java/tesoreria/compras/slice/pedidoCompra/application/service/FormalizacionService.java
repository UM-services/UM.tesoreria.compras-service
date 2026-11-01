package tesoreria.compras.slice.pedidoCompra.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.slice.pedidoCompra.domain.model.Formalizacion;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionSolicitud;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.EjercicioActualGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.FormalizacionGateway;

import java.util.List;
import java.util.Optional;

/**
 * Fachada de la Orden de Compra (etapa 8): compone contra core y no persiste. El permiso
 * {@code compras.formalizar} lo exige el PEP declarativo del controller. El ejercicio, si no llega,
 * se resuelve con el ejercicio en curso.
 */
@Service
@RequiredArgsConstructor
public class FormalizacionService {

    /** Permiso que gobierna la emisión de la Orden de Compra (etapa 8). */
    public static final String PERMISO_FORMALIZAR = "compras.formalizar";

    private final FormalizacionGateway formalizacionGateway;
    private final EjercicioActualGateway ejercicioActualGateway;

    /** Adjudicaciones pendientes de emitir la Orden de Compra. */
    public List<FormalizacionPendiente> pendientes(Integer ejercicioId) {
        return formalizacionGateway.pendientes(
                ejercicioId != null ? ejercicioId : ejercicioActualGateway.getEjercicioActualId());
    }

    public List<Formalizacion> listar(FormalizacionFiltro filtro) {
        return formalizacionGateway.listar(conEjercicio(filtro));
    }

    private FormalizacionFiltro conEjercicio(FormalizacionFiltro filtro) {
        if (filtro != null && filtro.ejercicioId() != null) {
            return filtro;
        }
        Integer ejercicioId = ejercicioActualGateway.getEjercicioActualId();
        return new FormalizacionFiltro(ejercicioId,
                filtro == null ? null : filtro.proveedorId(),
                filtro == null ? null : filtro.fechaDesde(),
                filtro == null ? null : filtro.fechaHasta());
    }

    public Formalizacion getById(Long compraFormalizacionId) {
        return formalizacionGateway.getById(compraFormalizacionId);
    }

    /** La OC emitida para un proceso de cotización, si existe. */
    public Optional<Formalizacion> porCotizacion(Long compraCotizacionId) {
        return formalizacionGateway.porCotizacion(compraCotizacionId);
    }

    public Formalizacion formalizar(FormalizacionSolicitud solicitud, Long usuarioId) {
        return formalizacionGateway.formalizar(solicitud, usuarioId);
    }
}
