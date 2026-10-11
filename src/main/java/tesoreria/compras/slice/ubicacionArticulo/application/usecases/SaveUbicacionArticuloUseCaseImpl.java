package tesoreria.compras.slice.ubicacionArticulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.ubicacionArticulo.domain.model.UbicacionArticulo;
import tesoreria.compras.slice.ubicacionArticulo.domain.ports.in.SaveUbicacionArticuloUseCase;
import tesoreria.compras.slice.ubicacionArticulo.domain.ports.out.UbicacionArticuloGateway;

@Component
@RequiredArgsConstructor
public class SaveUbicacionArticuloUseCaseImpl implements SaveUbicacionArticuloUseCase {

    private final UbicacionArticuloGateway ubicacionArticuloGateway;

    @Override
    public UbicacionArticulo save(UbicacionArticulo ubicacionArticulo) {
        return ubicacionArticuloGateway.save(ubicacionArticulo);
    }
}
