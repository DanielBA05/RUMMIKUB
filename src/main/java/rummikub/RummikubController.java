package rummikub;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class RummikubController {
    private List<Jugador> jugadores; //lista con los nombres de los jugadores en la partida
    private int jugadorActualIndex; //entero para llevar cuenta de a quien le corresponde el turno
    private Tablero tablero; //objeto de clase tablero para llevar la partida
    private MontonFichas monton; //objeto monton de fichas de la partida
    private Ficha[][] tableroCopiaInicioTurno; // una copia para poder restablecer el tablero a como estaba al inicio del turno en caso de que se requiera restablecer(reiniciar tablero)
    private List<Ficha> fichasAtrilInicioTurno; // lo mismo que el tablero pero ahora aplicado al atril
    private Ficha fichaSeleccionada; //objeto tipo ficha usada para tener margen de la ficha en actual uso 
    private boolean turnoTerminado; // booleano para validar turnos
    private boolean fichaRobada; //booleano para comprobar si el jugador del turno actual ha robado ficha
    private boolean jugadaValida; // booleano para ir actualizando dependiendo de si la jugada acaba siendo válida o no(empieza en true)
    private final List<Ficha> fichasColocadasEsteTurno = new ArrayList<>(); // para tener constancia de las fichas del turno actual, esto sirve para evitar errores de lógica 

    public RummikubController(List<String> nombresJugadores) {//constructor
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
    
    public List<Jugador> getJugadores() { //método get para pasar el nombre de los jugadores a otras clases
        return Collections.unmodifiableList(jugadores);
    }
    
    private void repartirFichasIniciales() { //método para entregar las 14 fichas iniciales a cada jugador, agarrandolas del tablero
        for (Jugador jugador : jugadores) {
            for (int i = 0; i < 14; i++) {
                Ficha ficha = monton.robarFicha();
                if (ficha != null) {
                    jugador.agregarFicha(ficha);
                }
            }
        }
    }

    private void guardarEstadoInicialTurno() { //para  actualizar tablerocopiaInicioTurno
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

    public Jugador getJugadorActual() { //método para obtener el jugador del turno actual
        return jugadores.get(jugadorActualIndex);
    }

    public List<Ficha> getFichasJugadorActual() { //método para obtener las fichas del jugador que está en su turno
        return getJugadorActual().getFichas();
    }

    public Tablero getTablero() { //método get para obtener el tablero
        return tablero;
    }

    public Ficha getFichaSeleccionada() { //método get para obtener la ficha que está siendo usada
        return fichaSeleccionada;
    }

    public void setFichaSeleccionada(Ficha ficha) { //método set para cambiar el valor de fichaseleccionada por un nuevo objeto del tipo ficha
        this.fichaSeleccionada = ficha;
    }

    public void colocarFichaEnTablero(Ficha ficha, int fila, int columna) { // método para colocar una ficha del tablero mientras se quita del atril del jugador actual
        getJugadorActual().removerFicha(ficha);
        ficha.setX(columna);
        ficha.setY(fila);
        tablero.colocarFichaEn(fila, columna, ficha);
        fichasColocadasEsteTurno.add(ficha); 
    }

    public boolean terminarTurno(List<List<Ficha>> mat) { //método booleano para actualizar turnoterminado y pasar al siguiente turno, también valida si el jugador perdió fichas(que jugó)
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

    public void siguienteTurno() { //método que resetea todos los métodos y actualiza el index para poder ir correctamente al siguiente turno
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

    public void anunciarGanador(Jugador ganador) { //método que recibe un jugador y retorna ¡Ganador: jugador!
        System.out.println("¡Ganador: " + ganador.getNombre() + "!");
    }

    public boolean validarJugada(List<List<Ficha>> combinaciones) { //método que aprovecha métodos de combinaciones y sus subclases para determinar si la jugada fué válida
        if (combinaciones.isEmpty()){
            return false;
        }
        for (List<Ficha> c : combinaciones){
            Combinaciones combinacion = new Combinaciones(c); //ciclo for para ir validando las nuevas combinaciones cree el usuario
            if (!combinacion.esCombinacionValida()){
                return false;
            }
        }
        return true;
    }

    public void reiniciarJugada() {  //método que aprovecha fichascolocadasesteturno para poder reiniciar correctamente el tablero, también devuelve las fichas al atril del jugador
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
                siguienteTurno(); //roba una ficha y se la quita al montón, si se usa, se pasa de turno automaticamente
            }
        }
    }
    
    public int getMonton() {
        return monton.getCantidadFichas(); //para obtener el número de fichas del montón
    }

    public boolean validarJugadaInicial() { //para validar la jugada de 30 puntos 
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

    
    public List<List<Ficha>> getCombinacionesDeFichasColocadas() { //método para obtener las combinaciones colocadas en el tablero, usando las fichascolocadasesteturno
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
                if (filaCombinacion.size() >= 3) {
                    combinaciones.add(filaCombinacion);
                }
            }
        }
        return combinaciones;
    }

    public boolean jugadorColocoAlMenosUnaFicha() { //método necesario para validar que el jugador hay usado aunque sea una ficha antes de pasar el turno
        int fichasIniciales = fichasAtrilInicioTurno.size();
        int fichasActuales = getJugadorActual().getFichas().size();
        return fichasActuales < fichasIniciales;
    }
    
    public int getCantidadFichasEnMazo() { //método para obtener la cantidad de fichas del monton
        return monton.getCantidadFichas();
    }
    
    public boolean esPrimerTurno() {
        return !getJugadorActual().haHechoPrimeraJugada(); //método para comprobar si el jugador ya hizo su primer turno
    }
}
