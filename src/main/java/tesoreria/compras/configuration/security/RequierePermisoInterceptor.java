package tesoreria.compras.configuration.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.PermisoGateway;

import java.util.List;
import java.util.function.Supplier;

/**
 * PEP (Policy Enforcement Point): evalúa {@link RequierePermiso} sobre el handler
 * resuelto antes de ejecutarlo.
 *
 * <p>Opt-in y acotado: si {@code enforce} es {@code false} no hace nada, y aunque
 * esté activo sólo actúa sobre endpoints anotados explícitamente. Los endpoints
 * legacy quedan intactos. Es <b>fail-closed</b>: si core no responde o el usuario
 * no existe, se deniega.
 *
 * <p>El {@link PermisoGateway} se recibe como {@code Supplier} para resolverlo en la
 * primera request: registrarlo en la configuración MVC e inyectarlo directo crea un
 * ciclo de beans con el cliente Feign.
 */
public class RequierePermisoInterceptor implements HandlerInterceptor {

    /** Header transitorio de identidad hasta que exista JWT (M3). */
    public static final String USER_ID_HEADER = "X-User-Id";

    private final Supplier<PermisoGateway> permisoGateway;
    private final boolean enforce;

    public RequierePermisoInterceptor(Supplier<PermisoGateway> permisoGateway, boolean enforce) {
        this.permisoGateway = permisoGateway;
        this.enforce = enforce;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!enforce || !(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequierePermiso requierePermiso = resolveAnnotation(handlerMethod);
        if (requierePermiso == null) {
            return true;
        }

        Long userId = resolveUserId(request);
        if (userId == null) {
            throw new IdentidadRequeridaException();
        }

        String clave = requierePermiso.value();
        try {
            List<String> permisos = permisoGateway.get().getPermisosEfectivos(userId);
            if (permisos == null || !permisos.contains(clave)) {
                throw new PermisoDenegadoException(clave);
            }
        } catch (PermisoDenegadoException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            // Core caído o respuesta ilegible: fail-closed, nunca fail-open.
            throw new PermisoDenegadoException(clave);
        }
        return true;
    }

    private RequierePermiso resolveAnnotation(HandlerMethod handlerMethod) {
        RequierePermiso onMethod = handlerMethod.getMethodAnnotation(RequierePermiso.class);
        return onMethod != null ? onMethod : handlerMethod.getBeanType().getAnnotation(RequierePermiso.class);
    }

    private Long resolveUserId(HttpServletRequest request) {
        String header = request.getHeader(USER_ID_HEADER);
        if (header == null || header.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(header.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
