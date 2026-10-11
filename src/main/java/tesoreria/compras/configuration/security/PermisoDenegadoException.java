package tesoreria.compras.configuration.security;

/**
 * El usuario no tiene la clave requerida por el endpoint anotado con
 * {@link RequierePermiso}. Se responde {@code 403}. Ver {@link SecurityExceptionHandler}.
 */
public class PermisoDenegadoException extends RuntimeException {

    private final String clave;

    public PermisoDenegadoException(String clave) {
        super("Permiso requerido: " + clave);
        this.clave = clave;
    }

    public String getClave() {
        return clave;
    }
}
