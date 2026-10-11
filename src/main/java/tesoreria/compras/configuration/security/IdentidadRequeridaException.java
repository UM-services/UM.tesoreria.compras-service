package tesoreria.compras.configuration.security;

/**
 * El endpoint exige una identidad y no llegó el header transitorio
 * {@code X-User-Id}. Se responde {@code 401} (no es un problema de permisos sino
 * de identidad). Ver {@link SecurityExceptionHandler}.
 */
public class IdentidadRequeridaException extends RuntimeException {

    public IdentidadRequeridaException() {
        super("Falta la identidad del usuario (header " + RequierePermisoInterceptor.USER_ID_HEADER + ")");
    }
}
