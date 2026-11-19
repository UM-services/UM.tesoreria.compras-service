package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoreAutorizanteEnvioResponse(Integer usuarioId, List<Integer> dependenciaIds) {
}
