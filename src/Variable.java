import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Variable {

    int number;
    Point p;
    List<Integer> domains;
    int nbContrainte;
    int heuristic;

    public Variable(int number, Point p){
        this.p = p;
        this.number = number;
        domains = new ArrayList<>();
        nbContrainte = 0;
        int heuristic = 0;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "number=" + number +
                ", p=" + p +
                ", domains=" + domains +
                '}';
    }
}
