package rummikub;
import java.util.List;

public class Serie extends Combinaciones{
    
    public Serie(List<Ficha> fichas){
        super(fichas);
    }

    //como las series no deben ser del mismo color, entonces se sobreescribe el método de la superclase para poder hacer que retorne true si hay al menos dos de un mismo color.
    public boolean mismoColor(){
        int[] colores = new int[5]; //como estamos manejando los colores con números, entonces, en el índice del color, se va a ir sumando uno cada que lo vea
        for (Ficha f : fichas){
            int color = f.getColor();
            if (color == 0){ //si es un comodín, básicamente lo ignora
                continue; //según lo que investigué, esto hace que salte a la siguiente vuelta del loop.
            }
            colores[color]++;
            if (colores[color]>=2){ //revisa que no haya más de uno de ese color.
                return true; 
            }
        }
        return false;
    }
    @Override //método heredado y con Override
    public boolean esValido(){
        if (cantFichas>4 || cantFichas<3){ //revisa que la longitud de la serie esté dentro de los parámetros de una
            return false;
        }
        int n = fichas.get(0).getN();
        for (int i = 0 ; i<cantFichas; i++){ //recorre la serie, asegurándose de que todas las fichas tengan el mismo n
            if (fichas.get(i).getN() != n){
                if (fichas.get(i).getN() != 0){
                    return false; //de lo contrario, retorna falso
                }
            }
        }
        return !mismoColor(); //revisa que ninguna ficha sea del mismo color, por eso se niega la proposición
    }
}
