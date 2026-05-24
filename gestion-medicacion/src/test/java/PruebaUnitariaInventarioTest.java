import com.medistation.controller.InventarioController;
import com.medistation.model.Medicamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PruebaUnitariaInventarioTest {

    private InventarioController inventario;

    @BeforeEach
    void setUp() {
        inventario = new InventarioController();
    }

    @Test
    void testRegistrarMedicamentoYVerificarStock() {
        inventario.registrarCapsula("Ibuprofeno", "Ibuprofeno", 2, 10.0);
        Medicamento med = inventario.buscarMedicamento("Ibuprofeno");

        assertNotNull(med, "El medicamento no deberia ser nulo tras registrarse");
        assertEquals(20.0, med.getStock(), "El stock inicial deberia ser 20.0");
    }

    @Test
    void testReabastecerMedicamento() {
        inventario.registrarCapsula("Ibuprofeno", "Ibuprofeno", 2, 10.0);
        
        inventario.reabastecerMedicamento("Ibuprofeno", 5);
        Medicamento med = inventario.buscarMedicamento("Ibuprofeno");

        assertEquals(5, med.getCantidadEnvases(), "Deberia haber 5 envases tras el reabastecimiento");
        assertEquals(50.0, med.getStock(), "El stock total deberia ser 50.0");
    }
}