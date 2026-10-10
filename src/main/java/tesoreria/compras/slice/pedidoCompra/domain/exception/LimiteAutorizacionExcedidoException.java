package tesoreria.compras.slice.pedidoCompra.domain.exception;

import java.math.BigDecimal;

/**
 * El usuario tiene autoridad de presupuesto pero el monto del pedido supera su límite
 * ({@code multiplico × referencia}), o no puede calcularse (sin referencia del ejercicio).
 */
public class LimiteAutorizacionExcedidoException extends RuntimeException {

    private final BigDecimal monto;
    private final BigDecimal limite;

    public LimiteAutorizacionExcedidoException(Integer compraPedidoId, BigDecimal monto, BigDecimal limite) {
        super("El monto " + monto + " supera el límite de autorización"
                + (limite != null ? " (" + limite + ")" : " (límite no disponible)")
                + " para el pedido de compra " + compraPedidoId);
        this.monto = monto;
        this.limite = limite;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public BigDecimal getLimite() {
        return limite;
    }
}
