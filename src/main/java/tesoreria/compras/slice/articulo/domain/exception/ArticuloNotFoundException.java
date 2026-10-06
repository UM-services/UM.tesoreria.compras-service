package tesoreria.compras.slice.articulo.domain.exception;

public class ArticuloNotFoundException extends RuntimeException {

    private final Long articuloId;

    public ArticuloNotFoundException(Long articuloId, Throwable cause) {
        super("No existe el artículo con ID " + articuloId, cause);
        this.articuloId = articuloId;
    }

    public Long getArticuloId() {
        return articuloId;
    }
}
