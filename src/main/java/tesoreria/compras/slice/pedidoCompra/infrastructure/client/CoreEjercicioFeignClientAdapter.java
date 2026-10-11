package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.EjercicioActualGateway;

@Component
@RequiredArgsConstructor
public class CoreEjercicioFeignClientAdapter implements EjercicioActualGateway {

    private final CoreEjercicioFeignClient coreEjercicioFeignClient;

    @Override
    public Integer getEjercicioActualId() {
        try {
            CoreEjercicioResponse response = coreEjercicioFeignClient.getLast();
            return response == null ? null : response.ejercicioId();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }
}
