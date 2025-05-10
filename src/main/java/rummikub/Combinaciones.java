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
        int colorBase=fichas.get(0).getColor();
        for (Ficha f : fichas){
            if (f.getColor()!=colorBase){
                return f.getColor()==0;
            }
        }
        return true;
    }
    
    public boolean esCombinacionValida(){
        if (new Serie(fichas).esValido()){
            return true;
        }
        return new Escalera(fichas).esValido();
    }
    
    public boolean esValido() {
        return cantFichas >= 3;
    }

    public void setFichas(List<Ficha> fichas) {
        this.fichas = new ArrayList<>(fichas);
    }

    public void setCantFichas(List<Ficha> fichas) {
        this.cantFichas = fichas.size();
    }

    public List<Ficha> getFichas() {
        return fichas;
    }

    public int getCantFichas() {
        return cantFichas;
    }
}