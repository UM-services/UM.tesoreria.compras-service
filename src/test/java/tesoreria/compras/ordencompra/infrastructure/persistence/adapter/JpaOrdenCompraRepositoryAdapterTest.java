package tesoreria.compras.ordencompra.infrastructure.persistence.adapter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import tesoreria.compras.ordencompra.OrdenCompraFixture;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraCriteria;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;
import tesoreria.compras.ordencompra.infrastructure.persistence.entity.OrdenCompraEntity;
import tesoreria.compras.ordencompra.infrastructure.persistence.mapper.OrdenCompraPersistenceMapper;
import tesoreria.compras.ordencompra.infrastructure.persistence.repository.JpaOrdenCompraRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaOrdenCompraRepositoryAdapterTest {

    @Mock
    private JpaOrdenCompraRepository repository;
    @Mock
    private JdbcTemplate jdbc;

    private final OrdenCompraPersistenceMapper mapper = new OrdenCompraPersistenceMapper();

    private JpaOrdenCompraRepositoryAdapter adapter() {
        return new JpaOrdenCompraRepositoryAdapter(repository, mapper, jdbc);
    }

    private OrdenCompraEntity entidadPersistida() {
        return mapper.toEntity(OrdenCompraFixture.persistida());
    }

    @Test
    void reservesTheNextNumberWithASingleAtomicStatement() {
        when(jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class)).thenReturn(7L);

        assertThat(adapter().reservarSiguienteNumero(2026)).isEqualTo(7L);

        var sql = ArgumentCaptor.forClass(String.class);
        verify(jdbc).update(sql.capture(), eq(2026));
        assertThat(sql.getValue())
                .contains("INSERT INTO compra_orden_secuencia")
                .contains("ON DUPLICATE KEY UPDATE ultimo_numero = LAST_INSERT_ID(ultimo_numero + 1)");
    }

    @Test
    void failsLoudlyWhenTheSequenceReturnsNothing() {
        when(jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class)).thenReturn(null);

        assertThatThrownBy(() -> adapter().reservarSiguienteNumero(2026))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void keepsItemIdentityAcrossASave() {
        when(repository.save(any())).thenReturn(entidadPersistida());

        var guardada = adapter().save(OrdenCompraFixture.persistida());

        var entidadGuardada = ArgumentCaptor.forClass(OrdenCompraEntity.class);
        verify(repository).save(entidadGuardada.capture());
        assertThat(entidadGuardada.getValue().getItems()).singleElement()
                .satisfies(item -> assertThat(item.getId()).isEqualTo(77L));
        assertThat(guardada.items()).singleElement()
                .satisfies(item -> assertThat(item.id()).isEqualTo(77L));
    }

    @Test
    void readsByIdByLockedIdAndByNumero() {
        var entidad = entidadPersistida();
        when(repository.findById(1L)).thenReturn(Optional.of(entidad));
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(entidad));
        when(repository.findByNumero(OrdenCompraFixture.NUMERO)).thenReturn(Optional.of(entidad));

        assertThat(adapter().findById(1L)).get().extracting("numero").isEqualTo(OrdenCompraFixture.NUMERO);
        assertThat(adapter().findByIdForUpdate(1L)).isPresent();
        assertThat(adapter().findByNumero(OrdenCompraFixture.NUMERO)).isPresent();
    }

    @Test
    void returnsEmptyWhenTheOrderIsNotThere() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThat(adapter().findById(9L)).isEmpty();
    }

    @Test
    void pagesHeadersFirstAndThenFetchesTheItemsOfThatPageOnly() {
        var entidad = entidadPersistida();
        var pageable = PageRequest.of(1, 25, Sort.by(Sort.Direction.DESC, "fechaEmision")
                .and(Sort.by(Sort.Direction.DESC, "id")));
        when(repository.findAll(ArgumentMatchers.<Specification<OrdenCompraEntity>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entidad), pageable, 60L));
        when(repository.findByIdIn(List.of(1L))).thenReturn(List.of(entidad));

        var pagina = adapter().findByCriteria(new OrdenCompraCriteria(null, null, null, null, null, 1, 25));

        assertThat(pagina.contenido()).hasSize(1);
        assertThat(pagina.pagina()).isEqualTo(1);
        assertThat(pagina.tamano()).isEqualTo(25);
        assertThat(pagina.totalElementos()).isEqualTo(60L);
        assertThat(pagina.totalPaginas()).isEqualTo(3);

        var pedido = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(ArgumentMatchers.<Specification<OrdenCompraEntity>>any(), pedido.capture());
        assertThat(pedido.getValue().getPageNumber()).isEqualTo(1);
        assertThat(pedido.getValue().getPageSize()).isEqualTo(25);
        assertThat(pedido.getValue().getSort().toString()).isEqualTo("fechaEmision: DESC,id: DESC");
        verify(repository).findByIdIn(List.of(1L));
    }

    @Test
    void skipsTheSecondQueryWhenThePageIsEmpty() {
        when(repository.findAll(ArgumentMatchers.<Specification<OrdenCompraEntity>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 50), 0L));

        var pagina = adapter().findByCriteria(OrdenCompraCriteria.primeraPagina(null, null, null, null, null));

        assertThat(pagina.contenido()).isEmpty();
        assertThat(pagina.totalElementos()).isZero();
        verify(repository, never()).findByIdIn(any());
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void buildsOnePredicatePerSuppliedFilter() {
        when(repository.findAll(ArgumentMatchers.<Specification<OrdenCompraEntity>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 50), 0L));

        var criteria = new OrdenCompraCriteria(OrdenCompraEstado.APROBADA, 8, 2,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 0, 50);
        adapter().findByCriteria(criteria);

        var spec = ArgumentCaptor.forClass(Specification.class);
        verify(repository).findAll(spec.capture(), any(Pageable.class));

        Root<OrdenCompraEntity> root = mock(Root.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);
        Path path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);
        doReturn(path).when(root).get(anyString());
        doReturn(predicate).when(builder).equal(ArgumentMatchers.<Expression<?>>any(),
                ArgumentMatchers.<Object>any());
        doReturn(predicate).when(builder).greaterThanOrEqualTo(
                ArgumentMatchers.<Expression<LocalDate>>any(), ArgumentMatchers.<LocalDate>any());
        doReturn(predicate).when(builder).lessThanOrEqualTo(
                ArgumentMatchers.<Expression<LocalDate>>any(), ArgumentMatchers.<LocalDate>any());
        doReturn(predicate).when(builder).and(any(Predicate[].class));

        ((Specification<OrdenCompraEntity>) spec.getValue()).toPredicate(root, null, builder);

        verify(root).get("estado");
        verify(root).get("proveedorId");
        verify(root).get("sedeId");
        verify(root, times(2)).get("fechaEmision");
        var predicados = ArgumentCaptor.forClass(Predicate[].class);
        verify(builder).and(predicados.capture());
        assertThat(predicados.getValue()).hasSize(5);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void buildsAnEmptyPredicateWhenNoFilterIsSupplied() {
        when(repository.findAll(ArgumentMatchers.<Specification<OrdenCompraEntity>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 50), 0L));

        adapter().findByCriteria(OrdenCompraCriteria.primeraPagina(null, null, null, null, null));

        var spec = ArgumentCaptor.forClass(Specification.class);
        verify(repository).findAll(spec.capture(), any(Pageable.class));

        Root<OrdenCompraEntity> root = mock(Root.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);
        doReturn(predicate).when(builder).and(any(Predicate[].class));

        ((Specification<OrdenCompraEntity>) spec.getValue()).toPredicate(root, null, builder);

        var predicados = ArgumentCaptor.forClass(Predicate[].class);
        verify(builder).and(predicados.capture());
        assertThat(predicados.getValue()).isEmpty();
        verify(root, never()).get(anyString());
    }
}
