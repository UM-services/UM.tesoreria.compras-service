package tesoreria.compras.slice.pedidoCompra.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.slice.pedidoCompra.domain.exception.AccesoPedidoDenegadoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.DependenciaNoAutorizadaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.IdentidadRequeridaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PermisoDenegadoException;
import tesoreria.compras.slice.pedidoCompra.domain.model.ContextoInicioPedido;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraHistorial;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraResumen;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.*;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.AutorizanteGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.PermisoGateway;

import java.util.List;

/**
 * Fachada del pedido de compra. Compone los casos de uso y aplica los permisos
 * {@code compras.iniciar_pedido}, {@code compras.enviar_pedido} y
 * {@code compras.consultar_pedidos} consultando el bundle efectivo de core.
 *
 * <p>Acceso acotado: el autorizante sólo decide sobre pedidos de las dependencias que tiene
 * habilitadas; el solicitante sólo opera sobre sus propios pedidos; la consulta global la ve
 * quien tiene {@code compras.consultar_pedidos}.</p>
 */
@Service
@RequiredArgsConstructor
public class PedidoCompraService {

    static final String PERMISO_INICIAR_PEDIDO = "compras.iniciar_pedido";
    static final String PERMISO_ENVIAR_PEDIDO = "compras.enviar_pedido";
    static final String PERMISO_CONSULTAR_PEDIDOS = "compras.consultar_pedidos";

    private final GetContextoInicioPedidoUseCase getContextoInicioPedidoUseCase;
    private final CrearPedidoCompraUseCase crearPedidoCompraUseCase;
    private final ActualizarPedidoCompraUseCase actualizarPedidoCompraUseCase;
    private final EnviarPedidoCompraUseCase enviarPedidoCompraUseCase;
    private final AprobarPedidoCompraUseCase aprobarPedidoCompraUseCase;
    private final RechazarPedidoCompraUseCase rechazarPedidoCompraUseCase;
    private final DescartarPedidoCompraUseCase descartarPedidoCompraUseCase;
    private final GetPedidoCompraUseCase getPedidoCompraUseCase;
    private final ListPedidosCompraUseCase listPedidosCompraUseCase;
    private final ListBandejaEnvioUseCase listBandejaEnvioUseCase;
    private final ListConsultaPedidosUseCase listConsultaPedidosUseCase;
    private final GetHistorialPedidoCompraUseCase getHistorialPedidoCompraUseCase;
    private final EnriquecerPedidosUseCase enriquecerPedidosUseCase;
    private final AutorizanteGateway autorizanteGateway;
    private final PermisoGateway permisoGateway;

    public ContextoInicioPedido getContexto(Long userId) {
        verificarPermiso(userId, PERMISO_INICIAR_PEDIDO);
        return getContextoInicioPedidoUseCase.getContexto(userId);
    }

    public PedidoCompra crear(Long userId, PedidoCompra pedido, boolean enviar) {
        verificarPermiso(userId, PERMISO_INICIAR_PEDIDO);
        PedidoCompra creado = crearPedidoCompraUseCase.crear(userId, pedido);
        return enviar ? enviarPedidoCompraUseCase.enviar(userId, creado.compraPedidoId()) : creado;
    }

    public PedidoCompra actualizar(Long userId, Integer compraPedidoId, PedidoCompra pedido, boolean enviar) {
        verificarPermiso(userId, PERMISO_INICIAR_PEDIDO);
        verificarSolicitante(userId, compraPedidoId);
        PedidoCompra actualizado = actualizarPedidoCompraUseCase.actualizar(compraPedidoId, pedido);
        return enviar ? enviarPedidoCompraUseCase.enviar(userId, compraPedidoId) : actualizado;
    }

    public PedidoCompra enviar(Long userId, Integer compraPedidoId) {
        verificarPermiso(userId, PERMISO_INICIAR_PEDIDO);
        verificarSolicitante(userId, compraPedidoId);
        return enviarPedidoCompraUseCase.enviar(userId, compraPedidoId);
    }

    public PedidoCompra descartar(Long userId, Integer compraPedidoId, String motivo) {
        verificarPermiso(userId, PERMISO_INICIAR_PEDIDO);
        verificarSolicitante(userId, compraPedidoId);
        return descartarPedidoCompraUseCase.descartar(compraPedidoId, userId, motivo);
    }

