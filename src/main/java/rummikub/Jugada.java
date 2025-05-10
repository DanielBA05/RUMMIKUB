package rummikub;

import java.util.ArrayList;
import java.util.List;

public class Jugada {
    private List<Combinaciones> combinacionesPropuestas;

    public Jugada() {
        this.combinacionesPropuestas = new ArrayList<>();
    }

    public void agregarCombinacion(Combinaciones combinacion) {
        combinacionesPropuestas.add(combinacion);
    }

    public List<Combinaciones> getCombinacionesPropuestas() {
        return combinacionesPropuestas;
    }

    public boolean validarJugada() {
        for (Combinaciones combinacion : combinacionesPropuestas) {
            if (!combinacion.esValido()) {
                return false;
            }
        }
        return true;
    }

    public void calcularPuntosYActualizarJugador(Jugador jugador) {
        if (!validarJugada()) {
            throw new IllegalStateException("No se puede calcular puntos porque la jugada no es válida.");
        }
        int puntos = 0;
        for (Combinaciones combinacion : combinacionesPropuestas) {
            for (Ficha ficha : combinacion.getFichas()) {
                puntos += ficha.getN();
            }
        }
        jugador.setPuntos(jugador.getPuntos() + puntos);
    }
}
