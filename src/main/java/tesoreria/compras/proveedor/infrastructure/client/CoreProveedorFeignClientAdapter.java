package tesoreria.compras.proveedor.infrastructure.client;

import lombok.RequiredArgsConstructor;
import feign.FeignException;
import org.springframework.stereotype.Component;
import tesoreria.compras.proveedor.domain.model.CuentaContable;
import tesoreria.compras.proveedor.domain.model.Proveedor;
import tesoreria.compras.proveedor.domain.exception.ProveedorNotFoundException;
import tesoreria.compras.proveedor.domain.exception.ProveedorSourceUnavailableException;
import tesoreria.compras.proveedor.domain.ports.out.ProveedorGateway;

@Component
@RequiredArgsConstructor
public class CoreProveedorFeignClientAdapter implements ProveedorGateway {

    private final CoreProveedorFeignClient coreProveedorFeignClient;

    @Override
    public Proveedor getProveedorById(Integer proveedorId) {
        try {
            return toDomain(coreProveedorFeignClient.getProveedorById(proveedorId));
        } catch (FeignException.NotFound exception) {
            throw new ProveedorNotFoundException(proveedorId, exception);
        } catch (FeignException exception) {
            throw new ProveedorSourceUnavailableException(exception);
        }
    }

    private Proveedor toDomain(CoreProveedorResponse response) {
        CuentaContable cuenta = response.cuenta() == null ? null : new CuentaContable(
                response.cuenta().numeroCuenta(),
                response.cuenta().nombre(),
                response.cuenta().integradora(),
                response.cuenta().grado(),
                response.cuenta().grado1(),
                response.cuenta().grado2(),
                response.cuenta().grado3(),
                response.cuenta().grado4(),
                response.cuenta().geograficaId(),
                response.cuenta().fechaBloqueo(),
                response.cuenta().visible(),
                response.cuenta().cuentaContableId()
        );

        return new Proveedor(
                response.proveedorId(),
                response.cuit(),
                response.nombreFantasia(),
                response.razonSocial(),
                response.ordenCheque(),
                response.domicilio(),
                response.telefono(),
                response.fax(),
                response.celular(),
                response.email(),
                response.emailInterno(),
                response.numeroCuenta(),
                response.habilitado(),
                response.cbu(),
                cuenta
        );
    }
}
