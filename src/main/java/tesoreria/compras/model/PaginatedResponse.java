package tesoreria.compras.model;

import java.util.List;

/**
 * Página de resultados con el mismo contrato JSON que usa {@code core-service}
 * ({@code {data, totalElements, totalPages, currentPage, pageSize}}), para que la
 * fachada no obligue al frontend a cambiar su modelo de paginación.
 */
public record PaginatedResponse<T>(
        List<T> data,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize
) {
}
