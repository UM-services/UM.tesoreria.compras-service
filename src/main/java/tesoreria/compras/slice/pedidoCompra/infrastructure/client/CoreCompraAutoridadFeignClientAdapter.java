package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.model.LimiteAutorizacion;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.AutoridadGateway;

@Component
@RequiredArgsConstructor
public class CoreCompraAutoridadFeignClientAdapter implements AutoridadGateway {

    private final CoreCompraAutoridadFeignClient coreCompraAutoridadFeignClient;

    @Override
    public LimiteAutorizacion getLimite(Long usuarioId, Integer ejercicioId) {
        try {
            return toDomain(coreCompraAutoridadFeignClient.getLimite(toInteger(usuarioId), ejercicioId));
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    private Integer toInteger(Long usuarioId) {
        return usuarioId == null ? null : usuarioId.intValue();
    }

    private LimiteAutorizacion toDomain(CoreLimiteAutorizacionResponse response) {
        if (response == null) {
            return null;
        }
        return new LimiteAutorizacion(
                response.usuarioId() == null ? null : response.usuarioId().longValue(),
                response.ejercicioId(),
                response.multiplico(),
                response.referencia(),
                response.limite(),
                Boolean.TRUE.equals(response.ilimitado()),
                Boolean.TRUE.equals(response.tieneAutoridad()));
    }
}
