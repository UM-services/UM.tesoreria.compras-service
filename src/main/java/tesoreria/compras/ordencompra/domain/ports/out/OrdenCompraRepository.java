package tesoreria.compras.ordencompra.domain.ports.out;

import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraCriteria;
import tesoreria.compras.ordencompra.domain.model.PaginaOrdenCompra;

import java.util.Optional;

public interface OrdenCompraRepository {

    long reservarSiguienteNumero(int anio);

    OrdenCompra save(OrdenCompra ordenCompra);

    Optional<OrdenCompra> findById(Long id);

    Optional<OrdenCompra> findByIdForUpdate(Long id);

    Optional<OrdenCompra> findByNumero(String numero);

    PaginaOrdenCompra findByCriteria(OrdenCompraCriteria criteria);
}
