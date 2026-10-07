package tesoreria.compras.slice.pedidoCompra.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.slice.pedidoCompra.domain.exception.IdentidadRequeridaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PermisoDenegadoException;
import tesoreria.compras.slice.pedidoCompra.domain.model.ContextoInicioPedido;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.*;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.PermisoGateway;

import java.util.List;

/**
 * Fachada del pedido de compra. Compone los casos de uso y aplica el permiso
 * {@code compras.iniciar_pedido} consultando el bundle efectivo de core.
 */
@Service
@RequiredArgsConstructor
public class PedidoCompraService {

    static final String PERMISO_INICIAR_PEDIDO = "compras.iniciar_pedido";

    private final GetContextoInicioPedidoUseCase getContextoInicioPedidoUseCase;
    private final CrearPedidoCompraUseCase crearPedidoCompraUseCase;
    private final ActualizarPedidoCompraUseCase actualizarPedidoCompraUseCase;
    private final EnviarPedidoCompraUseCase enviarPedidoCompraUseCase;
    private final GetPedidoCompraUseCase getPedidoCompraUseCase;
    private final ListPedidosCompraUseCase listPedidosCompraUseCase;
    private final PermisoGateway permisoGateway;

    public ContextoInicioPedido getContexto(Long userId) {
        verificarPermiso(userId);
        return getContextoInicioPedidoUseCase.getContexto(userId);
    }

    public PedidoCompra crear(Long userId, PedidoCompra pedido, boolean enviar) {
        verificarPermiso(userId);
        PedidoCompra creado = crearPedidoCompraUseCase.crear(userId, pedido);
        return enviar ? enviarPedidoCompraUseCase.enviar(creado.compraPedidoId()) : creado;
    }

    public PedidoCompra actualizar(Long userId, Integer compraPedidoId, PedidoCompra pedido, boolean enviar) {
        verificarPermiso(userId);
        PedidoCompra actualizado = actualizarPedidoCompraUseCase.actualizar(compraPedidoId, pedido);
        return enviar ? enviarPedidoCompraUseCase.enviar(compraPedidoId) : actualizado;
    }

    public PedidoCompra enviar(Long userId, Integer compraPedidoId) {
        verificarPermiso(userId);
        return enviarPedidoCompraUseCase.enviar(compraPedidoId);
    }

    public PedidoCompra getById(Integer compraPedidoId) {
        return getPedidoCompraUseCase.getById(compraPedidoId);
    }

    public List<PedidoCompra> listar(Long userId) {
        if (userId == null) {
            throw new IdentidadRequeridaException();
        }
        return listPedidosCompraUseCase.listar(userId);
    }

    private void verificarPermiso(Long userId) {
        if (userId == null) {
            throw new IdentidadRequeridaException();
        }
        List<String> permisos = permisoGateway.getPermisosEfectivos(userId);
        if (permisos == null || !permisos.contains(PERMISO_INICIAR_PEDIDO)) {
            throw new PermisoDenegadoException(PERMISO_INICIAR_PEDIDO);
        }
    }
}
