package tesoreria.compras.slice.ubicacionArticulo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.slice.ubicacionArticulo.domain.model.UbicacionArticulo;
import tesoreria.compras.slice.ubicacionArticulo.domain.ports.in.GetUbicacionArticulosByArticuloUseCase;
import tesoreria.compras.slice.ubicacionArticulo.domain.ports.in.SaveUbicacionArticuloUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UbicacionArticuloService {

    private final GetUbicacionArticulosByArticuloUseCase getUbicacionArticulosByArticuloUseCase;
    private final SaveUbicacionArticuloUseCase saveUbicacionArticuloUseCase;

    public List<UbicacionArticulo> getByArticulo(Long articuloId) {
        return getUbicacionArticulosByArticuloUseCase.getByArticulo(articuloId);
    }

    public UbicacionArticulo save(UbicacionArticulo ubicacionArticulo) {
        return saveUbicacionArticuloUseCase.save(ubicacionArticulo);
    }
}
