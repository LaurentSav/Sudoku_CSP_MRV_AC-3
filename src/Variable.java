import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Variable {

    int number;
    Point p;
    List<Domains> domains;
    List<Constraint> constraints;
    int nbContrainte;

    public Variable(int number, Point p){
        this.p = p;
        this.number = number;
        domains = new ArrayList<Domains>();
        constraints = new ArrayList<Constraint>();
        nbContrainte = 0;
    }


    @Override
    public String toString() {
        return "Variable{" +
                ", p=" + p +
                ", nbContrainte=" + nbContrainte +
                '}';
    }
}
