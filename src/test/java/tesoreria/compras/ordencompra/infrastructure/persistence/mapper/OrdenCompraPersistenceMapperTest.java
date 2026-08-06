package tesoreria.compras.ordencompra.infrastructure.persistence.mapper;
import org.junit.jupiter.api.Test; import tesoreria.compras.ordencompra.OrdenCompraFixture; import static org.assertj.core.api.Assertions.*;
class OrdenCompraPersistenceMapperTest { @Test void mapsEntityAndDomain(){var mapper=new OrdenCompraPersistenceMapper();var entity=mapper.toEntity(OrdenCompraFixture.pendiente());assertThat(entity.getItems()).hasSize(1);assertThat(mapper.toDomain(entity).numero()).isEqualTo("OC-2026-000001");} }
