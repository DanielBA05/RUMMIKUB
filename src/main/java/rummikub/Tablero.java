package rummikub;

// Clase que representa el tablero de juego de Rummikub
public class Tablero {
    // Constantes que definen el tamaño del tablero
    public static final int FILAS = 20;
    public static final int COLUMNAS = 20;

    // Matriz que almacena las fichas colocadas en el tablero
    private Ficha[][] matriz = new Ficha[FILAS][COLUMNAS];

    // Coloca una ficha en la posición especificada (fila, columna)
    public void colocarFichaEn(int fila, int columna, Ficha ficha) {
        matriz[fila][columna] = ficha;
        if (ficha != null) {
            // Se actualizan las coordenadas internas de la ficha
            ficha.setX(columna);
            ficha.setY(fila);
        }
    }

    // Devuelve la ficha en la posición (fila, columna), o null si está vacía
    public Ficha getFichaEn(int fila, int columna) {
        if (fila >= 0 && fila < FILAS && columna >= 0 && columna < COLUMNAS) {
            return matriz[fila][columna];
        }
        return null;
    }

    // Verifica si una posición específica del tablero está vacía (sin ficha)
    public boolean estaVacio(int fila, int columna) {
        return getFichaEn(fila, columna) == null;
    }

    // Elimina la ficha en la posición (fila, columna), dejándola vacía
    public void eliminarFichaEn(int fila, int columna) {
        if (fila >= 0 && fila < FILAS && columna >= 0 && columna < COLUMNAS) {
            matriz[fila][columna] = null;
        }
    }

    // Devuelve la matriz completa del tablero (puede ser usada para recorrer todo el tablero)
    public Ficha[][] getMatriz() {
        return matriz;
    }
}
