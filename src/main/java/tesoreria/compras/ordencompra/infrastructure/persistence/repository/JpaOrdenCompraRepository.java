package tesoreria.compras.ordencompra.infrastructure.persistence.repository;
import org.springframework.data.jpa.repository.*; import tesoreria.compras.ordencompra.infrastructure.persistence.entity.OrdenCompraEntity; import java.util.*;
public interface JpaOrdenCompraRepository extends JpaRepository<OrdenCompraEntity,Long>, JpaSpecificationExecutor<OrdenCompraEntity> { @EntityGraph(attributePaths="items") Optional<OrdenCompraEntity> findByNumero(String numero); @Override @EntityGraph(attributePaths="items") Optional<OrdenCompraEntity> findById(Long id); }
