package tesoreria.compras.ordencompra.domain.model;

public enum OrdenCompraEstado {
    PENDIENTE_DE_APROBAR, APROBADA, ENVIADA, FACTURA_PARCIAL, CUMPLIDA_PARCIAL, CUMPLIDA, ANULADA;

    public boolean permite(OrdenCompraEstado destino) {
        return switch (this) {
            case PENDIENTE_DE_APROBAR -> destino == APROBADA || destino == ANULADA;
            case APROBADA -> destino == ENVIADA || destino == ANULADA;
            case ENVIADA -> destino == FACTURA_PARCIAL || destino == CUMPLIDA_PARCIAL
                    || destino == CUMPLIDA || destino == ANULADA;
            case FACTURA_PARCIAL, CUMPLIDA_PARCIAL -> destino == CUMPLIDA;
            case CUMPLIDA, ANULADA -> false;
        };
    }
}
