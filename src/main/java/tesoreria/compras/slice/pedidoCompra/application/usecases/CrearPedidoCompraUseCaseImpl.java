package tesoreria.compras.slice.pedidoCompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.exception.DependenciaNoAsignadaException;
import tesoreria.compras.slice.pedidoCompra.domain.model.DependenciaInfo;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.Solicitante;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.CrearPedidoCompraUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.ContextoGateway;

@Component
@RequiredArgsConstructor
public class CrearPedidoCompraUseCaseImpl implements CrearPedidoCompraUseCase {

    private final ContextoGateway contextoGateway;
    private final CompraPedidoGateway compraPedidoGateway;

    @Override
    public PedidoCompra crear(Long userId, PedidoCompra pedido) {
        Solicitante solicitante = contextoGateway.getSolicitante(userId);
        if (solicitante.dependenciaId() == null) {
            throw new DependenciaNoAsignadaException();
        }
        DependenciaInfo dependencia = contextoGateway.getDependencia(solicitante.dependenciaId());
        PedidoCompra completo = pedido.conIdentidad(solicitante.userId(), dependencia.dependenciaId(),
                dependencia.facultadId(), dependencia.geograficaId());
        return compraPedidoGateway.crear(completo);
    }
}
