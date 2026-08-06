package tesoreria.compras.ordencompra;
import tesoreria.compras.ordencompra.domain.model.*; import java.math.BigDecimal; import java.time.LocalDate; import java.util.List;
public final class OrdenCompraFixture { private OrdenCompraFixture(){} public static OrdenCompraItem item(){return new OrdenCompraItem(10,"Papel",new BigDecimal("2"),new BigDecimal("12.50"),20L);} public static OrdenCompra pendiente(){return OrdenCompra.nueva("OC-2026-000001",LocalDate.of(2026,8,5),8,2,"notas",List.of(item()));} }
