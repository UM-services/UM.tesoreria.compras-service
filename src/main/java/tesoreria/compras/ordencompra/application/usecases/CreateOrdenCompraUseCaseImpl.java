package tesoreria.compras.ordencompra.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraItem;
import tesoreria.compras.ordencompra.domain.ports.in.CreateOrdenCompraUseCase;
import tesoreria.compras.ordencompra.domain.ports.out.OrdenCompraRepository;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateOrdenCompraUseCaseImpl implements CreateOrdenCompraUseCase {

    private static final String FORMATO_NUMERO = "OC-%d-%06d";

    private final OrdenCompraRepository repository;

    /**
     * La reserva del correlativo y el alta comparten transacción: si el alta falla, el
     * número vuelve atrás y la serie OC-AAAA-NNNNNN no queda con huecos.
     */
    @Override
    @Transactional
    public OrdenCompra create(LocalDate fecha, Integer proveedorId, Integer sedeId, String observaciones,
                              List<OrdenCompraItem> items) {
        long correlativo = repository.reservarSiguienteNumero(fecha.getYear());
        String numero = FORMATO_NUMERO.formatted(fecha.getYear(), correlativo);
        return repository.save(OrdenCompra.nueva(numero, fecha, proveedorId, sedeId, observaciones, items));
    }
}
