package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.DependenciaInfo;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraResumen;
import tesoreria.compras.slice.pedidoCompra.domain.model.Solicitante;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.EnriquecerPedidosUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.ContextoGateway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resuelve los nombres de dependencia y solicitante de un listado, cacheando por request
 * para no repetir consultas cuando varios pedidos comparten el mismo id. Si la resolución
 * de un nombre falla, degrada a {@code null} sin romper el listado.
 */
@Component
@RequiredArgsConstructor
public class EnriquecerPedidosUseCaseImpl implements EnriquecerPedidosUseCase {

    private final ContextoGateway contextoGateway;

    @Override
    public List<PedidoCompraResumen> enriquecer(List<PedidoCompra> pedidos) {
        if (pedidos == null || pedidos.isEmpty()) {
            return List.of();
        }
        Map<Integer, String> dependencias = new HashMap<>();
        Map<Long, String> solicitantes = new HashMap<>();
        return pedidos.stream()
                .map(pedido -> new PedidoCompraResumen(
                        pedido,
                        dependenciaNombre(pedido, dependencias),
                        solicitanteNombre(pedido, solicitantes)))
                .toList();
    }

    private String dependenciaNombre(PedidoCompra pedido, Map<Integer, String> cache) {
        Integer id = pedido.dependenciaId();
        if (id == null) {
            return null;
        }
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        String nombre = null;
        try {
            DependenciaInfo info = contextoGateway.getDependencia(id);
            nombre = info == null ? null : info.nombre();
        } catch (RuntimeException exception) {
            nombre = null;
        }
        cache.put(id, nombre);
        return nombre;
    }

    private String solicitanteNombre(PedidoCompra pedido, Map<Long, String> cache) {
        Integer id = pedido.solicitanteId();
        if (id == null) {
            return null;
        }
        Long key = id.longValue();
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        String nombre = null;
        try {
            Solicitante solicitante = contextoGateway.getSolicitante(key);
            nombre = solicitante == null ? null : solicitante.nombre();
        } catch (RuntimeException exception) {
            nombre = null;
        }
        cache.put(key, nombre);
        return nombre;
    }
}
