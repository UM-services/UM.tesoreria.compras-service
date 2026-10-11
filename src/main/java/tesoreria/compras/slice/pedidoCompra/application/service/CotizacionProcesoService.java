package tesoreria.compras.slice.pedidoCompra.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionAdjudicacion;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProceso;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedor;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedorPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.LimiteAutorizacion;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CotizacionProcesoGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.AutoridadGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.EjercicioActualGateway;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionProcesoConflictoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionProcesoInvalidaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.LimiteAutorizacionExcedidoException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Fachada de los procesos de cotización: compone contra core y no persiste. El permiso
 * {@code compras.cotizaciones.gestionar} lo exige el PEP declarativo del controller. El
 * ejercicio, si no llega, se resuelve con el ejercicio en curso.
 */
@Service
@RequiredArgsConstructor
public class CotizacionProcesoService {

    /** Permiso que gobierna la adjudicación (etapa 7). */
    public static final String PERMISO_ADJUDICAR = "compras.adjudicacion.autorizar";

    private static final String ESTADO_RECOMENDADA = "RECOMENDADA";
    private static final String DECISION_ADJUDICADA = "ADJUDICADA";
    private static final String DECISION_DEVUELTA = "DEVUELTA_A_COMPRAS";
    private static final String DECISION_NO_ADJUDICADA = "NO_ADJUDICADA";

    private final CotizacionProcesoGateway cotizacionProcesoGateway;
    private final EjercicioActualGateway ejercicioActualGateway;
    private final AutoridadGateway autoridadGateway;

    public List<CotizacionPendiente> pendientes(Integer ejercicioId, List<Integer> dependenciaIds) {
        return cotizacionProcesoGateway.pendientes(
                ejercicioId != null ? ejercicioId : ejercicioActualGateway.getEjercicioActualId(), dependenciaIds);
    }

    /** Bandeja "Cotizaciones pedidas": documentos de proveedor en ENVIADA pendientes de respuesta. */
    public List<CotizacionProcesoProveedorPendiente> respuestasPendientes(Integer ejercicioId) {
        return cotizacionProcesoGateway.respuestasPendientes(
                ejercicioId != null ? ejercicioId : ejercicioActualGateway.getEjercicioActualId());
    }

    public List<CotizacionProceso> listar(CotizacionProcesoFiltro filtro) {
        return cotizacionProcesoGateway.listar(filtro);
    }

    public CotizacionProceso getById(Long compraCotizacionId) {
        return cotizacionProcesoGateway.getById(compraCotizacionId);
    }

    public CotizacionProceso crear(CotizacionProceso proceso) {
        return cotizacionProcesoGateway.crear(withEjercicio(proceso));
    }

    private CotizacionProceso withEjercicio(CotizacionProceso proceso) {
        if (proceso.ejercicioId() != null) {
            return proceso;
        }
        Integer ejercicioId = ejercicioActualGateway.getEjercicioActualId();
        return new CotizacionProceso(proceso.compraCotizacionId(), proceso.numero(), ejercicioId,
                proceso.fecha(), proceso.estado(), proceso.observacion(), proceso.fechaLimiteRespuesta(),
                proceso.montoEstimado(), proceso.cantidadMinima(), proceso.excepcionMotivo(),
                proceso.fechaEnvio(), proceso.proveedorRecomendadoId(), proceso.fundamentoRecomendacion(),
                proceso.fechaRecomendacion(), proceso.usuarioId(), proceso.items(), proceso.proveedores());
    }

    public CotizacionProceso actualizar(Long compraCotizacionId, CotizacionProceso proceso) {
        return cotizacionProcesoGateway.actualizar(compraCotizacionId, proceso);
    }

    public CotizacionProceso enviar(Long compraCotizacionId) {
        return cotizacionProcesoGateway.enviar(compraCotizacionId);
    }

    public CotizacionProceso anular(Long compraCotizacionId, String motivo) {
        return cotizacionProcesoGateway.anular(compraCotizacionId, motivo);
    }

    // ---- Etapa 6C ----

    public CotizacionProcesoProveedor registrarRespuesta(Long compraCotizacionId, Long compraCotizacionProveedorId,
                                                         CotizacionProcesoProveedor respuesta) {
        return cotizacionProcesoGateway.registrarRespuesta(compraCotizacionId, compraCotizacionProveedorId, respuesta);
    }

