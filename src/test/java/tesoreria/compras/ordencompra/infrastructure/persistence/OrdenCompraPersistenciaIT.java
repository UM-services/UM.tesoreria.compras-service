package tesoreria.compras.ordencompra.infrastructure.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.cache.autoconfigure.CacheAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraCriteria;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraItem;
import tesoreria.compras.ordencompra.infrastructure.persistence.adapter.JpaOrdenCompraRepositoryAdapter;
import tesoreria.compras.ordencompra.infrastructure.persistence.mapper.OrdenCompraPersistenceMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Corre contra MySQL real: es la única forma de verificar el DDL que se le pide al DBA,
 * la reserva atómica con LAST_INSERT_ID y que un merge conserve los ids de los ítems.
 *
 * Sin Docker se saltea, pero con `REQUIRE_DOCKER=true` falla en vez de saltearse. CI
 * exporta esa variable: un build verde tiene que significar que estas pruebas corrieron,
 * no que desaparecieron sin avisar. La puerta de cobertura no depende de esta clase.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration({JdbcTemplateAutoConfiguration.class, CacheAutoConfiguration.class})
@Import({JpaOrdenCompraRepositoryAdapter.class, OrdenCompraPersistenceMapper.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Testcontainers
@EnabledIf("hayDocker")
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.cloud.consul.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.service-registry.auto-registration.enabled=false"
})
class OrdenCompraPersistenciaIT {

    private static final String AYUDA_DOCKER =
            "Testcontainers no encontró Docker, así que las pruebas de integración contra MySQL "
            + "no corrieron. Con Colima hay que exportar:\n"
            + "  export DOCKER_HOST=\"unix://$HOME/.colima/default/docker.sock\"\n"
            + "  export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock\n"
            + "Ver la sección \"Pruebas de integración\" del README.";

    /**
     * Devuelve false para saltear en una máquina sin Docker, pero tira si alguien pidió
     * REQUIRE_DOCKER: ahí saltearse en silencio sería exactamente el bug que queremos evitar.
     */
    static boolean hayDocker() {
        boolean disponible = DockerClientFactory.instance().isDockerAvailable();
        if (!disponible && dockerExigido()) {
            throw new IllegalStateException("REQUIRE_DOCKER=true pero " + AYUDA_DOCKER);
        }
        if (!disponible) {
            System.err.println("[AVISO] " + AYUDA_DOCKER);
        }
        return disponible;
    }

    private static boolean dockerExigido() {
        String porEntorno = System.getenv("REQUIRE_DOCKER");
        String porPropiedad = System.getProperty("requireDocker");
        return Boolean.parseBoolean(porEntorno) || Boolean.parseBoolean(porPropiedad);
    }

    @Container
    @ServiceConnection
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4")
            .withInitScript("db/compra-orden-ddl.sql");

    @Autowired
    private JpaOrdenCompraRepositoryAdapter adapter;
    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void limpiar() {
        jdbc.execute("DELETE FROM compra_orden_item");
        jdbc.execute("DELETE FROM compra_orden");
        jdbc.execute("DELETE FROM compra_orden_secuencia");
    }

    private OrdenCompra nueva(String numero, LocalDate fecha) {
        return OrdenCompra.nueva(numero, fecha, 8, 2, "notas",
                List.of(OrdenCompraItem.nuevo(10, "Papel", new BigDecimal("2"), new BigDecimal("12.50"), 20L),
                        OrdenCompraItem.nuevo(11, "Tinta", new BigDecimal("1"), new BigDecimal("100.00"), 21L)));
    }

    @Test
    void reservesConsecutiveNumbersAndRestartsTheSeriesEachYear() {
        assertThat(adapter.reservarSiguienteNumero(2026)).isEqualTo(1L);
        assertThat(adapter.reservarSiguienteNumero(2026)).isEqualTo(2L);
        assertThat(adapter.reservarSiguienteNumero(2026)).isEqualTo(3L);
        assertThat(adapter.reservarSiguienteNumero(2027)).isEqualTo(1L);
        assertThat(adapter.reservarSiguienteNumero(2026)).isEqualTo(4L);
    }

    @Test
    void neverHandsTheSameNumberToTwoConcurrentCallers() throws Exception {
        int hilos = 24;
        try (ExecutorService pool = Executors.newFixedThreadPool(8)) {
            List<Callable<Long>> tareas = IntStream.range(0, hilos)
                    .<Callable<Long>>mapToObj(i -> () -> adapter.reservarSiguienteNumero(2026))
                    .toList();
            Set<Long> numeros = pool.invokeAll(tareas).stream()
                    .map(this::valorDe)
                    .collect(Collectors.toSet());

            assertThat(numeros).hasSize(hilos);
            assertThat(numeros).containsExactlyInAnyOrderElementsOf(
                    IntStream.rangeClosed(1, hilos).mapToObj(Long::valueOf).toList());
        }
    }

