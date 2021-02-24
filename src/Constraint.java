public class Constraint {

    Variable v1;
    Variable v2;

    public Constraint(Variable v1, Variable v2){
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public String toString() {
        return "\nConstraint{" +
                ", v2=" + v2.number +
                '}';
    }
}
