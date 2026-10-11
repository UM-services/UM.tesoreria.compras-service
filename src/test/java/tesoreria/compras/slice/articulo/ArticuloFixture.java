package tesoreria.compras.slice.articulo;

import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.model.ArticuloSearch;
import tesoreria.compras.slice.articulo.domain.model.Cuenta;
import tesoreria.compras.slice.articulo.infrastructure.client.CoreArticuloResponse;
import tesoreria.compras.slice.articulo.infrastructure.client.CoreArticuloSearchResponse;
import tesoreria.compras.slice.articulo.infrastructure.client.CoreCuentaArticuloResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class ArticuloFixture {

    private ArticuloFixture() {
    }

    public static Articulo articulo() {
        return new Articulo(
                101L, "Resma A4", "Resma papel A4 80g", "Un.", new BigDecimal("12500.00"),
                (byte) 1, 10L, new BigDecimal("20101090099"), "A", (byte) 0, (byte) 1, cuenta()
        );
    }

    public static ArticuloSearch articuloSearch() {
        return new ArticuloSearch(
                101L, "Resma A4", "Resma papel A4 80g", "Un.", new BigDecimal("12500.00"),
                (byte) 1, 10L, new BigDecimal("20101090099"), cuenta(), "A", (byte) 0, (byte) 1,
                "Resma A4 Resma papel A4 80g", OffsetDateTime.parse("2026-08-05T10:15:30-03:00"), "ddq"
        );
    }

    public static Cuenta cuenta() {
        return new Cuenta(
                new BigDecimal("20101090099"), "Obligaciones a Pagar", (byte) 0, 5,
                new BigDecimal("20000000000"), new BigDecimal("20100000000"),
                new BigDecimal("20101000000"), new BigDecimal("20101090000"),
                null, null, (byte) 1, 2133L
        );
    }

    public static CoreArticuloResponse coreArticuloResponse() {
        return new CoreArticuloResponse(
                101L, "Resma A4", "Resma papel A4 80g", "Un.", new BigDecimal("12500.00"),
                (byte) 1, 10L, new BigDecimal("20101090099"), "A", (byte) 0, (byte) 1,
                coreCuentaResponse()
        );
    }

    public static CoreArticuloSearchResponse coreArticuloSearchResponse() {
        return new CoreArticuloSearchResponse(
                101L, "Resma A4", "Resma papel A4 80g", "Un.", new BigDecimal("12500.00"),
                (byte) 1, 10L, new BigDecimal("20101090099"), coreCuentaResponse(), "A", (byte) 0, (byte) 1,
                "Resma A4 Resma papel A4 80g", OffsetDateTime.parse("2026-08-05T10:15:30-03:00"), "ddq"
        );
    }

    public static CoreCuentaArticuloResponse coreCuentaResponse() {
        return new CoreCuentaArticuloResponse(
                new BigDecimal("20101090099"), "Obligaciones a Pagar", (byte) 0, 5,
                new BigDecimal("20000000000"), new BigDecimal("20100000000"),
                new BigDecimal("20101000000"), new BigDecimal("20101090000"),
                null, null, (byte) 1, 2133L
        );
    }
}