    public PedidoCompra aprobar(Long userId, Integer compraPedidoId) {
        verificarPermiso(userId, PERMISO_ENVIAR_PEDIDO);
        verificarDependenciaAutorizada(userId, compraPedidoId);
        return aprobarPedidoCompraUseCase.aprobar(compraPedidoId, userId);
    }

    public PedidoCompra rechazar(Long userId, Integer compraPedidoId, String motivo) {
        verificarPermiso(userId, PERMISO_ENVIAR_PEDIDO);
        verificarDependenciaAutorizada(userId, compraPedidoId);
        return rechazarPedidoCompraUseCase.rechazar(compraPedidoId, userId, motivo);
    }

    public List<PedidoCompraResumen> bandeja(Long userId, String estado) {
        verificarPermiso(userId, PERMISO_ENVIAR_PEDIDO);
        return enriquecerPedidosUseCase.enriquecer(listBandejaEnvioUseCase.listar(userId, estado));
    }

    public List<PedidoCompraResumen> consulta(Long userId, PedidoCompraFiltro filtro) {
        verificarPermiso(userId, PERMISO_CONSULTAR_PEDIDOS);
        return enriquecerPedidosUseCase.enriquecer(listConsultaPedidosUseCase.listar(filtro));
    }

    public PedidoCompra getById(Long userId, Integer compraPedidoId) {
        PedidoCompra pedido = getPedidoCompraUseCase.getById(compraPedidoId);
        verificarLectura(userId, pedido);
        return pedido;
    }

    public List<PedidoCompraHistorial> historial(Long userId, Integer compraPedidoId) {
        PedidoCompra pedido = getPedidoCompraUseCase.getById(compraPedidoId);
        verificarLectura(userId, pedido);
        return getHistorialPedidoCompraUseCase.listar(compraPedidoId);
    }

    public List<PedidoCompra> listar(Long userId) {
        verificarPermiso(userId, PERMISO_INICIAR_PEDIDO);
        return listPedidosCompraUseCase.listar(userId);
    }

    private void verificarLectura(Long userId, PedidoCompra pedido) {
        if (userId == null) {
            throw new IdentidadRequeridaException();
        }
        List<String> permisos = permisoGateway.getPermisosEfectivos(userId);
        if (permisos == null) {
            throw new PermisoDenegadoException(PERMISO_INICIAR_PEDIDO);
        }
        if (permisos.contains(PERMISO_CONSULTAR_PEDIDOS)) {
            return;
        }
        if (permisos.contains(PERMISO_ENVIAR_PEDIDO)
                && autorizanteGateway.getDependenciasAutorizadas(userId).contains(pedido.dependenciaId())) {
            return;
        }
        if (permisos.contains(PERMISO_INICIAR_PEDIDO) && esSolicitante(pedido, userId)) {
            return;
        }
        throw new AccesoPedidoDenegadoException(pedido.compraPedidoId());
    }

    private void verificarSolicitante(Long userId, Integer compraPedidoId) {
        PedidoCompra pedido = getPedidoCompraUseCase.getById(compraPedidoId);
        if (!esSolicitante(pedido, userId)) {
            throw new AccesoPedidoDenegadoException(compraPedidoId);
        }
    }

    private boolean esSolicitante(PedidoCompra pedido, Long userId) {
        return userId != null && pedido.solicitanteId() != null && pedido.solicitanteId().equals(userId.intValue());
    }

    private void verificarDependenciaAutorizada(Long userId, Integer compraPedidoId) {
        PedidoCompra pedido = getPedidoCompraUseCase.getById(compraPedidoId);
        List<Integer> dependencias = autorizanteGateway.getDependenciasAutorizadas(userId);
        if (dependencias == null || !dependencias.contains(pedido.dependenciaId())) {
            throw new DependenciaNoAutorizadaException(pedido.dependenciaId());
        }
    }

    private void verificarPermiso(Long userId, String clave) {
        if (userId == null) {
            throw new IdentidadRequeridaException();
        }
        List<String> permisos = permisoGateway.getPermisosEfectivos(userId);
        if (permisos == null || !permisos.contains(clave)) {
            throw new PermisoDenegadoException(clave);
        }
    }
}
