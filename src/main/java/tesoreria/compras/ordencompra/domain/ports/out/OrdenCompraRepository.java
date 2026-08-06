package tesoreria.compras.ordencompra.domain.ports.out;

import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraCriteria;
import java.util.List;
import java.util.Optional;

public interface OrdenCompraRepository {
    long reservarSiguienteNumero(int anio);
    OrdenCompra save(OrdenCompra ordenCompra);
    Optional<OrdenCompra> findById(Long id);
    Optional<OrdenCompra> findByNumero(String numero);
    List<OrdenCompra> findByCriteria(OrdenCompraCriteria criteria);
}
