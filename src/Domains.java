public class Domains {

    int valeur;
    int nbValeurPossible;

    public Domains(int valeurpossible){
        this.valeur = valeurpossible;
        nbValeurPossible = 0;
    }

    @Override
    public String toString() {
        return "Domains{" +
                "valeur=" + valeur +
                ", nbValeurPossible=" + nbValeurPossible +
                '}';
    }
}
