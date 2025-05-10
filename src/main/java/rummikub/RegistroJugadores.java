package rummikub;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

// Clase para registrar jugadores antes de iniciar el juego de Rummikub
public class RegistroJugadores extends JFrame {
    // Lista que almacena los campos de texto donde se ingresan los nombres
    private final List<JTextField> camposJugadores = new ArrayList<>();

    // Panel que contiene los campos de nombre de jugadores
    private final JPanel panelCampos = new JPanel(new GridLayout(5, 1, 10, 10));

    // Componente para seleccionar la cantidad de jugadores
    private final JSpinner spinnerCantidad;

    // Constructor de la ventana de registro
    public RegistroJugadores() {
        setTitle("Registro de Jugadores");
        setSize(400, 300);
        setLocationRelativeTo(null); // Centra la ventana
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel con spinner para elegir la cantidad de jugadores
        JPanel spinnerPanel = new JPanel(new FlowLayout());
        spinnerPanel.add(new JLabel("Cantidad de jugadores (2-4):"));

        // Spinner que permite seleccionar entre 2 y 4 jugadores
        spinnerCantidad = new JSpinner(new SpinnerNumberModel(2, 2, 4, 1));

        // Desactiva la edición manual del spinner por teclado
        JComponent editor = spinnerCantidad.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setEditable(false);         // No editable
            textField.setFocusable(false);        // No se puede enfocar
            textField.setCursor(Cursor.getDefaultCursor()); // Cursor por defecto
        }

        // Actualiza los campos de nombre al cambiar la cantidad de jugadores
        spinnerCantidad.addChangeListener(e -> actualizarCampos());

        spinnerPanel.add(spinnerCantidad);
        add(spinnerPanel, BorderLayout.NORTH);

        // Panel central con los campos para ingresar los nombres
        panelCampos.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(panelCampos, BorderLayout.CENTER);

        // Botón para iniciar el juego
        JButton btnIniciar = new JButton("Iniciar Juego");
        btnIniciar.addActionListener(this::iniciarJuego);
        add(btnIniciar, BorderLayout.SOUTH);

        // Crea los campos iniciales según la cantidad seleccionada
        actualizarCampos();
        setVisible(true);
    }

    // Método que crea dinámicamente los campos de texto según la cantidad de jugadores seleccionada
    private void actualizarCampos() {
        panelCampos.removeAll();       // Limpia campos actuales
        camposJugadores.clear();       // Limpia lista de campos
        int cantidad = (int) spinnerCantidad.getValue();
        for (int i = 0; i < cantidad; i++) {
            JTextField campo = new JTextField();
            campo.setToolTipText("Jugador " + (i + 1)); // Texto de ayuda
            campo.setBorder(BorderFactory.createTitledBorder("Jugador " + (i + 1))); // Título del campo
            camposJugadores.add(campo);
            panelCampos.add(campo);   // Agrega el campo al panel
        }
        panelCampos.revalidate();      // Actualiza el layout
        panelCampos.repaint();         // Repinta el panel
    }

    // Método que se ejecuta cuando el usuario presiona "Iniciar Juego"
    private void iniciarJuego(ActionEvent e) {
        List<String> nombres = new ArrayList<>();

        // Recoge los nombres ingresados por el usuario
        for (JTextField campo : camposJugadores) {
            String nombre = campo.getText().trim();
            if (!nombre.isEmpty()) {
                nombres.add(nombre);
            }
        }

        // Validación: debe haber al menos 2 nombres ingresados
        if (nombres.size() < 2) {
            JOptionPane.showMessageDialog(this, 
                "Debe ingresar al menos 2 jugadores.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Solicita al usuario cuántas partidas desea jugar
        int partidasATotal = obtenerNumeroPartidas();
        if (partidasATotal <= 0) {
            return;
        }

        // Cierra esta ventana e inicia la ventana principal del juego
        dispose();
        SwingUtilities.invokeLater(() -> {
            GestorPartidas gestor = new GestorPartidas(nombres, partidasATotal);
            new RummikubSwing(nombres, gestor);
        });
    }

    // Método que muestra un diálogo para seleccionar el número de partidas del torneo
    private int obtenerNumeroPartidas() {
        Integer[] opciones = {1, 2, 3, 5, 10};
        Object seleccion = JOptionPane.showInputDialog(
            this,
            "Selecciona el número de partidas:",
            "Número de Partidas",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0] // Opción por defecto
        );
        return seleccion != null ? (int) seleccion : 0;
    }

    // Método main que lanza esta ventana como punto de inicio
    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegistroJugadores::new);
    }
}