    private Long valorDe(Future<Long> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void keepsItemRowIdsWhenTheOrderChangesState() {
        OrdenCompra guardada = adapter.save(nueva("OC-2026-000001", LocalDate.of(2026, 8, 5)));
        List<Long> idsOriginales = guardada.items().stream().map(OrdenCompraItem::id).sorted().toList();
        assertThat(idsOriginales).doesNotContainNull().hasSize(2);

        OrdenCompra aprobada = adapter.save(guardada.transicionarA(OrdenCompraEstado.APROBADA));

        assertThat(aprobada.estado()).isEqualTo(OrdenCompraEstado.APROBADA);
        assertThat(aprobada.items().stream().map(OrdenCompraItem::id).sorted().toList())
                .isEqualTo(idsOriginales);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM compra_orden_item", Integer.class)).isEqualTo(2);
    }

    @Test
    void storesTheTotalItComputedFromTheItems() {
        adapter.save(nueva("OC-2026-000001", LocalDate.of(2026, 8, 5)));

        BigDecimal guardado = jdbc.queryForObject("SELECT total FROM compra_orden", BigDecimal.class);

        assertThat(guardado).isEqualByComparingTo("125.00");
    }

    @Test
    void loadsTheDetailWithItsItemsAfterTheSessionIsGone() {
        Long id = adapter.save(nueva("OC-2026-000001", LocalDate.of(2026, 8, 5))).id();

        OrdenCompra leida = adapter.findById(id).orElseThrow();

        assertThat(leida.items()).hasSize(2);
        assertThat(leida.total()).isEqualByComparingTo("125.00");
        assertThat(adapter.findByNumero("OC-2026-000001")).get()
                .extracting(orden -> ((OrdenCompra) orden).items().size()).isEqualTo(2);
    }

    @Test
    void filtersByAnInclusiveDateRange() {
        adapter.save(nueva("OC-2026-000001", LocalDate.of(2026, 8, 4)));
        adapter.save(nueva("OC-2026-000002", LocalDate.of(2026, 8, 5)));
        adapter.save(nueva("OC-2026-000003", LocalDate.of(2026, 8, 6)));

        var enRango = adapter.findByCriteria(OrdenCompraCriteria.primeraPagina(null, null, null,
                LocalDate.of(2026, 8, 4), LocalDate.of(2026, 8, 6)));
        var soloElBorde = adapter.findByCriteria(OrdenCompraCriteria.primeraPagina(null, null, null,
                LocalDate.of(2026, 8, 6), LocalDate.of(2026, 8, 6)));

        assertThat(enRango.totalElementos()).isEqualTo(3L);
        assertThat(soloElBorde.totalElementos()).isEqualTo(1L);
        assertThat(soloElBorde.contenido()).singleElement()
                .extracting(OrdenCompra::numero).isEqualTo("OC-2026-000003");
    }

    @Test
    void pagesWithoutRepeatingOrDroppingRows() {
        for (int i = 1; i <= 5; i++) {
            adapter.save(nueva("OC-2026-00000" + i, LocalDate.of(2026, 8, i)));
        }

        var primera = adapter.findByCriteria(new OrdenCompraCriteria(null, null, null, null, null, 0, 2));
        var segunda = adapter.findByCriteria(new OrdenCompraCriteria(null, null, null, null, null, 1, 2));
        var tercera = adapter.findByCriteria(new OrdenCompraCriteria(null, null, null, null, null, 2, 2));

        assertThat(primera.totalElementos()).isEqualTo(5L);
        assertThat(primera.totalPaginas()).isEqualTo(3);
        assertThat(primera.contenido()).hasSize(2);
        assertThat(segunda.contenido()).hasSize(2);
        assertThat(tercera.contenido()).hasSize(1);
        assertThat(primera.contenido()).allSatisfy(orden -> assertThat(orden.items()).hasSize(2));

        List<String> vistos = List.of(primera, segunda, tercera).stream()
                .flatMap(pagina -> pagina.contenido().stream())
                .map(OrdenCompra::numero)
                .toList();
        assertThat(vistos).doesNotHaveDuplicates().hasSize(5);
        assertThat(vistos).isSortedAccordingTo((a, b) -> b.compareTo(a));
    }

    @Test
    void filtersByStateProviderAndSite() {
        adapter.save(nueva("OC-2026-000001", LocalDate.of(2026, 8, 5)));
        adapter.save(adapter.save(nueva("OC-2026-000002", LocalDate.of(2026, 8, 5)))
                .transicionarA(OrdenCompraEstado.APROBADA));

        var aprobadas = adapter.findByCriteria(
                OrdenCompraCriteria.primeraPagina(OrdenCompraEstado.APROBADA, 8, 2, null, null));
        var otraSede = adapter.findByCriteria(
                OrdenCompraCriteria.primeraPagina(null, null, 99, null, null));

        assertThat(aprobadas.contenido()).singleElement()
                .extracting(OrdenCompra::numero).isEqualTo("OC-2026-000002");
        assertThat(otraSede.contenido()).isEmpty();
    }
}
