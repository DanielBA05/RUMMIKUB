package rummikub;
import javax.swing.SwingUtilities;

// Clase principal que inicia la aplicación Rummikub
public class Rummikub {
    public static void main(String[] args) {
        // Lanza la interfaz de registro de jugadores en el hilo de eventos de Swing
        SwingUtilities.invokeLater(RegistroJugadores::new); // Inicia programa
    }
}
