package rummikub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MontonFichas {
    private List<Ficha> fichas;

    public MontonFichas() {
        fichas = new ArrayList<>();
        inicializarFichas();
    }

    private void inicializarFichas() {
        for (int color = Ficha.ROJO; color <= Ficha.AMARILLO; color++) {
            for (int numero = 1; numero <= 13; numero++) {
                fichas.add(new Ficha(numero, color));
                fichas.add(new Ficha(numero, color));
            }
        }
        //Añade comodines
        fichas.add(new Ficha(0, 0));
        fichas.add(new Ficha(0, 0));
        Collections.shuffle(fichas);
        //borrar despues, es para verificar que se añadan todas las fichas
        System.out.println("Total fichas creadas: " + fichas.size());
        for (int i = 0; i < Math.min(10, fichas.size()); i++) {
            Ficha f = fichas.get(i);
            System.out.println("Ficha " + i + ": Num=" + f.getN() + 
                             ", Color=" + f.getColor() + 
                             ", EsComodin=" + f.esComodin());
        }
    }

    public Ficha robarFicha() {
        if (fichas.isEmpty()) {
            return null;
        }
        Ficha robada = fichas.remove(0);
        System.out.println("Ficha robada - Num: " + robada.getN() + ", Color: " + robada.getColor());
        return robada;
    }

    public int getCantidadFichas() {
        return fichas.size();
    } 
}