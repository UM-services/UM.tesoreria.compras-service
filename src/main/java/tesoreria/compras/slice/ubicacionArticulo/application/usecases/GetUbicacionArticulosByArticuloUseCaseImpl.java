package tesoreria.compras.slice.ubicacionArticulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.ubicacionArticulo.domain.model.UbicacionArticulo;
import tesoreria.compras.slice.ubicacionArticulo.domain.ports.in.GetUbicacionArticulosByArticuloUseCase;
import tesoreria.compras.slice.ubicacionArticulo.domain.ports.out.UbicacionArticuloGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetUbicacionArticulosByArticuloUseCaseImpl implements GetUbicacionArticulosByArticuloUseCase {

    private final UbicacionArticuloGateway ubicacionArticuloGateway;

    @Override
    public List<UbicacionArticulo> getByArticulo(Long articuloId) {
        return ubicacionArticuloGateway.getByArticulo(articuloId);
    }
}
