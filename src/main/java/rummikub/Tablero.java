package rummikub;

public class Tablero {
    public static final int FILAS = 20; //Dimensiones del tablero
    public static final int COLUMNAS = 20;

    private Ficha[][] matriz = new Ficha[FILAS][COLUMNAS];

    public void colocarFichaEn(int fila, int columna, Ficha ficha) { //Coloca ficha "ficha" en posición i, j
        matriz[fila][columna] = ficha;
        if (ficha != null) {
            ficha.setX(columna);
            ficha.setY(fila);
        }
    }

    public Ficha getFichaEn(int fila, int columna) {
        if (fila >= 0 && fila < FILAS && columna >= 0 && columna < COLUMNAS) { //Consigue ficha en i, j lugar
            return matriz[fila][columna];
        }
        return null;
    }

    public boolean estaVacio(int fila, int columna) {
        return getFichaEn(fila, columna) == null;
    }

    public void eliminarFichaEn(int fila, int columna) {
        if (fila >= 0 && fila < FILAS && columna >= 0 && columna < COLUMNAS) { //Vuelve i, j lugar una posición nula (sin ficha)
            matriz[fila][columna] = null;
        }
    }

    public Ficha[][] getMatriz() {
        return matriz;
    }
}
