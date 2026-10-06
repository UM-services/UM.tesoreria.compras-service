package tesoreria.compras.slice.proveedor.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import tesoreria.compras.slice.proveedor.domain.model.CuentaContable;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;
import tesoreria.compras.slice.proveedor.infrastructure.web.dto.CuentaContableResponse;
import tesoreria.compras.slice.proveedor.infrastructure.web.dto.ProveedorResponse;

@Component
public class ProveedorDtoMapper {

    public ProveedorResponse toResponse(Proveedor proveedor) {
        return new ProveedorResponse(
                proveedor.proveedorId(),
                proveedor.cuit(),
                proveedor.nombreFantasia(),
                proveedor.razonSocial(),
                proveedor.ordenCheque(),
                proveedor.domicilio(),
                proveedor.telefono(),
                proveedor.fax(),
                proveedor.celular(),
                proveedor.email(),
                proveedor.emailInterno(),
                proveedor.numeroCuenta(),
                proveedor.habilitado(),
                proveedor.cbu(),
                toCuentaResponse(proveedor.cuenta())
        );
    }

    private CuentaContableResponse toCuentaResponse(CuentaContable cuenta) {
        if (cuenta == null) {
            return null;
        }

        return new CuentaContableResponse(
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
