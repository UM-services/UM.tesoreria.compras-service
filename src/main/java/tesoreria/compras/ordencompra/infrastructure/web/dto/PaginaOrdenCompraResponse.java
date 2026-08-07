package tesoreria.compras.ordencompra.infrastructure.web.dto;

import java.util.List;

public record PaginaOrdenCompraResponse(List<OrdenCompraResponse> contenido, int pagina, int tamano,
                                        long totalElementos, int totalPaginas) {
}
