package tesoreria.compras.slice.pedidoCompra.domain.exception;

public class PedidoCompraSourceUnavailableException extends RuntimeException {

    public PedidoCompraSourceUnavailableException(Throwable cause) {
        super("El servicio de pedidos de compra no está disponible", cause);
    }
}
