package tesoreria.compras.slice.pedidoCompra.domain.exception;

public class PermisoDenegadoException extends RuntimeException {

    public PermisoDenegadoException(String clave) {
        super("Permiso requerido: " + clave);
    }
}
