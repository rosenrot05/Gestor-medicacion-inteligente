import com.medistation.controller.PacienteController;
import com.medistation.controller.InventarioController;
import com.medistation.view.MainFrame;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        
        // instanciar controladores globales
        PacienteController pacienteCtrl = new PacienteController();
        InventarioController inventarioCtrl = new InventarioController();

        // iniciar interfaz grafica
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(pacienteCtrl, inventarioCtrl);
            frame.setVisible(true);
        });
    }
}