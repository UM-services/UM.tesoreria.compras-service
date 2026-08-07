package tesoreria.compras.ordencompra.domain.model;

import tesoreria.compras.ordencompra.domain.exception.OrdenCompraInvalidaException;

import java.time.LocalDate;

public record OrdenCompraCriteria(OrdenCompraEstado estado, Integer proveedorId, Integer sedeId,
                                  LocalDate fechaDesde, LocalDate fechaHasta, int pagina, int tamano) {

    public static final int TAMANO_POR_DEFECTO = 50;
    /** Mismo valor que TAMANO_POR_DEFECTO: @RequestParam sólo acepta un String constante. */
    public static final String TAMANO_POR_DEFECTO_PARAM = "50";
    public static final int TAMANO_MAXIMO = 200;

    public OrdenCompraCriteria {
        if (pagina < 0) {
            throw new OrdenCompraInvalidaException("La página no puede ser negativa");
        }
        if (tamano < 1 || tamano > TAMANO_MAXIMO) {
            throw new OrdenCompraInvalidaException("El tamaño de página debe estar entre 1 y " + TAMANO_MAXIMO);
        }
        if (fechaDesde != null && fechaHasta != null && fechaHasta.isBefore(fechaDesde)) {
            throw new OrdenCompraInvalidaException("El rango de fechas está invertido");
        }
    }

    public static OrdenCompraCriteria primeraPagina(OrdenCompraEstado estado, Integer proveedorId, Integer sedeId,
                                                    LocalDate fechaDesde, LocalDate fechaHasta) {
        return new OrdenCompraCriteria(estado, proveedorId, sedeId, fechaDesde, fechaHasta, 0, TAMANO_POR_DEFECTO);
    }
}
