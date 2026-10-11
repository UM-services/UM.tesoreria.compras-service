package tesoreria.compras.slice.pedidoCompra.domain.ports.out;

/**
 * Resuelve el ejercicio en curso desde core, para evaluar el límite de autorización sin
 * depender de que haya pedidos pendientes.
 */
public interface EjercicioActualGateway {

    Integer getEjercicioActualId();
}
