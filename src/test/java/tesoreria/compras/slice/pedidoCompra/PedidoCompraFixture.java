package tesoreria.compras.slice.pedidoCompra;

import tesoreria.compras.slice.pedidoCompra.domain.model.*;
import tesoreria.compras.slice.pedidoCompra.infrastructure.client.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class PedidoCompraFixture {

    private PedidoCompraFixture() {
    }

    public static PedidoCompraItem item() {
        return new PedidoCompraItem(100, 1, 1, new BigDecimal("5.00"), "Unidad", "Notebook",
                "16 GB RAM, SSD 512 GB", "https://ejemplo.com/notebook");
    }

    public static PedidoCompra pedido() {
        return new PedidoCompra(1, "PC-2026-000001", 7, LocalDateTime.of(2026, 10, 7, 9, 0), "BORRADOR",
                null, 10, 20, 30, 40, "Renovación de equipamiento", LocalDateTime.of(2026, 10, 15, 0, 0),
                false, null, true, new BigDecimal("4500000.00"), null,
                null, null, null, List.of(item()));
    }

    public static PedidoCompra pedidoPendienteEnvio() {
        return new PedidoCompra(1, "PC-2026-000001", 7, LocalDateTime.of(2026, 10, 7, 9, 0), "PENDIENTE_ENVIO",
                null, 10, 20, 30, 40, "Renovación de equipamiento", LocalDateTime.of(2026, 10, 15, 0, 0),
                false, null, true, new BigDecimal("4500000.00"), null,
                null, null, null, List.of(item()));
    }

    public static PedidoCompra pedidoRechazado() {
        return new PedidoCompra(1, "PC-2026-000001", 7, LocalDateTime.of(2026, 10, 7, 9, 0), "RECHAZADO",
                10, 10, 20, 30, 40, "Renovación de equipamiento", LocalDateTime.of(2026, 10, 15, 0, 0),
                false, null, true, new BigDecimal("4500000.00"), null,
                null, "Falta cotización", null, List.of(item()));
    }

    public static PedidoCompra pedidoAjeno() {
        return new PedidoCompra(1, "PC-2026-000001", 7, LocalDateTime.of(2026, 10, 7, 9, 0), "PENDIENTE_ENVIO",
                99, 99, 20, 30, 40, "Renovación de equipamiento", LocalDateTime.of(2026, 10, 15, 0, 0),
                false, null, true, new BigDecimal("4500000.00"), null,
                null, null, null, List.of(item()));
    }

    public static PedidoCompra pedidoPendientePresupuesto() {
        return new PedidoCompra(1, "PC-2026-000001", 7, LocalDateTime.of(2026, 10, 7, 9, 0),
                "PENDIENTE_AUTORIZACION_PRESUPUESTO", 10, 10, 20, 30, 40,
                "Renovación de equipamiento", LocalDateTime.of(2026, 10, 15, 0, 0),
                false, null, true, new BigDecimal("4500000.00"), "Estimación de compras",
                LocalDateTime.of(2026, 10, 8, 12, 0), null, null, List.of(item()));
    }

    public static LimiteAutorizacion limite(BigDecimal limite, boolean ilimitado, boolean tieneAutoridad) {
        return new LimiteAutorizacion(10L, 7, ilimitado ? null : 3, new BigDecimal("1500000.00"),
                limite, ilimitado, tieneAutoridad);
    }

    public static CoreLimiteAutorizacionResponse coreLimiteResponse(BigDecimal limite) {
        return new CoreLimiteAutorizacionResponse(10, 7, 3, new BigDecimal("1500000.00"), limite,
                false, true);
    }

    public static PedidoCompraResumen resumen() {
        return new PedidoCompraResumen(pedidoPendienteEnvio(),
                "Dirección General de Administración", "Usuario Demo");
    }

    public static PedidoCompra pedidoSinIdentidad() {
        return new PedidoCompra(null, null, null, null, null, null, null, null, null, null,
                "Renovación de equipamiento", null, false, null, true, null, null,
                null, null, null, List.of(item()));
    }

    public static PedidoCompraHistorial historial() {
        return new PedidoCompraHistorial(1L, 1, "ENVIADO", 10, null, LocalDateTime.of(2026, 10, 8, 12, 0));
    }

    public static Solicitante solicitante() {
        return new Solicitante(10L, "Usuario Demo", "udemo", 20);
    }

    public static DependenciaInfo dependencia() {
        return new DependenciaInfo(20, "Dirección General de Administración", 30, "Rectorado", 40, "Mendoza");
    }

    public static ContextoInicioPedido contexto() {
        return new ContextoInicioPedido(solicitante(), dependencia());
    }

    public static CoreCompraPedidoItemResponse coreItemResponse() {
        return new CoreCompraPedidoItemResponse(100, 1, 1, new BigDecimal("5.00"), "Unidad", "Notebook",
                "16 GB RAM, SSD 512 GB", "https://ejemplo.com/notebook");
    }

    public static CoreCompraPedidoResponse coreResponse() {
        return new CoreCompraPedidoResponse(1, "PC-2026-000001", 7, LocalDateTime.of(2026, 10, 7, 9, 0),
                "BORRADOR", null, 10, 20, 30, 40, "Renovación de equipamiento",
                LocalDateTime.of(2026, 10, 15, 0, 0), false, null, true, new BigDecimal("4500000.00"),
                null, null, null, null, List.of(coreItemResponse()));
    }

    public static CoreCompraPedidoAutorizanteResponse coreAutorizanteResponse(List<Integer> dependenciaIds) {
        return new CoreCompraPedidoAutorizanteResponse(10, dependenciaIds);
    }

    public static CoreCompraPedidoHistorialResponse coreHistorialResponse() {
        return new CoreCompraPedidoHistorialResponse(1L, 1, "ENVIADO", 10, null,
                LocalDateTime.of(2026, 10, 8, 12, 0));
    }

    public static CoreUsuarioResponse coreUsuarioResponse() {
        return new CoreUsuarioResponse(10L, "udemo", "Usuario Demo", 20, 40, "Mendoza");
    }

    public static CoreDependenciaResponse coreDependenciaResponse() {
        return new CoreDependenciaResponse(20, "Dirección General de Administración", 30, 40,
                new CoreFacultadResponse(30, "Rectorado"), new CoreGeograficaResponse(40, "Mendoza"));
    }
}
