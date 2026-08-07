package tesoreria.compras.ordencompra.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import tesoreria.compras.ordencompra.OrdenCompraFixture;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraInconsistenteException;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;
import tesoreria.compras.ordencompra.infrastructure.persistence.entity.OrdenCompraEntity;
import tesoreria.compras.ordencompra.infrastructure.persistence.entity.OrdenCompraItemEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrdenCompraPersistenceMapperTest {

    private final OrdenCompraPersistenceMapper mapper = new OrdenCompraPersistenceMapper();

    @Test
    void mapsHeaderItemsAndComputedTotalIntoTheEntity() {
        var entity = mapper.toEntity(OrdenCompraFixture.pendiente());

        assertThat(entity.getNumero()).isEqualTo(OrdenCompraFixture.NUMERO);
        assertThat(entity.getEstado()).isEqualTo("PENDIENTE_DE_APROBAR");
        assertThat(entity.getTotal()).isEqualByComparingTo("25.00");
        assertThat(entity.getItems()).singleElement()
                .satisfies(item -> assertThat(item.getOrdenCompra()).isSameAs(entity));
    }

    @Test
    void carriesItemIdsBothWaysSoAMergeUpdatesInsteadOfReinserting() {
        var entity = mapper.toEntity(OrdenCompraFixture.persistida());

        assertThat(entity.getItems()).singleElement()
                .satisfies(item -> assertThat(item.getId()).isEqualTo(77L));
        assertThat(mapper.toDomain(entity).items()).singleElement()
                .satisfies(item -> assertThat(item.id()).isEqualTo(77L));
    }

    @Test
    void leavesTheIdNullForItemsThatWereNeverPersisted() {
        var entity = mapper.toEntity(OrdenCompraFixture.pendiente());

        assertThat(entity.getItems()).singleElement()
                .satisfies(item -> assertThat(item.getId()).isNull());
    }

    @Test
    void readsTheStoredStateBackIntoTheEnum() {
        var domain = mapper.toDomain(mapper.toEntity(OrdenCompraFixture.persistida()));

        assertThat(domain.numero()).isEqualTo(OrdenCompraFixture.NUMERO);
        assertThat(domain.estado()).isEqualTo(OrdenCompraEstado.PENDIENTE_DE_APROBAR);
        assertThat(domain.total()).isEqualByComparingTo("25.00");
    }

    @Test
    void refusesToReadBackAnOrderWhoseStoredTotalDriftedFromItsItems() {
        var entity = new OrdenCompraEntity(1L, OrdenCompraFixture.NUMERO, LocalDate.of(2026, 8, 5), 8, 2,
                "notas", "PENDIENTE_DE_APROBAR", new BigDecimal("999.00"));
        entity.addItem(new OrdenCompraItemEntity(77L, 10, "Papel", new BigDecimal("2"),
                new BigDecimal("12.50"), 20L));

        assertThatThrownBy(() -> mapper.toDomain(entity))
                .isInstanceOf(OrdenCompraInconsistenteException.class)
                .hasMessageContaining("999.00")
                .hasMessageContaining("25.00");
    }
}
