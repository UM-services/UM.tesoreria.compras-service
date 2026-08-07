package tesoreria.compras.ordencompra.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tesoreria.compras.ordencompra.application.service.OrdenCompraService;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraInvalidaException;
import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraCriteria;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;
import tesoreria.compras.ordencompra.infrastructure.web.dto.OrdenCompraRequest;
import tesoreria.compras.ordencompra.infrastructure.web.dto.OrdenCompraResponse;
import tesoreria.compras.ordencompra.infrastructure.web.dto.PaginaOrdenCompraResponse;
import tesoreria.compras.ordencompra.infrastructure.web.mapper.OrdenCompraDtoMapper;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping(OrdenCompraController.RUTA)
@RequiredArgsConstructor
public class OrdenCompraController {

    static final String RUTA = "/api/tesoreria/compras/ordenCompra";
    private static final String ACCIONES = "aprobar|enviar|factura-parcial|cumplida-parcial|cumplida|anular";

    private final OrdenCompraService service;
    private final OrdenCompraDtoMapper mapper;

    @PostMapping
    public ResponseEntity<OrdenCompraResponse> create(@Valid @RequestBody OrdenCompraRequest request) {
        OrdenCompra orden = service.create(request.fechaEmision(), request.proveedorId(), request.sedeId(),
                request.observaciones(), mapper.toItems(request.items()));
        return ResponseEntity.created(URI.create(RUTA + "/" + orden.id())).body(mapper.toResponse(orden));
    }

    @GetMapping("/{id}")
    public OrdenCompraResponse get(@PathVariable Long id) {
        return mapper.toResponse(service.get(id));
    }

    @GetMapping("/numero/{numero}")
    public OrdenCompraResponse getByNumero(@PathVariable String numero) {
        return mapper.toResponse(service.getByNumero(numero));
    }

    /**
     * Siempre paginado. Sin tope, un GET sin filtros traía toda la tabla y sus ítems a memoria.
     */
    @GetMapping
    public PaginaOrdenCompraResponse list(@RequestParam(required = false) OrdenCompraEstado estado,
                                          @RequestParam(required = false) Integer proveedorId,
                                          @RequestParam(required = false) Integer sedeId,
                                          @RequestParam(required = false) LocalDate fechaDesde,
                                          @RequestParam(required = false) LocalDate fechaHasta,
                                          @RequestParam(defaultValue = "0") int pagina,
                                          @RequestParam(defaultValue = OrdenCompraCriteria.TAMANO_POR_DEFECTO_PARAM) int tamano) {
        OrdenCompraCriteria criteria = new OrdenCompraCriteria(estado, proveedorId, sedeId, fechaDesde, fechaHasta,
                pagina, tamano);
        return mapper.toResponse(service.list(criteria));
    }

    @PutMapping("/{id}")
    public OrdenCompraResponse update(@PathVariable Long id, @Valid @RequestBody OrdenCompraRequest request) {
        return mapper.toResponse(service.update(id, request.fechaEmision(), request.proveedorId(), request.sedeId(),
                request.observaciones(), mapper.toItems(request.items())));
    }

    @PostMapping("/{id}/{accion:" + ACCIONES + "}")
    public OrdenCompraResponse transition(@PathVariable Long id, @PathVariable String accion) {
        return mapper.toResponse(service.transition(id, estadoDe(accion)));
    }

    private OrdenCompraEstado estadoDe(String accion) {
        return switch (accion) {
            case "aprobar" -> OrdenCompraEstado.APROBADA;
            case "enviar" -> OrdenCompraEstado.ENVIADA;
            case "factura-parcial" -> OrdenCompraEstado.FACTURA_PARCIAL;
            case "cumplida-parcial" -> OrdenCompraEstado.CUMPLIDA_PARCIAL;
            case "cumplida" -> OrdenCompraEstado.CUMPLIDA;
            case "anular" -> OrdenCompraEstado.ANULADA;
            default -> throw new OrdenCompraInvalidaException("Acción inválida: " + accion);
        };
    }
}
