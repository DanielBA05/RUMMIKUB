package rummikub;
import java.util.ArrayList;
import java.util.List;

public class Combinaciones {
    protected List<Ficha> fichas;
    protected int cantFichas;

    public Combinaciones(List<Ficha> fichasP) {
        setFichas(fichasP);
        setCantFichas(fichasP);
    }

    public boolean mismoColor(){
        int colorBase = fichas.get(0).getColor(); //usamos el primero como color de referencia.
        for (Ficha f : fichas){
            if (f.getColor() != colorBase){ //compara este con el color de cada ficha.
                return f.getColor() == 0; //si el color es 0, entonces todo bien porque es un comodín.
            }
        }
        return true; //si no encuentra nada raro, retorna true.
    }
    
    public boolean esCombinacionValida(){ //revisa primero si es válido/calza con las características de serie. Sino, revisa las de escalera.
        if (new Serie(fichas).esValido()){
            return true;
        }
        return new Escalera(fichas).esValido();
    }
    
    public boolean esValido() { //una condición importante es que tengan al menos tres fichas.
        return cantFichas >= 3;
    }

    //Métodos set y get.
    public void setFichas(List<Ficha> fichas) {
        this.fichas = new ArrayList<>(fichas);
    }

    public void setCantFichas(List<Ficha> fichas) { //cantFichas se refiera al tamaño, entonces solo se le saca size a la lista.
        this.cantFichas = fichas.size();
    }

    public List<Ficha> getFichas() {
        return fichas;
    }

    public int getCantFichas() {
        return cantFichas;
    }
}
