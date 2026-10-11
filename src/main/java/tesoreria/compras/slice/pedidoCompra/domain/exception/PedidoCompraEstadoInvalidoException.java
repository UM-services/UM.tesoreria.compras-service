package tesoreria.compras.slice.pedidoCompra.domain.exception;

public class PedidoCompraEstadoInvalidoException extends RuntimeException {

    public PedidoCompraEstadoInvalidoException(Integer compraPedidoId, Throwable cause) {
        super("El pedido de compra " + compraPedidoId + " no admite la operación en su estado actual", cause);
    }
}
