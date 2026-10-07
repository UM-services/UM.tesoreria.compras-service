package tesoreria.compras.slice.pedidoCompra.domain.exception;

public class PedidoCompraNotFoundException extends RuntimeException {

    public PedidoCompraNotFoundException(Integer compraPedidoId, Throwable cause) {
        super("No se encontró el pedido de compra " + compraPedidoId, cause);
    }
}
