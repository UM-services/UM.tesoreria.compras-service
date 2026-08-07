package tesoreria.compras.ordencompra.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import tesoreria.compras.ordencompra.OrdenCompraFixture;
import tesoreria.compras.ordencompra.infrastructure.web.dto.OrdenCompraItemRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrdenCompraDtoMapperTest {

    private final OrdenCompraDtoMapper mapper = new OrdenCompraDtoMapper();

    @Test
    void readsRequestItemsAsBrandNewDomainItems() {
        var request = new OrdenCompraItemRequest(10, "Papel", new BigDecimal("2"), new BigDecimal("12.50"), 20L);

        var items = mapper.toItems(List.of(request));

        assertThat(items).containsExactly(OrdenCompraFixture.item());
        assertThat(items).singleElement().satisfies(item -> assertThat(item.id()).isNull());
    }

    @Test
    void exposesTotalSubtotalAndItemIdOnTheResponse() {
        var response = mapper.toResponse(OrdenCompraFixture.persistida());

        assertThat(response.total()).isEqualByComparingTo("25.00");
        assertThat(response.estado()).isEqualTo("PENDIENTE_DE_APROBAR");
        assertThat(response.items()).singleElement().satisfies(item -> {
            assertThat(item.id()).isEqualTo(77L);
            assertThat(item.subtotal()).isEqualByComparingTo("25.00");
        });
    }
}
