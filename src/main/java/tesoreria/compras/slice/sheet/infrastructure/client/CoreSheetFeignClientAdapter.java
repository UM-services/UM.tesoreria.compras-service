package tesoreria.compras.slice.sheet.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.sheet.domain.exception.SheetSourceUnavailableException;
import tesoreria.compras.slice.sheet.domain.ports.out.SheetGateway;

@Component
@RequiredArgsConstructor
public class CoreSheetFeignClientAdapter implements SheetGateway {

    private final CoreSheetFeignClient coreSheetFeignClient;

    @Override
    public byte[] generateProveedores() {
        try {
            return coreSheetFeignClient.generateProveedores();
        } catch (FeignException exception) {
            throw new SheetSourceUnavailableException(exception);
        }
    }
}
