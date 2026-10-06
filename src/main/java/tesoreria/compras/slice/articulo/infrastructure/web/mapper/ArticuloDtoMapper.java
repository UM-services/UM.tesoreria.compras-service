package tesoreria.compras.slice.articulo.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.model.ArticuloSearch;
import tesoreria.compras.slice.articulo.domain.model.Cuenta;
import tesoreria.compras.slice.articulo.infrastructure.web.dto.ArticuloResponse;
import tesoreria.compras.slice.articulo.infrastructure.web.dto.ArticuloSearchResponse;
import tesoreria.compras.slice.articulo.infrastructure.web.dto.CuentaResponse;

@Component
public class ArticuloDtoMapper {

    public ArticuloResponse toResponse(Articulo articulo) {
        return new ArticuloResponse(
                articulo.articuloId(),
                articulo.nombre(),
                articulo.descripcion(),
                articulo.unidad(),
                articulo.precio(),
                articulo.inventariable(),
                articulo.stockMinimo(),
                articulo.numeroCuenta(),
                articulo.tipo(),
                articulo.directo(),
                articulo.habilitado(),
                toCuentaResponse(articulo.cuenta())
        );
    }

    public ArticuloSearchResponse toSearchResponse(ArticuloSearch articulo) {
        return new ArticuloSearchResponse(
                articulo.articuloId(),
                articulo.nombre(),
                articulo.descripcion(),
                articulo.unidad(),
                articulo.precio(),
                articulo.inventariable(),
                articulo.stockMinimo(),
                articulo.numeroCuenta(),
                toCuentaResponse(articulo.cuenta()),
                articulo.tipo(),
                articulo.directo(),
                articulo.habilitado(),
                articulo.search(),
                articulo.fechaAuditoria(),
                articulo.usuarioAuditoria()
        );
    }

    private CuentaResponse toCuentaResponse(Cuenta cuenta) {
        if (cuenta == null) {
            return null;
        }

        return new CuentaResponse(
                cuenta.numeroCuenta(),
                cuenta.nombre(),
                cuenta.integradora(),
                cuenta.grado(),
                cuenta.grado1(),
                cuenta.grado2(),
                cuenta.grado3(),
                cuenta.grado4(),
                cuenta.geograficaId(),
                cuenta.fechaBloqueo(),
                cuenta.visible(),
                cuenta.cuentaContableId()
        );
    }
}
