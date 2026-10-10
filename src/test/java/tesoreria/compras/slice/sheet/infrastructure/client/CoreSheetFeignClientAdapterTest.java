package tesoreria.compras.slice.sheet.infrastructure.client;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.sheet.domain.exception.SheetSourceUnavailableException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreSheetFeignClientAdapterTest {

    @Mock
    private CoreSheetFeignClient coreSheetFeignClient;

    @InjectMocks
    private CoreSheetFeignClientAdapter adapter;

    @Test
    void returnsBytes() {
        when(coreSheetFeignClient.generateProveedores()).thenReturn(new byte[]{1, 2, 3});

        assertThat(adapter.generateProveedores()).containsExactly(1, 2, 3);
        verify(coreSheetFeignClient).generateProveedores();
    }

    @Test
    void translatesUnavailableCore() {
        when(coreSheetFeignClient.generateProveedores()).thenThrow(new FeignException.ServiceUnavailable(
                "down", Request.create(Request.HttpMethod.GET,
                "http://core-service.test/api/tesoreria/core/sheet/generateProveedores",
                Map.of(), null, StandardCharsets.UTF_8), new byte[0], Map.of()));

        assertThatThrownBy(() -> adapter.generateProveedores())
                .isInstanceOf(SheetSourceUnavailableException.class)
                .hasMessage("La generación de la planilla no está disponible");
    }
}
