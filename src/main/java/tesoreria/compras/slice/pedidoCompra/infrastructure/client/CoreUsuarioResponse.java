package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoreUsuarioResponse(
        Long userId,
        String login,
        String nombre,
        Integer dependenciaId,
        Integer geograficaId,
        String sede
) {
}
