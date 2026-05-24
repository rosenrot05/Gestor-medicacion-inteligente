import com.medistation.controller.PacienteController;
import com.medistation.controller.InventarioController;
import com.medistation.model.GeneradorDatosPrueba;
import com.medistation.model.GestorArchivosTXT;
import com.medistation.view.MainFrame;
import javax.swing.SwingUtilities;

public class App {
    
    public static void main(String[] args) {
        
        //crear los controladores globales (únicos para toda la app)
        PacienteController pacienteCtrl = new PacienteController();
        InventarioController inventarioCtrl = new InventarioController();
        
        //cargar datos del TXT o generar datos de prueba si es la primera vez
        GeneradorDatosPrueba.inicializarSistema(pacienteCtrl, inventarioCtrl);
        
        //configurar el guardado automático al cerrar la inerfaz
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cerrando aplicacion... Guardando absolutamente TODO en TXT.");
            GestorArchivosTXT.guardarDatos(pacienteCtrl, inventarioCtrl);
        }));
        
        //mostrar la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(pacienteCtrl, inventarioCtrl);
            frame.setVisible(true);
        });
    }
}