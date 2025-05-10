package rummikub;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.table.DefaultTableModel;

public class RummikubSwing extends JFrame {
    private int fichaSeleccionadaFila = -1;
    private int fichaSeleccionadaColumna = -1;
    private JPanel tablero;
    private JPanel atril;
    private JPanel botonesPanel;
    private JLabel turnoLabel;
    private JLabel fichasRestantesLabel;
    private FichaSwing fichaSeleccionada = null;
    private final int FILAS = 20;
    private final int COLUMNAS = 20;
    private final JPanel[][] celdas = new JPanel[FILAS][COLUMNAS];
    private final List<FichaSwing> fichasJugador = new ArrayList<>();
    private RummikubController controller;
    private final GestorPartidas gestor;

    public RummikubSwing(List<String> jugadores, GestorPartidas gestor) {
        this.gestor = gestor;
        controller = new RummikubController(jugadores);
        setTitle("Rummikub - Swing Edition");
        setSize(1200, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initTablero();
        initAtril();
        initBotones();
        setVisible(true);
    }

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
                celda.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
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
                        } else if (celda.getComponentCount() > 0) {
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

    private void initBotones() {
        botonesPanel = new JPanel(new FlowLayout());
        turnoLabel = new JLabel("Turno de: " + controller.getJugadorActual().getNombre());
        turnoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        botonesPanel.add(turnoLabel);
        JButton btnComer = new JButton("Comer Ficha");
        btnComer.addActionListener(e -> {
            controller.reiniciarJugada();
            controller.robarFicha();
            refresh();
        });
        JButton btnReiniciar = new JButton("Reiniciar Jugada");
        btnReiniciar.addActionListener(e -> {
            controller.reiniciarJugada();
            refresh();
        });
        JButton btnTerminar = new JButton("Terminar Turno");
        btnTerminar.addActionListener(e -> {
            List<List<Ficha>> combinaciones = obtenerCombinacionesDesdeTablero();
            gestor.partidaFinalizada(controller);
            if (gestor.isPartidaTerminada()) {
                mostrarTablaCalificaciones();
            }
            boolean esValida;
            if (controller.esPrimerTurno()) {
                esValida = controller.validarJugadaInicial();
            } else {
                esValida = controller.validarJugada(combinaciones);
            }
            if (esValida) {
                controller.terminarTurno(combinaciones);
                refresh();
            } else {
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
        add(botonesPanel, BorderLayout.NORTH);
        fichasRestantesLabel = new JLabel("Fichas en mazo: " + controller.getCantidadFichasEnMazo());
        fichasRestantesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        botonesPanel.add(fichasRestantesLabel);
    }

    private void mostrarTablaCalificaciones() {
    //Se crea la tabla y se muestra
    String[] columnas = {"Jugador", "Puntuación Total"};
    DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
    List<Map.Entry<String, Integer>> puntuacionesOrdenadas = new ArrayList<>(
        gestor.getPuntuacionesAcumuladas().entrySet()
    );
    puntuacionesOrdenadas.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
    for (Map.Entry<String, Integer> entry : puntuacionesOrdenadas) {
        modelo.addRow(new Object[]{entry.getKey(), entry.getValue()});
    }
    JTable tabla = new JTable(modelo);
    JScrollPane scrollPane = new JScrollPane(tabla);
    //Se muestra el diálogo con la tabla, junto a las opciones
    String mensaje;
    Object[] opciones;
    if (gestor.quedanPartidas()) {
        mensaje = "Partida " + gestor.getPartidasJugadas() + "/" + gestor.getPartidasTotales() + " - Puntuaciones:";
        opciones = new Object[]{"Siguiente Partida", "Salir"};
    } 
    else {
        mensaje = "¡Torneo completado! Puntuaciones finales:";
        opciones = new Object[]{"Ver Resultados", "Salir"};
    }
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
    //Manejo de respuestas
    if (eleccion == 0) {
        if (gestor.quedanPartidas()) {
            iniciarNuevaPartida(); //Prepara nueva partida
        }
        else {
            //Muestra resultados finales
            mostrarTablaCalificaciones();
        }
    }
    else {
        if (gestor.isPartidaTerminada()) {
            JOptionPane.showMessageDialog(this, 
                "Resultados finales:\n" + gestor.obtenerMarcadorFinal());
        }
        System.exit(0); //Cierra el juego
    }
}
    
    private void refresh() {
        turnoLabel.setText("Turno de: " + controller.getJugadorActual().getNombre());
        atril.removeAll();
        fichasJugador.clear();
        for (Ficha ficha : controller.getFichasJugadorActual()) {
            FichaSwing fichaSwing = new FichaSwing(ficha);
            fichasJugador.add(fichaSwing);
            atril.add(fichaSwing.getPanel());
        }
        atril.revalidate();
        atril.repaint();
        fichasRestantesLabel.setText("Fichas en mazo: " + controller.getCantidadFichasEnMazo());
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

    public void iniciarNuevaPartida() {
        //Obtiene nombres y puntajes de los jugadores sin volver a cambiarlos
        List<String> nombresJugadores = new ArrayList<>(gestor.getPuntuacionesAcumuladas().keySet());
        //Crea nuevo control con los mismos jugadores
        controller = new RummikubController(nombresJugadores);
        //Prepara el gestor para nueva partida
        gestor.prepararNuevaPartida();
        refresh();
        JOptionPane.showMessageDialog(this, 
            "¡Nueva partida iniciada! Partida " + 
            (gestor.getPartidasJugadas() + 1) + "/" + 
            gestor.getPartidasTotales());
    }
    
    private List<List<Ficha>> obtenerCombinacionesDesdeTablero() {
        List<List<Ficha>> combinaciones = new ArrayList<>();
        for (int i = 0; i < FILAS; i++) {
            List<Ficha> filaActual = new ArrayList<>();
            for (int j = 0; j < COLUMNAS; j++) {
                Ficha ficha = controller.getTablero().getFichaEn(i, j);
                if (ficha != null) {
                    filaActual.add(ficha);
                } else {
                    if (filaActual.size() >= 3) {
                        combinaciones.add(new ArrayList<>(filaActual));
                    }
                    filaActual.clear();
                }
            }
        }
        return combinaciones;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegistroJugadores::new);
    }

    static class FichaSwing {
        private final Ficha ficha;
        private final JPanel panel;
        private final JLabel label;
        public FichaSwing(Ficha ficha) {
            this.ficha = ficha;
            label = new JLabel(ficha.esComodin() ? "*" : String.valueOf(ficha.getN()));
            label.setFont(new Font("Arial", Font.BOLD, 18));
            label.setForeground(getColor(ficha.getColor()));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            panel = new JPanel();
            panel.setPreferredSize(new Dimension(50, 70));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            panel.setLayout(new BorderLayout());
            panel.add(label, BorderLayout.CENTER);
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

        public Ficha getFicha() {
            return ficha;
        }

        public JPanel getPanel() {
            return panel;
        }

        public JLabel getLabel() {
            return label;
        }

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

