package tesoreria.compras.ordencompra.domain.exception;

import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;

public class OrdenCompraNoEditableException extends IllegalStateException {

    public OrdenCompraNoEditableException(OrdenCompraEstado estado) {
        super("Sólo se puede editar una orden pendiente de aprobación; su estado es " + estado);
    }
}
