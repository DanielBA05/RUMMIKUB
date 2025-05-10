package rummikub;
import java.util.List;
import java.util.ArrayList;

public class Escalera extends Combinaciones {
    
    public Escalera(List<Ficha> fichas) {
        super(fichas);
    }
    
    private void intercambiar(List<Ficha> fichas, int i, int j) {
        Ficha temp = fichas.get(i);
        fichas.set(i, fichas.get(j));
        fichas.set(j, temp);
    }
    
    private void quicksort(List<Ficha> fichas, int inicio, int fin) {
        if (inicio < fin) {
            Ficha pivote = fichas.get(fin);
            int i = inicio;
            for (int j = inicio; j < fin; j++) {
                if (fichas.get(j).getN() < pivote.getN() || 
                    (fichas.get(j).getN() == pivote.getN() && fichas.get(j).getColor() < pivote.getColor())) {
                    intercambiar(fichas, i, j);
                    i++;
                }
            }
            intercambiar(fichas, i, fin);
            quicksort(fichas, inicio, i - 1);
            quicksort(fichas, i + 1, fin);
        }
    }
    
    @Override
    public boolean esValido() {
        if (!mismoColor()) {
            return false;
        }
        List<Ficha> fichasOrdenadas = new ArrayList<>(fichas);
        quicksort(fichasOrdenadas, 0, cantFichas - 1);
        int comodines = 0;
        int inicio = 0;
        for (Ficha f : fichasOrdenadas) {
            if (f.getN() == 0) {
                comodines++;
                inicio++;
            } else {
                break;
            }
        }
        if (inicio == cantFichas) {
            return false;
        }
        int vacio = 0;
        for (int i = inicio; i < cantFichas - 1; i++) {
            int diferencia = fichasOrdenadas.get(i + 1).getN() - fichasOrdenadas.get(i).getN();
            if (diferencia <= 0) {
                return false;
            }
            vacio += (diferencia - 1);
        }
        return vacio <= comodines;
    }
}