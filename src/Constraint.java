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
                "v1 = " + v1.number + " " + v1.p+
                ", v2=" + v2.number + " " + v2.p+
                '}';
    }
}
