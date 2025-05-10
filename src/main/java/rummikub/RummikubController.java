package rummikub;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class RummikubController {
    private List<Jugador> jugadores;
    private int jugadorActualIndex;
    private Tablero tablero;
    private MontonFichas monton;
    private Ficha[][] tableroCopiaInicioTurno;
    private List<Ficha> fichasAtrilInicioTurno;
    private Ficha fichaSeleccionada;
    private boolean turnoTerminado;
    private boolean fichaRobada;
    private boolean jugadaValida;
    private final List<Ficha> fichasColocadasEsteTurno = new ArrayList<>();

    public RummikubController(List<String> nombresJugadores) {
        jugadores = new ArrayList<>();
        for (String nombre : nombresJugadores) {
            jugadores.add(new Jugador(nombre));
        }
        tablero = new Tablero();
        monton = new MontonFichas();
        jugadorActualIndex = 0;
        turnoTerminado = false;
        fichaRobada = false;
        jugadaValida = true;
        repartirFichasIniciales();
        guardarEstadoInicialTurno();
    }
    
    public List<Jugador> getJugadores() {
        return Collections.unmodifiableList(jugadores);
    }
    
    private void repartirFichasIniciales() {
        for (Jugador jugador : jugadores) {
            for (int i = 0; i < 14; i++) {
                Ficha ficha = monton.robarFicha();
                if (ficha != null) {
                    jugador.agregarFicha(ficha);
                }
            }
        }
    }

    private void guardarEstadoInicialTurno() {
        tableroCopiaInicioTurno = new Ficha[Tablero.FILAS][Tablero.COLUMNAS];
        for (int i = 0; i < Tablero.FILAS; i++) {
            for (int j = 0; j < Tablero.COLUMNAS; j++) {
                Ficha original = tablero.getFichaEn(i, j);
                if (original != null) {
                    tableroCopiaInicioTurno[i][j] = new Ficha(original);
                }
            }
        }
        fichasAtrilInicioTurno = new ArrayList<>();
        for (Ficha f : getJugadorActual().getFichas()) {
            fichasAtrilInicioTurno.add(new Ficha(f));
        }
    }

    public Jugador getJugadorActual() {
        return jugadores.get(jugadorActualIndex);
    }

    public List<Ficha> getFichasJugadorActual() {
        return getJugadorActual().getFichas();
    }

    public Tablero getTablero() {
        return tablero;
    }

    public Ficha getFichaSeleccionada() {
        return fichaSeleccionada;
    }

    public void setFichaSeleccionada(Ficha ficha) {
        this.fichaSeleccionada = ficha;
    }

    public void colocarFichaEnTablero(Ficha ficha, int fila, int columna) {
        getJugadorActual().removerFicha(ficha);
        ficha.setX(columna);
        ficha.setY(fila);
        tablero.colocarFichaEn(fila, columna, ficha);
        fichasColocadasEsteTurno.add(ficha); 
    }

    public boolean terminarTurno(List<List<Ficha>> mat) {
        if (jugadorColocoAlMenosUnaFicha()) {
            if (validarJugada(mat)) {
                turnoTerminado = true;
                getJugadorActual().setPrimeraJugadaHecha(true);
                siguienteTurno();
                return true;
            }
            return false; //jugada no válida
        } else {
            JOptionPane.showMessageDialog(
                null,
                "No se ha colocado ninguna ficha.",
                "Advertencia",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    public void siguienteTurno() {
        jugadorActualIndex = (jugadorActualIndex + 1) % jugadores.size();
        turnoTerminado = false;
        fichaRobada = false;
        jugadaValida = true;
        fichaSeleccionada = null;
        fichasColocadasEsteTurno.clear();
        guardarEstadoInicialTurno();
        if (getJugadorActual().getFichas().isEmpty()) {
            anunciarGanador(getJugadorActual());
        }
    }

    public void anunciarGanador(Jugador ganador) {
        System.out.println("¡Ganador: " + ganador.getNombre() + "!");
    }

    public boolean validarJugada(List<List<Ficha>> combinaciones) {
        if (combinaciones.isEmpty()){
            return false;
        }
        for (List<Ficha> c : combinaciones){
            Combinaciones combinacion = new Combinaciones(c);
            if (!combinacion.esCombinacionValida()){
                return false;
            }
        }
        return true;
    }

    public void reiniciarJugada() {
        getJugadorActual().getFichas().clear();
        fichasColocadasEsteTurno.clear();
        for (int i = 0; i < Tablero.FILAS; i++) {
            for (int j = 0; j < Tablero.COLUMNAS; j++) {
                tablero.eliminarFichaEn(i, j);
            }
        }
        for (int i = 0; i < Tablero.FILAS; i++) {
            for (int j = 0; j < Tablero.COLUMNAS; j++) {
                Ficha f = tableroCopiaInicioTurno[i][j];
                if (f != null) {
                    tablero.colocarFichaEn(i, j, new Ficha(f));
                }
            }
        }
        getJugadorActual().getFichas().clear();
        for (Ficha f : fichasAtrilInicioTurno) {
            getJugadorActual().agregarFicha(new Ficha(f));
        }
        fichaSeleccionada = null;
    }

    public void robarFicha() {
        if (!turnoTerminado && !fichaRobada) {
            Ficha ficha = monton.robarFicha();
            if (ficha != null) {
                getJugadorActual().agregarFicha(ficha);
                fichaRobada = true;
                jugadaValida = false;
                siguienteTurno();
            }
        }
    }
    
    public int getMonton() {
        return monton.getCantidadFichas(); 
    }

    public boolean validarJugadaInicial() {
        List<List<Ficha>> combinaciones = getCombinacionesDeFichasColocadas();
        int puntos = 0;
        for (List<Ficha> grupo : combinaciones) {
            for (Ficha f : grupo) {
                if (!f.esComodin()) {
                    puntos += f.getN();
                }
            }
        }
        return puntos >= 30 && validarJugada(combinaciones);
    }

    
    public List<List<Ficha>> getCombinacionesDeFichasColocadas() {
        List<List<Ficha>> combinaciones = new ArrayList<>();
        boolean[][] visitado = new boolean[Tablero.FILAS][Tablero.COLUMNAS];
        for (Ficha ficha : fichasColocadasEsteTurno) {
            int fila = ficha.getY();
            int col = ficha.getX();
            if (!visitado[fila][col]) {
                List<Ficha> filaCombinacion = new ArrayList<>();
                int c = col;
                while (c >= 0 && tablero.getFichaEn(fila, c) != null) c--;
                c++;
                while (c < Tablero.COLUMNAS && tablero.getFichaEn(fila, c) != null) {
                    Ficha f = tablero.getFichaEn(fila, c);
                    filaCombinacion.add(f);
                    visitado[fila][c] = true;
                    c++;
                }
                else if (filaCombinacion.size() >= 3) {
                    combinaciones.add(filaCombinacion);
                }
            }
        }
        return combinaciones;
    }

    public boolean jugadorColocoAlMenosUnaFicha() {
        int fichasIniciales = fichasAtrilInicioTurno.size();
        int fichasActuales = getJugadorActual().getFichas().size();
        return fichasActuales < fichasIniciales;
    }
    
    public int getCantidadFichasEnMazo() {
        return monton.getCantidadFichas();
    }
    
    public boolean esPrimerTurno() {
        return !getJugadorActual().haHechoPrimeraJugada();
    }
}
