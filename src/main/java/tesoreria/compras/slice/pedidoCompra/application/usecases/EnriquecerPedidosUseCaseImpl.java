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
 * Resuelve los nombres de dependencia y los datos del solicitante de un listado, cacheando
 * por request para no repetir consultas cuando varios pedidos comparten el mismo id. Si la
 * resolución de un dato falla, degrada a {@code null} sin romper el listado.
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
        Map<Long, Solicitante> solicitantes = new HashMap<>();
        return pedidos.stream()
                .map(pedido -> {
                    Solicitante solicitante = solicitante(pedido, solicitantes);
                    return new PedidoCompraResumen(
                            pedido,
                            dependenciaNombre(pedido, dependencias),
                            solicitante == null ? null : solicitante.nombre(),
                            solicitante == null ? null : solicitante.login());
                })
                .toList();
    }

    @Override
    public PedidoCompraResumen enriquecer(PedidoCompra pedido) {
        if (pedido == null) {
            return null;
        }
        return enriquecer(List.of(pedido)).getFirst();
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

    private Solicitante solicitante(PedidoCompra pedido, Map<Long, Solicitante> cache) {
        Integer id = pedido.solicitanteId();
        if (id == null) {
            return null;
        }
        Long key = id.longValue();
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        Solicitante solicitante = null;
        try {
            solicitante = contextoGateway.getSolicitante(key);
        } catch (RuntimeException exception) {
            solicitante = null;
        }
        cache.put(key, solicitante);
        return solicitante;
    }
}
