package tesoreria.compras;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Puerta O1: levanta la aplicación entera —bootstrap.yml, JPA, Feign, caché, actuator y
 * el servidor HTTP— contra un MySQL real, y comprueba que no toca el esquema.
 *
 * Los slices (@DataJpaTest, @WebMvcTest) no sirven para esto: arman un contexto recortado.
 * Sólo @SpringBootTest ejecuta el mismo arranque que corre en producción.
 *
 * Consul queda apagado a propósito: en producción lo hay, y no vamos a levantarlo para
 * verificar que el resto del cableado es correcto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@EnabledIf("hayDocker")
@TestPropertySource(properties = {
        "spring.cloud.consul.enabled=false",
        "spring.cloud.consul.discovery.enabled=false",
        "spring.cloud.consul.config.enabled=false",
        "spring.cloud.service-registry.auto-registration.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none"
})
class ArranqueAplicacionIT {

    @Container
    @ServiceConnection
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4")
            .withInitScript("db/compra-orden-ddl.sql");

    static boolean hayDocker() {
        boolean disponible = DockerClientFactory.instance().isDockerAvailable();
        if (!disponible && (Boolean.parseBoolean(System.getenv("REQUIRE_DOCKER"))
                || Boolean.parseBoolean(System.getProperty("requireDocker")))) {
            throw new IllegalStateException(
                    "REQUIRE_DOCKER=true pero no hay Docker; ver la sección "
                    + "\"Pruebas de integración\" del README.");
        }
        return disponible;
    }

    @LocalServerPort
    private int puerto;

    private RestTestClient http;

    @BeforeEach
    void apuntarAlServidorReal() {
        http = RestTestClient.bindToServer().baseUrl("http://localhost:" + puerto).build();
    }

    @Test
    void levantaElContextoCompleto() {
        assertThat(puerto).isPositive();
    }

    @Test
    void respondeElHealthDeActuator() {
        http.get().uri("/actuator/health").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(cuerpo -> assertThat(cuerpo).contains("\"status\":\"UP\""));
    }

    @Test
    void sirveLaRutaCanonicaDeOrdenCompraDeExtremoAExtremo() {
        http.get().uri("/api/tesoreria/compras/ordenCompra?tamano=5").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(cuerpo -> assertThat(cuerpo)
                        .contains("\"contenido\":[]")
                        .contains("\"totalElementos\":0"));
    }

    @Test
    void devuelve404PorUnaOrdenInexistenteEnLugarDeUn500() {
        http.get().uri("/api/tesoreria/compras/ordenCompra/999999").exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(cuerpo -> assertThat(cuerpo).contains("No existe la orden de compra 999999"));
    }

    @Test
    void rechazaUnTamanoDePaginaAbusivoConUn400() {
        http.get().uri("/api/tesoreria/compras/ordenCompra?tamano=100000").exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void publicaElContratoOpenApi() {
        http.get().uri("/v3/api-docs").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(cuerpo -> assertThat(cuerpo).contains("/api/tesoreria/compras/ordenCompra"));
    }
}
