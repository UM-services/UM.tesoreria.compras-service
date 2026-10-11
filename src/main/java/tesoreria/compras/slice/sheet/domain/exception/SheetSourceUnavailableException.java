package tesoreria.compras.slice.sheet.domain.exception;

public class SheetSourceUnavailableException extends RuntimeException {

    public SheetSourceUnavailableException(Throwable cause) {
        super("La generación de la planilla no está disponible", cause);
    }
}
