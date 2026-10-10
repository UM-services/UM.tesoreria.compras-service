package tesoreria.compras.configuration.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Exige que el usuario tenga la clave de permiso indicada (convención
 * {@code modulo.accion}) para ejecutar el endpoint anotado.
 *
 * <p><b>Alcance:</b> sólo endpoints NUEVOS de la fachada. Nunca anotar endpoints
 * consumidos por el sistema legacy (VB6/frontends viejos): no envían identidad ni
 * permisos y se romperían.
 *
 * <p>Lo evalúa {@link RequierePermisoInterceptor} contra el bundle efectivo de
 * {@code core-service} ({@link tesoreria.compras.slice.pedidoCompra.domain.ports.out.PermisoGateway}).
 * Hasta que exista identidad real (JWT, M3) la identidad se resuelve de forma
 * transitoria por el header {@code X-User-Id} (spoofable): es plomería de PEP, no
 * seguridad definitiva.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequierePermiso {

    /** Clave de catálogo requerida, p. ej. {@code compras.gastos.crear}. */
    String value();
}