    public CotizacionProcesoProveedor marcarNoCotiza(Long compraCotizacionId, Long compraCotizacionProveedorId,
                                                     String observacion) {
        return cotizacionProcesoGateway.marcarNoCotiza(compraCotizacionId, compraCotizacionProveedorId, observacion);
    }

    public CotizacionProceso comparacion(Long compraCotizacionId) {
        return cotizacionProcesoGateway.comparacion(compraCotizacionId);
    }

    public CotizacionProceso recomendar(Long compraCotizacionId, Long compraCotizacionProveedorId, String fundamento) {
        return cotizacionProcesoGateway.recomendar(compraCotizacionId, compraCotizacionProveedorId, fundamento);
    }

    public CotizacionProceso cerrar(Long compraCotizacionId) {
        return cotizacionProcesoGateway.cerrar(compraCotizacionId);
    }

    // ---- Etapa 7: adjudicación por monto ----

    /** Procesos recomendados pendientes de la decisión de la autoridad. */
    public List<CotizacionProceso> adjudicacionBandeja(Long userId) {
        return cotizacionProcesoGateway.listar(
                new CotizacionProcesoFiltro(ESTADO_RECOMENDADA, null, null, null));
    }

    /** Límite efectivo de la autoridad para el ejercicio (en curso si no se indica). */
    public LimiteAutorizacion adjudicacionLimite(Long userId, Integer ejercicioId) {
        Integer efectivo = ejercicioId != null ? ejercicioId : ejercicioActualGateway.getEjercicioActualId();
        return autoridadGateway.getLimite(userId, efectivo);
    }

    /** Adjudica la oferta (la recomendada u otra elegida del proceso). Fail-closed por límite. */
    public CotizacionAdjudicacion adjudicar(Long userId, Long compraCotizacionId, Long compraCotizacionProveedorId,
                                            String fundamento) {
        return resolver(userId, compraCotizacionId, DECISION_ADJUDICADA, compraCotizacionProveedorId, fundamento);
    }

    /** Devuelve el proceso al dpto. de compras. */
    public CotizacionAdjudicacion devolver(Long userId, Long compraCotizacionId, String fundamento) {
        return resolver(userId, compraCotizacionId, DECISION_DEVUELTA, null, fundamento);
    }

    /** No adjudica el proceso. */
    public CotizacionAdjudicacion noAdjudicar(Long userId, Long compraCotizacionId, String fundamento) {
        return resolver(userId, compraCotizacionId, DECISION_NO_ADJUDICADA, null, fundamento);
    }

    public java.util.Optional<CotizacionAdjudicacion> adjudicacion(Long compraCotizacionId) {
        return cotizacionProcesoGateway.adjudicacion(compraCotizacionId);
    }

    private CotizacionAdjudicacion resolver(Long userId, Long compraCotizacionId, String decision,
                                            Long compraCotizacionProveedorId, String fundamento) {
        CotizacionProceso proceso = cotizacionProcesoGateway.getById(compraCotizacionId);
        if (!ESTADO_RECOMENDADA.equals(proceso.estado())) {
            throw new CotizacionProcesoConflictoException("El proceso no está recomendado para adjudicar");
        }
        Long documentoId = compraCotizacionProveedorId != null ? compraCotizacionProveedorId
                : proceso.proveedorRecomendadoId();
        CotizacionProcesoProveedor documento = (proceso.proveedores() == null
                ? List.<CotizacionProcesoProveedor>of() : proceso.proveedores()).stream()
                .filter(p -> Objects.equals(p.compraCotizacionProveedorId(), documentoId))
                .findFirst()
                .orElseThrow(() -> new CotizacionProcesoInvalidaException(
                        "La oferta elegida no pertenece al proceso"));
        BigDecimal monto = documento.total();
        BigDecimal limiteAplicado = null;
        if (DECISION_ADJUDICADA.equals(decision)) {
            LimiteAutorizacion limite = autoridadGateway.getLimite(userId, proceso.ejercicioId());
            boolean excede = !limite.tieneAutoridad() || (!limite.ilimitado()
                    && (limite.limite() == null || monto == null || monto.compareTo(limite.limite()) > 0));
            if (excede) {
                throw new LimiteAutorizacionExcedidoException("el proceso de cotización " + compraCotizacionId,
                        monto, limite.limite());
            }
            limiteAplicado = limite.limite();
        }
        return cotizacionProcesoGateway.adjudicar(compraCotizacionId, decision, documentoId,
                documento.proveedorId(), monto, fundamento, userId, limiteAplicado);
    }
}
