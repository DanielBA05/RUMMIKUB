package rummikub;
import java.util.ArrayList;
import java.util.List;

public class Jugador {
    private String nombre;
    private List<Ficha> fichas;
    private int puntos;
    private boolean primeraJugadaHecha = false;

    public boolean haHechoPrimeraJugada() {
        return primeraJugadaHecha;
    }

    public void setPrimeraJugadaHecha(boolean hecha) {
        this.primeraJugadaHecha = hecha;
    }

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.fichas = new ArrayList<>();
        this.puntos = 0;
    }
    
    public void setPuntos(int nuevosPuntos) {
        this.puntos = nuevosPuntos;
    }

    public void sumarPuntos(int puntosASumar) {
        this.puntos += puntosASumar;
    }

    public void agregarFicha(Ficha ficha) {
        fichas.add(ficha);
    }

    public void removerFicha(Ficha ficha) {
        fichas.remove(ficha);
    }

    public List<Ficha> getFichas() {
        return fichas;
    }
    
    public void resetFichas() {
        this.fichas.clear();
    }
    
    public String getNombre() {
        return nombre;
    }

    public int getPuntos() {
        return puntos;
    }
}