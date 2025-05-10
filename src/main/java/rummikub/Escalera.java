package rummikub;
import java.util.List;
import java.util.ArrayList;

public class Escalera extends Combinaciones {
    
    public Escalera(List<Ficha> fichas) {
        super(fichas);
    }

    //Para facilitar el proceso de verificación, un quicksort parecía buena idea.
    private void intercambiar(List<Ficha> fichas, int i, int j) { 
        Ficha temp = fichas.get(i); //se establece un temporal para no perder valores a la hora de intercambiarlos.
        fichas.set(i, fichas.get(j)); //en la posición i, se agregan las cosas que estaban en j
        fichas.set(j, temp); //y en j se agrega el temporal (antes lo que había en i).
    }
    
    private void quicksort(List<Ficha> fichas, int inicio, int fin) {
        if (inicio < fin) { //si el inicio es menor que el fin
            Ficha pivote = fichas.get(fin); //se establece el último como pivote (no hay motivo en específico de por qué el último, parecía que sería más conveniente)
            int i = inicio;
            for (int j = inicio; j < fin; j++) {
                if (fichas.get(j).getN() < pivote.getN()) { //verifica que el número de j sea menor que el pivote.
                    intercambiar(fichas, i, j);
                    i++;
                }
            }
            intercambiar(fichas, i, fin); 
            quicksort(fichas, inicio, i - 1); //va aplicando lo mismo con las otras partes.
            quicksort(fichas, i + 1, fin);
        }
    }
    //Hasta esta parte llega la parte del quicksort.
    
    @Override
    public boolean esValido() {
        if (!mismoColor()) { //las escaleras deben ser del mismo color
            return false;
        }
        List<Ficha> fichasOrdenadas = new ArrayList<>(fichas); //se agregan las fichas.
        quicksort(fichasOrdenadas, 0, cantFichas - 1); //se le aplica quicksort para que sea más fácil evaluarlo.
        int comodines = 0; //cuenta la cantidad de comodines.
        int inicio = 0; //va a marcar donde está el inicio, es decir, descontando los comodines.
        for (Ficha f : fichasOrdenadas) { //se revisa si cada ficha es un comodín.
            if (f.getN() == 0) {
                comodines++; //se le suma al contador de comodines
                inicio++; //se suma uno a la posición de inicio ya que la posición 0 estaría ocupada por el comodín
            } else {
                break;
            }
        }
        if (inicio == cantFichas) { 
            return false;
        }
        int vacio = 0; //se refiere a la cantidad de "espacios" a rellenar.
        for (int i = inicio; i < cantFichas - 1; i++) { //cantFichas - 1 porque vamos a estar evaluando la siguiente posición.
            int diferencia = fichasOrdenadas.get(i + 1).getN() - fichasOrdenadas.get(i).getN(); //hacemos la siguiente - actual
            if (diferencia <= 0) { //si es de cero o menos, no se puede ya que es una escalera.
                return false;
            }
            vacio += (diferencia - 1); 
        }
        return vacio <= comodines; //se retorna si hay suficientes comodines para aplicarle a los espacios vacíos.
    }
}
