package rummikub;
import java.util.*;

public class GestorPartidas {
    private final List<String> nombresJugadores; //para obtener y almacenar los nombres de los jugadores aunque se recree el tablero y/o controlador
    private final Map<String, Integer> puntuacionesAcumuladas; // lo mismo que con los nombres, aquí usamos map() porque es más fácil almacenar los puntos así
    private int partidasJugadas; // la cantidad de partidas actuales
    private final int partidasTotales; //cantidad a partidas a alcanzar
    private boolean partidaTerminada; // para usar en el método partida terminada 
// el constructor, recibe del usuario la cantidad de partidas y los nombres de los jugadores
    public GestorPartidas(List<String> nombresJugadores, int partidasTotales) {
        this.nombresJugadores = nombresJugadores; 
        this.puntuacionesAcumuladas = new HashMap<>(); // para administrar las puntuaciones, hay otros métodos pero hash es muuy eficiente en términos O(1)
        this.partidasTotales = partidasTotales; 
        this.partidasJugadas = 0;
        this.partidaTerminada = false;
        for (String nombre : nombresJugadores) { //usamos otra vez map para asignar las puntuaciones a los jugadores 
            puntuacionesAcumuladas.put(nombre, 0);
        }
    }
// función para consultar sí la partida ya está terminada y a su vez proceder con el proceso de terminarla
    public void partidaFinalizada(RummikubController controller) {
        if (partidaTerminada) return; // para evitar bucles
        List<Jugador> jugadores = controller.getJugadores(); //obtener los jugadores del controlador(su estado en cuestión de atril)
        boolean hayGanador = jugadores.stream().anyMatch(j -> j.getFichas().isEmpty()); // checar si algún jugador tiene su lista de fichas vacío)
        boolean montonVacio = controller.getMonton()==0; //para checar el estado del montón del controlador/partida actual
        if (!hayGanador && !montonVacio) {
            return; //No hacer nada si no se cumplen las condiciones
        }
        //Determinar ganador
        Jugador ganador = jugadores.stream()
            .filter(j -> j.getFichas().isEmpty()) //generar al ganador en caso de que haya alguno con su lista de fichas vacía
            .findFirst()
            .orElseGet(() -> jugadores.stream()
                    .min(Comparator.comparingInt(this::calcularPuntajeJugador)) // comparar la puntos de cada jugador en caso de que no haya jugador con atril vacío
                    .orElse(null));
        //Solo continuar si hay un ganador válido
        if (ganador != null) {
        //Actualizar puntuaciones 
            for (Jugador jugador : jugadores) {  //ciclo for para buscar el ganador según los 2 posibles criterios de ganador que determinamos antes
                int puntaje = calcularPuntajeJugador(jugador); 
                int total = puntuacionesAcumuladas.get(jugador.getNombre());
                if (jugador == ganador) {
                    int puntosGanados = jugadores.stream() //buscar los puntos de los jugadores no ganadores
                        .filter(j -> j != jugador)
                        .mapToInt(this::calcularPuntajeJugador) 
                        .sum();
                    puntuacionesAcumuladas.put(jugador.getNombre(), total + puntosGanados); //sumarlos al ganador
                } 
                else {
                    puntuacionesAcumuladas.put(jugador.getNombre(), total - puntaje); //restarlo a los que perdieron
                }
            }
            partidasJugadas++; //acercar partidasjugadas a partidas totales
            partidaTerminada = true; //marcar la partida como jugada para evitar bucles
        }
    }

    public int getPartidasTotales() {
        return this.partidasTotales; //metodo get para las partidas a jugar(necesario para que se muestre en el tablero de la interfaz(rummikubswing) 
    }
    public List<String> getNombresJugadores() {
        return new ArrayList<>(this.puntuacionesAcumuladas.keySet()); //método get para obtener los nombres de los jugadores y sus puntuaciones acumuladas(map)
    }

    public int getPartidasJugadas() { //para obtener las partidas actuales, necesario para la interfaz
        return this.partidasJugadas;
    }
    
    public boolean quedanPartidas() {
        return partidasJugadas < partidasTotales; //método booleano para ir determinando si quedan partidas(true) o si ya se termino el juego(false)
    }

    public String obtenerMarcadorFinal() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== Marcador Final =====\n");
        for (Map.Entry<String, Integer> entry : puntuacionesAcumuladas.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" puntos\n");
        }
        sb.append("Partidas jugadas: ").append(partidasJugadas).append(" de ").append(partidasTotales);
        sb.append("\nGanador final: ").append(obtenerGanadorFinal());
        return sb.toString(); //aquí juntamos todo para tener los datos juntitos en la tabla de la interfaz
    }

    private int calcularPuntajeJugador(Jugador jugador) {
        return jugador.getFichas().stream() 
                .mapToInt(f -> f.esComodin() ? 30 : f.getN())
                .sum(); //se suman los puntos a los jugadores(valor de las fichas de su atril) al ser el final de la partida, si la ficha es comodin vale 30 puntitos
    }
    
    public Map<String, Integer> getPuntuacionesAcumuladas() {
           return Collections.unmodifiableMap(puntuacionesAcumuladas); //obtener las puntuaciones para usarlas en la tabla de puntuaciones
      }
    
    public boolean isPartidaTerminada() {
        return partidaTerminada; //para obtener el valor actual del booleano y usarlo en la interfaz
    }

    public String obtenerGanadorFinal() {
        return puntuacionesAcumuladas.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin ganador"); //obtener el ganador según sus puntos, necesario cuando hay múltiples partidas
    } 

    public void prepararNuevaPartida() {
        this.partidaTerminada = false; //marcar el booleano de partida terminada otra vez en falso para poder crear otro controlador
    }
}
