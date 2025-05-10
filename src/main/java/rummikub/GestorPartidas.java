package rummikub;
import java.util.*;

public class GestorPartidas {
    private final List<String> nombresJugadores;
    private final Map<String, Integer> puntuacionesAcumuladas;
    private int partidasJugadas;
    private final int partidasTotales;
    private boolean partidaTerminada;

    public GestorPartidas(List<String> nombresJugadores, int partidasTotales) {
        this.nombresJugadores = nombresJugadores;
        this.puntuacionesAcumuladas = new HashMap<>();
        this.partidasTotales = partidasTotales;
        this.partidasJugadas = 0;
        this.partidaTerminada = false;
        for (String nombre : nombresJugadores) {
            puntuacionesAcumuladas.put(nombre, 0);
        }
    }

    public void partidaFinalizada(RummikubController controller) {
        if (partidaTerminada) return;
        List<Jugador> jugadores = controller.getJugadores();
        boolean hayGanador = jugadores.stream().anyMatch(j -> j.getFichas().isEmpty());
        boolean montonVacio = controller.getMonton()==0;
        if (!hayGanador && !montonVacio) {
            return; //No hacer nada si no se cumplen las condiciones
        }
        //Determinar ganador
        Jugador ganador = jugadores.stream()
            .filter(j -> j.getFichas().isEmpty())
            .findFirst()
            .orElseGet(() -> jugadores.stream()
                    .min(Comparator.comparingInt(this::calcularPuntajeJugador))
                    .orElse(null));
        //Solo continuar si hay un ganador válido
        if (ganador != null) {
        //Actualizar puntuaciones
            for (Jugador jugador : jugadores) {
                int puntaje = calcularPuntajeJugador(jugador);
                int total = puntuacionesAcumuladas.get(jugador.getNombre());
                if (jugador == ganador) {
                    int puntosGanados = jugadores.stream()
                        .filter(j -> j != jugador)
                        .mapToInt(this::calcularPuntajeJugador)
                        .sum();
                    puntuacionesAcumuladas.put(jugador.getNombre(), total + puntosGanados);
                } 
                else {
                    puntuacionesAcumuladas.put(jugador.getNombre(), total - puntaje);
                }
            }
            partidasJugadas++;
            partidaTerminada = true;
        }
    }

    public int getPartidasTotales() {
        return this.partidasTotales;
    }
    public List<String> getNombresJugadores() {
        return new ArrayList<>(this.puntuacionesAcumuladas.keySet());
    }

    public int getPartidasJugadas() {
        return this.partidasJugadas;
    }
    
    public boolean quedanPartidas() {
        return partidasJugadas < partidasTotales;
    }

    public String obtenerMarcadorFinal() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== Marcador Final =====\n");
        for (Map.Entry<String, Integer> entry : puntuacionesAcumuladas.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" puntos\n");
        }
        sb.append("Partidas jugadas: ").append(partidasJugadas).append(" de ").append(partidasTotales);
        sb.append("\nGanador final: ").append(obtenerGanadorFinal());
        return sb.toString();
    }

    private int calcularPuntajeJugador(Jugador jugador) {
        return jugador.getFichas().stream()
                .mapToInt(f -> f.esComodin() ? 30 : f.getN())
                .sum();
    }
    
    public Map<String, Integer> getPuntuacionesAcumuladas() {
           return Collections.unmodifiableMap(puntuacionesAcumuladas);
      }
    
    public boolean isPartidaTerminada() {
        return partidaTerminada;
    }

    public String obtenerGanadorFinal() {
        return puntuacionesAcumuladas.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin ganador");
    }

    public void prepararNuevaPartida() {
        this.partidaTerminada = false;
    }
}