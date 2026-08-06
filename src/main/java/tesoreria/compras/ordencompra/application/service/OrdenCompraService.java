package tesoreria.compras.ordencompra.application.service;
import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service;
import tesoreria.compras.ordencompra.domain.model.*; import tesoreria.compras.ordencompra.domain.ports.in.*;
import java.time.LocalDate; import java.util.List;
@Service @RequiredArgsConstructor
public class OrdenCompraService {
    private final CreateOrdenCompraUseCase createUseCase; private final GetOrdenCompraUseCase getUseCase; private final GetOrdenCompraByNumeroUseCase getByNumeroUseCase; private final ListOrdenCompraUseCase listUseCase; private final UpdateOrdenCompraUseCase updateUseCase; private final TransitionOrdenCompraUseCase transitionUseCase;
    public OrdenCompra create(LocalDate f,Integer p,Integer s,String o,List<OrdenCompraItem> i){return createUseCase.create(f,p,s,o,i);} public OrdenCompra get(Long id){return getUseCase.get(id);} public OrdenCompra getByNumero(String n){return getByNumeroUseCase.getByNumero(n);} public List<OrdenCompra> list(OrdenCompraCriteria c){return listUseCase.list(c);} public OrdenCompra update(Long id,LocalDate f,Integer p,Integer s,String o,List<OrdenCompraItem> i){return updateUseCase.update(id,f,p,s,o,i);} public OrdenCompra transition(Long id,OrdenCompraEstado e){return transitionUseCase.transition(id,e);}
}
