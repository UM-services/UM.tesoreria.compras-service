package tesoreria.compras.slice.pedidoCompra.domain.exception;

public class AccesoPedidoDenegadoException extends RuntimeException {

    public AccesoPedidoDenegadoException(Integer compraPedidoId) {
        super("No tiene acceso al pedido de compra " + compraPedidoId);
    }
}
