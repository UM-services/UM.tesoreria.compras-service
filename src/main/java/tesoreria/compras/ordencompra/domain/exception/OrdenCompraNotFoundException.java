package tesoreria.compras.ordencompra.domain.exception;

public class OrdenCompraNotFoundException extends RuntimeException {
    public OrdenCompraNotFoundException(Object id) {
        super("No existe la orden de compra " + id);
    }
}
