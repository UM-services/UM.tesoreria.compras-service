package tesoreria.compras.ordencompra.domain.exception;

import java.math.BigDecimal;

/**
 * El total guardado no coincide con la suma de los ítems. Es corrupción de datos, no un
 * error del cliente: no se mapea a 4xx a propósito, para que salga como 500 y se investigue.
 */
public class OrdenCompraInconsistenteException extends IllegalStateException {

    public OrdenCompraInconsistenteException(String numero, BigDecimal guardado, BigDecimal calculado) {
        super("El total guardado de la orden " + numero + " es " + guardado
                + " pero sus ítems suman " + calculado);
    }
}
