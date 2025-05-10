package rummikub;
import javax.swing.SwingUtilities;

public class Rummikub {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegistroJugadores::new); //Inicia programa
    }
}

