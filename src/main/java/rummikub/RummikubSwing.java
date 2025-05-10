package rummikub;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.table.DefaultTableModel;

// Clase principal de la interfaz Swing del juego Rummikub
public class RummikubSwing extends JFrame {
    // Variables para rastrear ficha seleccionada
    private int fichaSeleccionadaFila = -1;
    private int fichaSeleccionadaColumna = -1;
    
    // Paneles principales de la interfaz
    private JPanel tablero;
    private JPanel atril;
    private JPanel botonesPanel;
    private JLabel turnoLabel;
    private JLabel fichasRestantesLabel;
    
    // Ficha seleccionada actualmente
    private FichaSwing fichaSeleccionada = null;

    // Constantes para tamaño del tablero
    private final int FILAS = 20;
    private final int COLUMNAS = 20;

    // Matriz de paneles que representa las celdas del tablero
    private final JPanel[][] celdas = new JPanel[FILAS][COLUMNAS];

    // Lista de fichas del jugador actual
    private final List<FichaSwing> fichasJugador = new ArrayList<>();

    // Controlador que maneja la lógica del juego
    private RummikubController controller;

    // Gestor de partidas (maneja múltiples partidas en un torneo)
    private final GestorPartidas gestor;

    // Constructor de la interfaz principal
    public RummikubSwing(List<String> jugadores, GestorPartidas gestor) {
        this.gestor = gestor;
        controller = new RummikubController(jugadores);
        setTitle("Rummikub - Swing Edition");
        setSize(1200, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initTablero();   // Inicializa el tablero
        initAtril();     // Inicializa el atril del jugador
        initBotones();   // Inicializa los botones de acción
        setVisible(true);
    }

    // Método que crea e inicializa el tablero de juego
    private void initTablero() {
        tablero = new JPanel(new GridLayout(FILAS, COLUMNAS));
        for (int fila = 0; fila < FILAS; fila++) {
            for (int col = 0; col < COLUMNAS; col++) {
                JPanel celda = new JPanel();
                celda.setPreferredSize(new Dimension(60, 80));
                celda.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                celda.setBackground(Color.LIGHT_GRAY);

                int finalFila = fila;
                int finalCol = col;

                // Listener para manejar clics en cada celda
                celda.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Coloca ficha seleccionada en celda vacía
                        if (fichaSeleccionada != null && celda.getComponentCount() == 0) {
                            celda.add(fichaSeleccionada.getPanel());
                            controller.colocarFichaEnTablero(fichaSeleccionada.getFicha(), finalFila, finalCol);
                            if (fichaSeleccionadaFila != -1 && fichaSeleccionadaColumna != -1) {
                                celdas[fichaSeleccionadaFila][fichaSeleccionadaColumna].removeAll();
                                controller.getTablero().eliminarFichaEn(fichaSeleccionadaFila, fichaSeleccionadaColumna);
                                fichaSeleccionadaFila = -1;
                                fichaSeleccionadaColumna = -1;
                            } else {
                                fichasJugador.remove(fichaSeleccionada);
                            }
                            fichaSeleccionada = null;
                            refresh();
                        } 
                        // Si se hace clic sobre una celda con ficha, selecciona esa ficha
                        else if (celda.getComponentCount() > 0) {
                            Ficha ficha = controller.getTablero().getFichaEn(finalFila, finalCol);
                            if (ficha != null) {
                                fichaSeleccionada = new FichaSwing(ficha);
                                fichaSeleccionadaFila = finalFila;
                                fichaSeleccionadaColumna = finalCol;
                            }
                        }
                    }
                });

                celdas[fila][col] = celda;
                tablero.add(celda);
            }
        }

        JScrollPane tableroScroll = new JScrollPane(tablero);
        tableroScroll.setPreferredSize(new Dimension(1000, 600));
        add(tableroScroll, BorderLayout.CENTER);
    }

    // Método que inicializa el atril del jugador actual
    private void initAtril() {
        atril = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Ficha ficha : controller.getFichasJugadorActual()) {
            FichaSwing fichaSwing = new FichaSwing(ficha);
            fichasJugador.add(fichaSwing);
            atril.add(fichaSwing.getPanel());
        }
        JPanel contenedorScroll = new JPanel(new BorderLayout());
        contenedorScroll.add(atril, BorderLayout.CENTER);
        contenedorScroll.setPreferredSize(new Dimension(2000, 200));

        JScrollPane atrilScroll = new JScrollPane(contenedorScroll);
        atrilScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        atrilScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        atrilScroll.setPreferredSize(new Dimension(1200, 150));
        add(atrilScroll, BorderLayout.SOUTH);
    }

    // Método que crea los botones de acción del juego (comer ficha, reiniciar, terminar turno)
    private void initBotones() {
        botonesPanel = new JPanel(new FlowLayout());

        turnoLabel = new JLabel("Turno de: " + controller.getJugadorActual().getNombre());
        turnoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        botonesPanel.add(turnoLabel);

        // Botón para robar una ficha
        JButton btnComer = new JButton("Comer Ficha");
        btnComer.addActionListener(e -> {
            controller.reiniciarJugada();
            controller.robarFicha();
            refresh();
        });

        // Botón para reiniciar jugada (quita fichas colocadas en el tablero)
        JButton btnReiniciar = new JButton("Reiniciar Jugada");
        btnReiniciar.addActionListener(e -> {
            controller.reiniciarJugada();
            refresh();
        });

        // Botón para terminar el turno actual
        JButton btnTerminar = new JButton("Terminar Turno");
        btnTerminar.addActionListener(e -> {
            List<List<Ficha>> combinaciones = obtenerCombinacionesDesdeTablero();
            gestor.partidaFinalizada(controller);

            // Si ya se terminó el torneo, muestra la tabla final
            if (gestor.isPartidaTerminada()) {
                mostrarTablaCalificaciones();
            }

            boolean esValida;
            // Verifica si la jugada es válida según si es el primer turno o no
            if (controller.esPrimerTurno()) {
                esValida = controller.validarJugadaInicial();
            } else {
                esValida = controller.validarJugada(combinaciones);
            }

            // Si es válida, termina el turno y actualiza tablero
            if (esValida) {
                controller.terminarTurno(combinaciones);
                refresh();
            } else {
                // Muestra mensaje de error
                JOptionPane.showMessageDialog(this,
                    controller.esPrimerTurno()
                        ? "La jugada inicial debe sumar al menos 30 puntos (sin contar comodines) y formar combinaciones válidas."
                        : "La jugada no es válida.",
                    "Jugada Inválida", JOptionPane.ERROR_MESSAGE);
            }
        });

        botonesPanel.add(btnComer);
        botonesPanel.add(btnReiniciar);
        botonesPanel.add(btnTerminar);

        // Etiqueta que muestra cuántas fichas quedan en el mazo
        fichasRestantesLabel = new JLabel("Fichas en mazo: " + controller.getCantidadFichasEnMazo());
        fichasRestantesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        botonesPanel.add(fichasRestantesLabel);

        add(botonesPanel, BorderLayout.NORTH);
    }


    // Muestra una tabla con las puntuaciones de los jugadores
    private void mostrarTablaCalificaciones() {
        // Se define la estructura de la tabla
        String[] columnas = {"Jugador", "Puntuación Total"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        // Ordena las puntuaciones de mayor a menor
        List<Map.Entry<String, Integer>> puntuacionesOrdenadas = new ArrayList<>(
            gestor.getPuntuacionesAcumuladas().entrySet()
        );
        puntuacionesOrdenadas.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Agrega cada entrada a la tabla
        for (Map.Entry<String, Integer> entry : puntuacionesOrdenadas) {
            modelo.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }

        // Muestra la tabla en un JScrollPane
        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);

        // Define mensaje y opciones del diálogo según si hay más partidas
        String mensaje;
        Object[] opciones;
        if (gestor.quedanPartidas()) {
            mensaje = "Partida " + gestor.getPartidasJugadas() + "/" + gestor.getPartidasTotales() + " - Puntuaciones:";
            opciones = new Object[]{"Siguiente Partida", "Salir"};
        } else {
            mensaje = "¡Torneo completado! Puntuaciones finales:";
            opciones = new Object[]{"Ver Resultados", "Salir"};
        }

        // Muestra el diálogo de opciones
        int eleccion = JOptionPane.showOptionDialog(
            this,
            scrollPane,
            mensaje,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        // Manejo de la elección del usuario
        if (eleccion == 0) {
            if (gestor.quedanPartidas()) {
                iniciarNuevaPartida(); // Inicia una nueva partida si quedan
            } else {
                mostrarTablaCalificaciones(); // Muestra resultados finales otra vez
            }
        } else {
            if (gestor.isPartidaTerminada()) {
                // Muestra marcador final antes de cerrar
                JOptionPane.showMessageDialog(this, 
                    "Resultados finales:\n" + gestor.obtenerMarcadorFinal());
            }
            System.exit(0); // Cierra la aplicación
        }
    }

    // Refresca todos los elementos visuales (tablero, atril, etiquetas)
    private void refresh() {
        turnoLabel.setText("Turno de: " + controller.getJugadorActual().getNombre());

        // Refresca atril del jugador
        atril.removeAll();
        fichasJugador.clear();
        for (Ficha ficha : controller.getFichasJugadorActual()) {
            FichaSwing fichaSwing = new FichaSwing(ficha);
            fichasJugador.add(fichaSwing);
            atril.add(fichaSwing.getPanel());
        }
        atril.revalidate();
        atril.repaint();

        // Actualiza etiqueta de fichas restantes
        fichasRestantesLabel.setText("Fichas en mazo: " + controller.getCantidadFichasEnMazo());

        // Refresca el contenido del tablero
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                celdas[i][j].removeAll();
                Ficha f = controller.getTablero().getFichaEn(i, j);
                if (f != null) {
                    FichaSwing fichaSwing = new FichaSwing(f);
                    celdas[i][j].add(fichaSwing.getPanel());
                }
                celdas[i][j].revalidate();
                celdas[i][j].repaint();
            }
        }
    }

    // Inicia una nueva partida del torneo con los mismos jugadores
    public void iniciarNuevaPartida() {
        // Obtiene los nombres originales de los jugadores
        List<String> nombresJugadores = new ArrayList<>(gestor.getPuntuacionesAcumuladas().keySet());

        // Crea un nuevo controlador de partida
        controller = new RummikubController(nombresJugadores);

        // Prepara el gestor para iniciar una nueva partida
        gestor.prepararNuevaPartida();
        refresh();

        // Muestra mensaje indicando el número de partida
        JOptionPane.showMessageDialog(this, 
            "¡Nueva partida iniciada! Partida " + 
            (gestor.getPartidasJugadas() + 1) + "/" + 
            gestor.getPartidasTotales());
    }

    // Obtiene combinaciones (posibles grupos de fichas) colocadas horizontalmente en el tablero
    private List<List<Ficha>> obtenerCombinacionesDesdeTablero() {
        List<List<Ficha>> combinaciones = new ArrayList<>();
        for (int i = 0; i < FILAS; i++) {
            List<Ficha> filaActual = new ArrayList<>();
            for (int j = 0; j < COLUMNAS; j++) {
                Ficha ficha = controller.getTablero().getFichaEn(i, j);
                if (ficha != null) {
                    filaActual.add(ficha);
                } else {
                    if (filaActual.size() == 1 || filaActual.size() == 2){ //Retorna una lista vacía si se encuentra una combinación de menos de 3 fichas
                        return new ArrayList<>();
                    }
                    // Solo se consideran combinaciones con al menos 3 fichas
                    else if (filaActual.size() >= 3) {
                        combinaciones.add(new ArrayList<>(filaActual));
                    }
                    filaActual.clear();
                }
            }
        }
        return combinaciones;
    }

    // Método principal para iniciar la aplicación
    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegistroJugadores::new);
    }

    // Clase interna para representar visualmente una ficha en Swing
    static class FichaSwing {
        private final Ficha ficha;
        private final JPanel panel;
        private final JLabel label;

        public FichaSwing(Ficha ficha) {
            this.ficha = ficha;

            // Crea la etiqueta con el número o comodín
            label = new JLabel(ficha.esComodin() ? "*" : String.valueOf(ficha.getN()));
            label.setFont(new Font("Arial", Font.BOLD, 18));
            label.setForeground(getColor(ficha.getColor()));
            label.setHorizontalAlignment(SwingConstants.CENTER);

            // Crea panel contenedor para la ficha
            panel = new JPanel();
            panel.setPreferredSize(new Dimension(50, 70));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            panel.setLayout(new BorderLayout());
            panel.add(label, BorderLayout.CENTER);
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Permite seleccionar una ficha con clic
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    RummikubSwing window = (RummikubSwing) SwingUtilities.getWindowAncestor(panel);
                    window.fichaSeleccionada = FichaSwing.this;
                    window.fichaSeleccionadaFila = ficha.getY();
                    window.fichaSeleccionadaColumna = ficha.getX();
                }
            });
        }

        // Devuelve la ficha lógica
        public Ficha getFicha() {
            return ficha;
        }

        // Devuelve el panel visual
        public JPanel getPanel() {
            return panel;
        }

        // Devuelve el JLabel que muestra el número
        public JLabel getLabel() {
            return label;
        }

        // Asigna color visual a la ficha según su código
        private Color getColor(int colorCode) {
            return switch (colorCode) {
                case Ficha.ROJO -> Color.RED;
                case Ficha.AZUL -> Color.BLUE;
                case Ficha.VERDE -> Color.GREEN;
                case Ficha.AMARILLO -> Color.ORANGE;
                default -> Color.BLACK;
            };
        }
    }
}
