package rummikub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Clase que representa el montón de fichas disponibles para robar en el juego
public class MontonFichas {
    private List<Ficha> fichas; // Lista que contiene todas las fichas disponibles en el montón

    // Constructor que inicializa el montón al crear la instancia
    public MontonFichas() {
        fichas = new ArrayList<>();
        inicializarFichas(); // Genera y mezcla todas las fichas
    }

    // Crea todas las fichas del juego y las mezcla aleatoriamente
    private void inicializarFichas() {
        for (int color = Ficha.ROJO; color <= Ficha.AMARILLO; color++) {
            for (int numero = 1; numero <= 13; numero++) {
                // Cada combinación de número y color se repite dos veces
                fichas.add(new Ficha(numero, color));
                fichas.add(new Ficha(numero, color));
            }
        }

        // Añade dos comodines al montón
        fichas.add(new Ficha(0, 0));
        fichas.add(new Ficha(0, 0));

        // Mezcla aleatoriamente las fichas
        Collections.shuffle(fichas);

        // Mensajes para depuración (pueden ser eliminados más adelante)
        System.out.println("Total fichas creadas: " + fichas.size());
        for (int i = 0; i < Math.min(10, fichas.size()); i++) {
            Ficha f = fichas.get(i);
            System.out.println("Ficha " + i + ": Num=" + f.getN() + 
                             ", Color=" + f.getColor() + 
                             ", EsComodin=" + f.esComodin());
        }
    }

    // Método para robar una ficha del montón (quita la primera)
    public Ficha robarFicha() {
        if (fichas.isEmpty()) {
            return null; // Retorna null si no quedan fichas
        }
        Ficha robada = fichas.remove(0);
        System.out.println("Ficha robada - Num: " + robada.getN() + ", Color: " + robada.getColor());
        return robada;
    }

    // Devuelve cuántas fichas quedan en el montón
    public int getCantidadFichas() {
        return fichas.size();
    } 
}
