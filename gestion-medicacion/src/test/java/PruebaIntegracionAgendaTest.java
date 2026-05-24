import com.medistation.controller.AgendaController;
import com.medistation.model.Capsula;
import com.medistation.model.Paciente;
import com.medistation.model.Tratamiento;
import com.medistation.strategy.DetectorSobredosis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class PruebaIntegracionAgendaTest {

    private Paciente paciente;
    private AgendaController agenda;
    private Capsula paracetamol;

    @BeforeEach
    void setUp() {
        paciente = new Paciente("Juan Perez", 45);
        agenda = new AgendaController(paciente);
        agenda.agregarReglaValidacion(new DetectorSobredosis());
        
        paracetamol = new Capsula("Dolex", "paracetamol", 5, 10, 5.0);
    }

    @Test
    void testAgendarTratamientoSeguro() {
        Tratamiento tratamientoSeguro = new Tratamiento(paciente, paracetamol, 8, 1000.0, LocalDateTime.now());
        
        boolean agendadoNormal = agenda.intentarAgendarTratamiento(tratamientoSeguro);
        
        assertTrue(agendadoNormal, "El sistema debe permitir un tratamiento seguro");
        assertEquals(1, paciente.getTratamientos().size(), "El paciente deberia tener 1 tratamiento registrado");
    }

    @Test
    void testBloqueoPorSobredosis() {
        Tratamiento tratamientoBase = new Tratamiento(paciente, paracetamol, 8, 1000.0, LocalDateTime.now());
        agenda.intentarAgendarTratamiento(tratamientoBase);

        Tratamiento tratamientoPeligroso = new Tratamiento(paciente, paracetamol, 6, 1000.0, LocalDateTime.now());
        boolean agendadoSobredosis = agenda.intentarAgendarTratamiento(tratamientoPeligroso);
        
        assertFalse(agendadoSobredosis, "El validador bloquea la sobredosis");
        assertEquals(1, paciente.getTratamientos().size(), "El paciente debe seguir teniendo solo el primer tratamiento");
    }
}