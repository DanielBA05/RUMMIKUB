package rummikub;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class RegistroJugadores extends JFrame {
    private final List<JTextField> camposJugadores = new ArrayList<>();
    private final JPanel panelCampos = new JPanel(new GridLayout(5, 1, 10, 10));
    private final JSpinner spinnerCantidad;
    public RegistroJugadores() {
        setTitle("Registro de Jugadores");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        //Spinner de cantidad de jugadores
        JPanel spinnerPanel = new JPanel(new FlowLayout());
        spinnerPanel.add(new JLabel("Cantidad de jugadores (2-4):"));
        spinnerCantidad = new JSpinner(new SpinnerNumberModel(2, 2, 4, 1));
        //Desactivar edición por teclado y selección de texto
        JComponent editor = spinnerCantidad.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setEditable(false);
            textField.setFocusable(false);
            textField.setCursor(Cursor.getDefaultCursor());
        }
        spinnerCantidad.addChangeListener(e -> actualizarCampos());
        spinnerPanel.add(spinnerCantidad);
        add(spinnerPanel, BorderLayout.NORTH);
        panelCampos.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(panelCampos, BorderLayout.CENTER);
        JButton btnIniciar = new JButton("Iniciar Juego");
        btnIniciar.addActionListener(this::iniciarJuego);
        add(btnIniciar, BorderLayout.SOUTH);
        actualizarCampos(); //Inicializa los campos para nombre
        setVisible(true);
    }

    private void actualizarCampos() {
        panelCampos.removeAll();
        camposJugadores.clear();
        int cantidad = (int) spinnerCantidad.getValue();
        for (int i = 0; i < cantidad; i++) {
            JTextField campo = new JTextField();
            campo.setToolTipText("Jugador " + (i + 1));
            campo.setBorder(BorderFactory.createTitledBorder("Jugador " + (i + 1)));
            camposJugadores.add(campo);
            panelCampos.add(campo);
        }
        panelCampos.revalidate();
        panelCampos.repaint();
    }

    private void iniciarJuego(ActionEvent e) {
        List<String> nombres = new ArrayList<>();
        for (JTextField campo : camposJugadores) {
            String nombre = campo.getText().trim();
            if (!nombre.isEmpty()) {
                nombres.add(nombre);
            }
        }
        if (nombres.size() < 2) {
            JOptionPane.showMessageDialog(this, 
                "Debe ingresar al menos 2 jugadores.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int partidasATotal = obtenerNumeroPartidas();
        if (partidasATotal <= 0) {
            return;
        }
        dispose();
        SwingUtilities.invokeLater(() -> {
            GestorPartidas gestor = new GestorPartidas(nombres, partidasATotal);
            new RummikubSwing(nombres, gestor);
        });
    }

    private int obtenerNumeroPartidas() {
        Integer[] opciones = {1, 2, 3, 5, 10};
        Object seleccion = JOptionPane.showInputDialog(
            this,
            "Selecciona el número de partidas:",
            "Número de Partidas",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );
        return seleccion != null ? (int) seleccion : 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegistroJugadores::new);
    }
}
