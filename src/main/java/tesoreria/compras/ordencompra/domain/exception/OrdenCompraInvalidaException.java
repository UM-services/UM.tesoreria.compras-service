package tesoreria.compras.ordencompra.domain.exception;

public class OrdenCompraInvalidaException extends IllegalArgumentException {

    public OrdenCompraInvalidaException(String mensaje) {
        super(mensaje);
    }
}
