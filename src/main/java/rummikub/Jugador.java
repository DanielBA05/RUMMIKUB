package rummikub;
import java.util.ArrayList;
import java.util.List;

// Clase que representa a un jugador en el juego de Rummikub
public class Jugador {
    private String nombre;                // Nombre del jugador
    private List<Ficha> fichas;          // Fichas que tiene actualmente el jugador
    private int puntos;                  // Puntos acumulados
    private boolean primeraJugadaHecha = false; // Indica si el jugador ya hizo su jugada inicial

    // Devuelve true si el jugador ya realizó su jugada inicial válida
    public boolean haHechoPrimeraJugada() {
        return primeraJugadaHecha;
    }

    // Establece si el jugador ya hizo su jugada inicial
    public void setPrimeraJugadaHecha(boolean hecha) {
        this.primeraJugadaHecha = hecha;
    }

    // Constructor que inicializa un jugador con su nombre
    public Jugador(String nombre) {
        this.nombre = nombre;
        this.fichas = new ArrayList<>();
        this.puntos = 0;
    }

    // Reemplaza los puntos del jugador por un nuevo valor
    public void setPuntos(int nuevosPuntos) {
        this.puntos = nuevosPuntos;
    }

    // Suma puntos al puntaje actual del jugador
    public void sumarPuntos(int puntosASumar) {
        this.puntos += puntosASumar;
    }

    // Agrega una ficha a la lista del jugador
    public void agregarFicha(Ficha ficha) {
        fichas.add(ficha);
    }

    // Elimina una ficha de la lista del jugador
    public void removerFicha(Ficha ficha) {
        fichas.remove(ficha);
    }

    // Devuelve la lista de fichas actuales del jugador
    public List<Ficha> getFichas() {
        return fichas;
    }

    // Elimina todas las fichas del jugador (reinicio)
    public void resetFichas() {
        this.fichas.clear();
    }

    // Devuelve el nombre del jugador
    public String getNombre() {
        return nombre;
    }

    // Devuelve la cantidad de puntos acumulados
    public int getPuntos() {
        return puntos;
    }
}
