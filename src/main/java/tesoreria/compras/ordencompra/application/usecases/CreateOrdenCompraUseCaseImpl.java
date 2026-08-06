package tesoreria.compras.ordencompra.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.ordencompra.domain.model.*;
import tesoreria.compras.ordencompra.domain.ports.in.CreateOrdenCompraUseCase;
import tesoreria.compras.ordencompra.domain.ports.out.OrdenCompraRepository;
import java.time.LocalDate; import java.util.List;
@Component @RequiredArgsConstructor
public class CreateOrdenCompraUseCaseImpl implements CreateOrdenCompraUseCase {
    private final OrdenCompraRepository repository;
    public OrdenCompra create(LocalDate fecha, Integer proveedorId, Integer sedeId, String observaciones, List<OrdenCompraItem> items) {
        long correlativo = repository.reservarSiguienteNumero(fecha.getYear());
        return repository.save(OrdenCompra.nueva("OC-%d-%06d".formatted(fecha.getYear(), correlativo), fecha, proveedorId, sedeId, observaciones, items));
    }
}
