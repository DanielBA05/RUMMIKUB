package rummikub;

public class Ficha {
    private int numero;
    private int color;
    private int x;
    private int y;

    public static final int ROJO = 1;
    public static final int AZUL = 2;
    public static final int VERDE = 3;
    public static final int AMARILLO = 4;

    public Ficha(int numero, int color) { //Constructor original
        this.numero = numero;
        this.color = color;
        this.x = -1;
        this.y = -1;
    }

    public Ficha(Ficha original) { //Constructor copia
        this.numero = original.numero;
        this.color = original.color;
        this.x = original.x;
        this.y = original.y;
    }

    public int getN() {
        return numero;
    }

    public int getColor() {
        return color;
    }

    public boolean esComodin() {
        return numero == 0 && color == 0;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
