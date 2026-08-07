package tesoreria.compras.ordencompra.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tesoreria.compras.ordencompra.infrastructure.persistence.entity.OrdenCompraEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JpaOrdenCompraRepository
        extends JpaRepository<OrdenCompraEntity, Long>, JpaSpecificationExecutor<OrdenCompraEntity> {

    @EntityGraph(attributePaths = "items")
    Optional<OrdenCompraEntity> findByNumero(String numero);

    @Override
    @EntityGraph(attributePaths = "items")
    Optional<OrdenCompraEntity> findById(Long id);

    /**
     * Segundo tramo del listado paginado: la página se resuelve con LIMIT sobre la cabecera
     * y recién acá se traen los ítems, para no paginar en memoria ni caer en N+1.
     */
    @EntityGraph(attributePaths = "items")
    List<OrdenCompraEntity> findByIdIn(Collection<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "items")
    @Query("select o from OrdenCompraEntity o where o.id = :id")
    Optional<OrdenCompraEntity> findByIdForUpdate(@Param("id") Long id);
}
