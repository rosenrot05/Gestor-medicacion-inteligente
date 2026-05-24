import com.medistation.controller.PacienteController;
import com.medistation.model.Paciente;
import com.medistation.model.TipoDiscapacidad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PruebaUnitariaPacienteTest {

    private PacienteController pacienteCtrl;

    @BeforeEach
    void setUp() {
        pacienteCtrl = new PacienteController();
    }

    @Test
    void testRegistrarPaciente() {
        pacienteCtrl.registrarPaciente("Ana Lopez", 30, 65.5);
        assertEquals(1, pacienteCtrl.getPacientesRegistrados().size(), "Deberia haber 1 paciente en la lista");
    }

    @Test
    void testSeleccionarYModificarPerfilClinico() {
        pacienteCtrl.registrarPaciente("Ana Lopez", 30, 65.5);
        
        boolean seleccionado = pacienteCtrl.seleccionarPaciente("Ana Lopez");
        pacienteCtrl.agregarAlergia("Penicilina");
        pacienteCtrl.activarDiscapacidad(TipoDiscapacidad.VISUAL_PARCIAL);
        Paciente activo = pacienteCtrl.getPacienteActual();

        assertTrue(seleccionado, "El sistema deberia poder seleccionar a Ana");
        assertNotNull(activo, "Deberia haber un paciente activo en sesion");
        assertTrue(activo.getPerfil().getAlergias().contains("Penicilina"), "La alergia deberia estar guardada en el perfil");
    }

    @Test
    void testEliminarPacienteYLimpiarSesion() {
        pacienteCtrl.registrarPaciente("Ana Lopez", 30, 65.5);
        pacienteCtrl.seleccionarPaciente("Ana Lopez");
        
        pacienteCtrl.eliminarPaciente("Ana Lopez");

        assertTrue(pacienteCtrl.getPacientesRegistrados().isEmpty(), "La lista de pacientes deberia estar vacia");
        assertNull(pacienteCtrl.getPacienteActual(), "La sesion del paciente deberia haberse limpiado");
    }
}