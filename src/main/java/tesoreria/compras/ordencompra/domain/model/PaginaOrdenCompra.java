package tesoreria.compras.ordencompra.domain.model;

import tesoreria.compras.ordencompra.domain.exception.OrdenCompraInvalidaException;

import java.util.List;

public record PaginaOrdenCompra(List<OrdenCompra> contenido, int pagina, int tamano, long totalElementos) {

    public PaginaOrdenCompra {
        if (tamano < 1) {
            throw new OrdenCompraInvalidaException("El tamaño de página debe ser al menos 1");
        }
        contenido = List.copyOf(contenido);
    }

    public int totalPaginas() {
        return (int) Math.ceil((double) totalElementos / tamano);
    }
}
