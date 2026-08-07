package tesoreria.compras.ordencompra.infrastructure.persistence.adapter;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraCriteria;
import tesoreria.compras.ordencompra.domain.model.PaginaOrdenCompra;
import tesoreria.compras.ordencompra.domain.ports.out.OrdenCompraRepository;
import tesoreria.compras.ordencompra.infrastructure.persistence.entity.OrdenCompraEntity;
import tesoreria.compras.ordencompra.infrastructure.persistence.mapper.OrdenCompraPersistenceMapper;
import tesoreria.compras.ordencompra.infrastructure.persistence.repository.JpaOrdenCompraRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaOrdenCompraRepositoryAdapter implements OrdenCompraRepository {

    private static final String RESERVAR_NUMERO = """
            INSERT INTO compra_orden_secuencia (anio, ultimo_numero)
            VALUES (?, LAST_INSERT_ID(1))
            ON DUPLICATE KEY UPDATE ultimo_numero = LAST_INSERT_ID(ultimo_numero + 1)
            """;

    /** Orden estable: sin él dos páginas consecutivas pueden repetir u omitir filas. */
    private static final Sort ORDEN = Sort.by(Sort.Direction.DESC, "fechaEmision")
            .and(Sort.by(Sort.Direction.DESC, "id"));

    private final JpaOrdenCompraRepository repository;
    private final OrdenCompraPersistenceMapper mapper;
    private final JdbcTemplate jdbc;

    /**
     * El UPDATE y el SELECT LAST_INSERT_ID() deben correr sobre la misma conexión: ese
     * valor es por sesión de MySQL. La transacción es lo que garantiza esa afinidad, así
     * que no quitar @Transactional ni separar las dos llamadas.
     */
    @Override
    @Transactional
    public long reservarSiguienteNumero(int anio) {
        jdbc.update(RESERVAR_NUMERO, anio);
        return Objects.requireNonNull(jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class));
    }

    @Override
    @Transactional
    public OrdenCompra save(OrdenCompra orden) {
        return mapper.toDomain(repository.save(mapper.toEntity(orden)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdenCompra> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Optional<OrdenCompra> findByIdForUpdate(Long id) {
        return repository.findByIdForUpdate(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdenCompra> findByNumero(String numero) {
        return repository.findByNumero(numero).map(mapper::toDomain);
    }

    /**
     * Dos consultas a propósito: la primera pagina cabeceras con LIMIT, la segunda trae los
     * ítems de esa página con un solo fetch. Paginar y hacer fetch de la colección en la
     * misma consulta obligaría a Hibernate a paginar en memoria.
     */
    @Override
    @Transactional(readOnly = true)
    public PaginaOrdenCompra findByCriteria(OrdenCompraCriteria criteria) {
        PageRequest pagina = PageRequest.of(criteria.pagina(), criteria.tamano(), ORDEN);
        Page<OrdenCompraEntity> cabeceras = repository.findAll(especificacionDe(criteria), pagina);
        List<Long> ids = cabeceras.getContent().stream().map(OrdenCompraEntity::getId).toList();
        if (ids.isEmpty()) {
            return new PaginaOrdenCompra(List.of(), criteria.pagina(), criteria.tamano(),
                    cabeceras.getTotalElements());
        }
        Map<Long, OrdenCompraEntity> conItems = repository.findByIdIn(ids).stream()
                .collect(Collectors.toMap(OrdenCompraEntity::getId, Function.identity()));
        List<OrdenCompra> contenido = ids.stream()
                .map(conItems::get)
                .filter(Objects::nonNull)
                .map(mapper::toDomain)
                .toList();
        return new PaginaOrdenCompra(contenido, criteria.pagina(), criteria.tamano(),
                cabeceras.getTotalElements());
    }

    private Specification<OrdenCompraEntity> especificacionDe(OrdenCompraCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicados = new ArrayList<>();
            if (criteria.estado() != null) {
                predicados.add(builder.equal(root.get("estado"), criteria.estado().name()));
            }
            if (criteria.proveedorId() != null) {
                predicados.add(builder.equal(root.get("proveedorId"), criteria.proveedorId()));
            }
            if (criteria.sedeId() != null) {
                predicados.add(builder.equal(root.get("sedeId"), criteria.sedeId()));
            }
            if (criteria.fechaDesde() != null) {
                predicados.add(builder.greaterThanOrEqualTo(root.get("fechaEmision"), criteria.fechaDesde()));
            }
            if (criteria.fechaHasta() != null) {
                predicados.add(builder.lessThanOrEqualTo(root.get("fechaEmision"), criteria.fechaHasta()));
            }
            return builder.and(predicados.toArray(Predicate[]::new));
        };
    }
}
