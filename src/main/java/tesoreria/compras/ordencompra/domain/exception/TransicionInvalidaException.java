package tesoreria.compras.ordencompra.domain.exception;

import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;

public class TransicionInvalidaException extends IllegalStateException {

    public TransicionInvalidaException(OrdenCompraEstado origen, OrdenCompraEstado destino) {
        super("No se puede pasar de " + origen + " a " + destino);
    }
}
