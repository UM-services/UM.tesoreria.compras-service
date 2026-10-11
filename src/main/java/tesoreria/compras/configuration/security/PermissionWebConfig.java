package tesoreria.compras.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.PermisoGateway;

/**
 * Registra el interceptor de permisos. Por defecto <b>activo</b>
 * ({@code app.permissions.enforce} / env {@code APP_PERMISSIONS_ENFORCE}); se puede
 * apagar sin desplegar código para volver al comportamiento anterior. Aun activo
 * sólo afecta a endpoints anotados con {@link RequierePermiso}, y esta fachada no
 * tiene endpoints legacy.
 *
 * <p>El {@link PermisoGateway} se inyecta como {@link ObjectProvider} y se resuelve
 * en la primera request (ver {@link RequierePermisoInterceptor}): inyectar el cliente
 * Feign directo acá crearía un ciclo con la configuración MVC.
 */
@Configuration
@ConditionalOnProperty(name = "app.permissions.enforce", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class PermissionWebConfig implements WebMvcConfigurer {

    private final ObjectProvider<PermisoGateway> permisoGatewayProvider;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequierePermisoInterceptor(permisoGatewayProvider::getObject, true));
    }
}
