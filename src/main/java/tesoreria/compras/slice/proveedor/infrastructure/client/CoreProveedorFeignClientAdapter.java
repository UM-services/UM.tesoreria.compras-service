package tesoreria.compras.slice.proveedor.infrastructure.client;

import lombok.RequiredArgsConstructor;
import feign.FeignException;
import org.springframework.stereotype.Component;
import tesoreria.compras.model.PageRequest;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.proveedor.domain.model.CuentaContable;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorConflictException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorCuitNotFoundException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorNotFoundException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorSourceUnavailableException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorValidationException;
import tesoreria.compras.slice.proveedor.domain.ports.out.ProveedorGateway;

import java.util.List;

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

    @Override
    public PaginatedResponse<Proveedor> getPaginated(int page, int size) {
        try {
            PaginatedResponse<CoreProveedorResponse> response = coreProveedorFeignClient.getPaginated(new PageRequest(page, size));
            List<Proveedor> data = response.data() == null ? List.of()
                    : response.data().stream().map(this::toDomain).toList();
            return new PaginatedResponse<>(data, response.totalElements(), response.totalPages(),
                    response.currentPage(), response.pageSize());
        } catch (FeignException exception) {
            throw new ProveedorSourceUnavailableException(exception);
        }
    }

    @Override
    public List<Proveedor> search(List<String> conditions) {
        try {
            return coreProveedorFeignClient.search(conditions).stream().map(this::toDomain).toList();
        } catch (FeignException exception) {
            throw new ProveedorSourceUnavailableException(exception);
        }
    }

    @Override
    public Proveedor getByCuit(String cuit) {
        try {
            return toDomain(coreProveedorFeignClient.getByCuit(cuit));
        } catch (FeignException.NotFound exception) {
            throw new ProveedorCuitNotFoundException(cuit, exception);
        } catch (FeignException exception) {
            throw new ProveedorSourceUnavailableException(exception);
        }
    }

    @Override
    public Proveedor create(Proveedor proveedor) {
        try {
            return toDomain(coreProveedorFeignClient.create(proveedor));
        } catch (FeignException exception) {
            throw translate(exception, null);
        }
    }

    @Override
    public Proveedor update(Integer proveedorId, Proveedor proveedor) {
        try {
            return toDomain(coreProveedorFeignClient.update(proveedorId, proveedor));
        } catch (FeignException exception) {
            throw translate(exception, proveedorId);
        }
    }

    @Override
    public void delete(Integer proveedorId) {
        try {
            coreProveedorFeignClient.delete(proveedorId);
        } catch (FeignException exception) {
            throw translate(exception, proveedorId);
        }
    }

    private RuntimeException translate(FeignException exception, Integer proveedorId) {
        int status = exception.status();
        if (status == 404) {
            return new ProveedorNotFoundException(proveedorId, exception);
        }
        if (status == 409) {
            return new ProveedorConflictException(exception);
        }
        if (status >= 400 && status < 500) {
            return new ProveedorValidationException(exception);
        }
        return new ProveedorSourceUnavailableException(exception);
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
