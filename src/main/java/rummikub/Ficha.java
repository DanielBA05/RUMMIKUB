package rummikub;

// Clase que representa una ficha del juego Rummikub
public class Ficha {
    private int numero;  // Número de la ficha (1-13, o 0 si es comodín)
    private int color;   // Color de la ficha (representado como entero)
    private int x;       // Posición X en el tablero (columna)
    private int y;       // Posición Y en el tablero (fila)

    // Constantes para los colores disponibles
    public static final int ROJO = 1;
    public static final int AZUL = 2;
    public static final int VERDE = 3;
    public static final int AMARILLO = 4;

    // Constructor principal: crea una ficha con número y color
    public Ficha(int numero, int color) {
        this.numero = numero;
        this.color = color;
        this.x = -1; // Inicialmente sin posición asignada
        this.y = -1;
    }

    // Constructor copia: crea una nueva ficha duplicando los atributos de otra
    public Ficha(Ficha original) {
        this.numero = original.numero;
        this.color = original.color;
        this.x = original.x;
        this.y = original.y;
    }

    // Devuelve el número de la ficha
    public int getN() {
        return numero;
    }

    // Devuelve el color de la ficha
    public int getColor() {
        return color;
    }

    // Verifica si la ficha es un comodín (número y color igual a 0)
    public boolean esComodin() {
        return numero == 0 && color == 0;
    }

    // Establece la coordenada X (columna) de la ficha en el tablero
    public void setX(int x) {
        this.x = x;
    }

    // Establece la coordenada Y (fila) de la ficha en el tablero
    public void setY(int y) {
        this.y = y;
    }

    // Devuelve la coordenada X de la ficha
    public int getX() {
        return x;
    }

    // Devuelve la coordenada Y de la ficha
    public int getY() {
        return y;
    }
}
