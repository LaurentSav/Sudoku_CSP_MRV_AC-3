

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class Sudoku {

    private int taille = 9;
    private int[][] grille;

    /* Liste des variables non assignées (case vide) */
    private ArrayList<Variable> unassigned;
    /* Liste des variables assignées (case non-vide) */
    private ArrayList<Variable> assigned;
    /* Liste des variables ayant le plus petit domaines */
    private ArrayList<Variable> bestVariable;
    /* Compteur de récursion */
    private int cpt;

    public Sudoku(String name){
        grille = new int[taille][taille];
        loadSudoku(name);
        update();
        backtracking();
    }

    /* Mise à jour des variables, contraintes, domaines et triage des variables en fonction de MRV ou MRV+Degree Heuristic */
    public void update(){
        unassigned = new ArrayList<>();
        assigned = new ArrayList<>();
        bestVariable = new ArrayList<>();
        updateVariable();
        updateConstraint();
        updateDomains();
        chooseVariable();
    }

    /* Mise à jour des variables, on parcourt la grille du sudoku puis
    on stock les variables non assigné (case vide) et assigné (case non vide) dans deux listes différentes (unassigned et assagnied) */
    public void updateVariable(){
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                if(grille[i][j] == 0){
                    unassigned.add(new Variable(grille[i][j],new Point(i,j)));
                }else{
                    assigned.add(new Variable(grille[i][j], new Point(i,j)));
                }
            }
        }
    }

    /* Mise à jour des domaines de chaque variable non assignés
    *  en testant les valeurs possibles d'une case dans un sudoku (1 à 9)
    *  Puis on compare ces valeurs aux différentes contraintes de
    *  la variable grâce à checkConstraint().
    *
    * */
    public void updateDomains(){
        for(Variable variable : unassigned){
            for (int i = 1; i < taille + 1; i++) {
                Variable vTemp = new Variable(i,variable.p);
                if(checkConstraint(variable, vTemp)){
                    variable.domains.add(new Domains(i));
                }
            }
        }
        // AC3 n'a pas l'air de marcher!
        AC3();
    }

    /* Une variable contient une liste de contraintes
    *  Chaque élément de la liste est un couple de variable (Contrainte Binaire)
    *  Dans cette fonction on regarde tout simplement si le chiffre de la variable 1 est
    *  la même que la variable 2.
    *  Si le chiffre est le même alors la contrainte n'est pas respecté et retourne donc false
    *  sinon elle retourne true, ce qui veut dire que toutes les contraintes de colonne, de ligne et de sous-carré sont respectés
    *
    * */
    public boolean checkConstraint(Variable var1, Variable var2){
        for(Constraint constraint : var1.constraints){
            if(constraint.v2.number == var2.number){
                return false;
            }
        }
        return true;
    }


    /*
    * Ajout des contraintes binaire dans la liste des contraintes d'une variable
    * Les contraintes sont de ligne, de colonne, et de sous-carrés
    * Chaque variable non assignés aura sa liste des contraintes mis à jour
    *
    * */
    public void updateConstraint(){

        for(Variable variable : unassigned){
            //Check Column
            for (int i = 0; i < taille; i++) {
                if(grille[i][variable.p.y] != 0){
                    variable.constraints.add(new Constraint(variable, checkVariable(i,variable.p.y)));
                }
            }
            // Check Row
            for (int i = 0; i < taille; i++) {
                if(grille[variable.p.x][i] != 0){
                    variable.constraints.add(new Constraint(variable, checkVariable(variable.p.x,i)));
                }
            }
            // Check Square
            int row = variable.p.x - variable.p.x%3;
            int col = variable.p.y - variable.p.y%3;

            for (int i = row; i < 3; i++) {
                for (int j = col; j < 3; j++){
                    if(grille[i][j] != 0){
                        variable.constraints.add(new Constraint(variable, checkVariable(i,j)));
                    }
                }
            }
        }
    }


    public Variable checkVariable(int i, int j){
        for(Variable variable : assigned){
            if(variable.p.x == i && variable.p.y == j){
                return variable;
            }
        }
        return null;
    }

    /* Backtracking */

    public boolean backtracking(){
        Variable variable = null;
        this.cpt = 1;
        update();
        return backtrackingRecursion(variable);

    }

    /* Backtraking Récursion */
    public boolean backtrackingRecursion(Variable variable){

        cpt++;
        System.out.println("Récursion : " +cpt);
        System.out.println(this);

        /* Si grille pleine, alors fin de la récursion et donc du backtracking */
        if(checkFull()){
            return true;
        }
        System.out.println(bestVariable);
        // On selectionne la première valeur de la liste des variables non assignés
        Variable v1 = bestVariable.remove(0);

        // LeastConstraining Method
        LeastConstraining(v1);


        // Pour chaque valeur du domaines de la variable sélectionné
        for(Domains i : v1.domains){
            Variable v2 = v1;
            v2.number = i.valeur;
            // On regarde si la valeur i est consistante avec les différentes contraintes de la variable sélectionné
           if(checkConstraint(v1, v2)){
               // Contraintes sont respectés, on met à jour la grille du sudoku avec la nouvelle valeur
               // puis on met à jour les variables, les contraintes, les domaines de chaque variable non assigné
               grille[v1.p.x][v1.p.y] = v2.number;
               update();

               // Récursion
               if(backtrackingRecursion(v2)){
                   return true;
               }

               // La récursion n'a pas fonctionné, donc backtracking
               // On remet à jour la grille du sudoku avec la nouvelle valeur et on met à jour les variables,
               // les contraintes, les domaines de chaque variable non assigné
               grille[v1.p.x][v1.p.y] = 0;
               update();
           }
        }
        return false;
    }

    /*
    * Choix de la prochaine variable mais en réalité nous faisons un triage de la liste des variables non assignés
    * car dans la fonction backtracking récursion, nous prenons le premier élement de la liste
    *
    * */

    public void chooseVariable(){

        // Utilisation de MRV (triage de la liste en fonction de la taille du domaine
        MRV();

        // Vérification dans la liste des variables non assignées que le second élément a oui ou non la même taille de domaine
        // que le premier élément
        // Si vrai alors on ajoute cette variable dans la liste des variables ayant le même domaine
        for(Variable variable : unassigned){
            if(unassigned.get(0).domains.size() == variable.domains.size()){
                bestVariable.add(variable);
            }
        }

        // Utilisation de Degree Heuristic sur la liste des variables ayant le même domaine
        // Degree Heuristic fera le tri de cette liste selon le nombre de contraintes sur les variables restantes
        if(unassigned.size() > 1){
            if(unassigned.get(0).domains.size() == unassigned.get(1).domains.size()){
                DegreeHeuristic();
            }
        }

    }

    /* MRV() Simple tri de la liste des variables non assignés en fonction de la taille du domaine (du nombre des valeurs possibles de la variable) */
    public void MRV(){
        unassigned.sort(Comparator.comparing(a -> a.domains.size()));
    }

    /*
    * Degree Heuristic va calculer puis assigner à toutes les variables le nombre de contraintes
    * une variable a sur les autres variables
    *
    * Pour calculer ce nombre, on regarde simple dans chaque ligne, colonne et sous carré où se trouve la variable actuelle
    * s'il y'a une case vide ( modélisé par 0 )
    * Puis une fois que toutes les variables ont reçu leur valeurs, on trie la liste 'BestVariables' en fonction de cette valeurs
    *
    * A la fin, normalement le premier élément de la liste 'BestVariables' sera l'élément ayant le plus petit domaines mais aussi ayant le plus
    * grand nombre de contraintes sur les variables restantes (case vide)
    * */
    public void DegreeHeuristic(){
        for(Variable variable : bestVariable){
            int compteur = -1;

            // Check Column
            for (int i = 0; i < taille; i++) {
                if(grille[i][variable.p.y] == 0){
                    compteur++;
                }
            }

            // Check Row
            for (int j = 0; j < taille; j++) {
                if(grille[variable.p.x][j] == 0){
                    compteur++;
                }
            }

            // Check Square
            int row = variable.p.x - variable.p.x%3;
            int col = variable.p.y - variable.p.y%3;

            for (int i = row; i < 3; i++) {
                for (int j = col; j < 3; j++){
                    if(grille[i][j] == 0){
                        compteur++;
                    }
                }
            }
            variable.nbContrainte = compteur;
        }
        // Triage
        bestVariable.sort(Collections.reverseOrder(Comparator.comparing(a -> a.nbContrainte)));
    }

    public void LeastConstraining(Variable variable){

        int[][] grilletemp = grille;

        for(Domains valeur : variable.domains){
            int compteur = 0;
            grilletemp[variable.p.x][variable.p.y] = valeur.valeur;

            for (int i = 0; i < taille; i++) {
                if(grilletemp[i][variable.p.y] == 0){
                    compteur += checkPossibleValue(grilletemp, compteur, valeur.valeur, i, variable.p.y);
                }
            }
            for (int j = 0; j < taille; j++) {
                if(grilletemp[variable.p.x][j] == 0){
                    compteur += checkPossibleValue(grilletemp, compteur, valeur.valeur, variable.p.x, j);
                }
            }
            int row = variable.p.x - variable.p.x%3;
            int col = variable.p.y - variable.p.y%3;

            for (int i = row; i < 3; i++) {
                for (int j = col; j < 3; j++){
                    if(grille[i][j] == 0){
                        compteur += checkPossibleValue(grilletemp, compteur, valeur.valeur, i, j);
                    }
                }
            }
            valeur.nbValeurPossible = compteur;
        }
        variable.domains.sort(Collections.reverseOrder(Comparator.comparing(a -> a.nbValeurPossible)));
    }

    public int checkPossibleValue(int[][] grilletemp, int compteur, int value, int x, int y){
        for (int i = 0; i < taille; i++) {
            if(grilletemp[i][y] == value){
                compteur++;
            }
        }
        for (int j = 0; j < taille; j++) {
            if(grilletemp[x][j] == value){
                compteur ++;
            }
        }
        int row = x - x%3;
        int col = y - y%3;

        for (int i = row; i < 3; i++) {
            for (int j = col; j < 3; j++){
                if(grille[i][j] == value){
                    compteur++;
                }
            }
        }
        return compteur;
    }

    public void AC3(){
        Stack<Constraint> queue= new Stack<Constraint>();
        for (Variable variable : unassigned){
            for(Constraint c : variable.constraints){
                queue.add(c);
            }
        }

        while(!queue.isEmpty()){
            Constraint c = queue.remove(0);
            if(removeInconsistentValue(c.v1,c.v2)){
                for(Constraint constraint : c.v2.constraints){
                    queue.add(constraint);
                }
            }
        }


    }

    public boolean removeInconsistentValue(Variable v1, Variable v2){

        boolean removed = false;
        Domains elementtoremove = null;
        for (Domains x : v1.domains){
            for(Domains y : v2.domains){
                if(x.valeur == y.valeur){
                    elementtoremove = x;
                    removed = true;
                }
            }
        }
        if(removed){
            v1.domains.remove(elementtoremove);
        }
        return removed;
    }


    public boolean checkFull(){
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                if(grille[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    }

    public void loadSudoku(String name){
        try{
            File f = new File("src/Puzzles/" + name);
            Scanner fReader = new Scanner(f);
            int indexLigne = 1;
            while(fReader.hasNextLine()){
                String a = fReader.nextLine().replace("!", "");
                if(!a.contains("-")){
                    String[] line = a.split("");
                    for (int i = 0; i < line.length; i++) {
                        if(line[i].equals(".")){
                            grille[indexLigne-1][i] = 0;
                        }else{
                            grille[indexLigne-1][i] = Integer.parseInt(line[i]);
                        }
                    }
                    indexLigne++;
                }
            }
            fReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        String affichage = "";
        for (int i = 1; i < taille+1; i++) {
            affichage += "| ";
            for (int j = 1; j < taille+1; j++) {
                if(grille[i -1][j-1] == 0){
                    affichage += " " + "." + " ";
                }else{
                    affichage += " " + grille[i -1 ][j -1] + " ";
                }
                if(j%3 == 0){
                    affichage += " | ";
                }
            }
            affichage += "\n";
        }
        return affichage;
    }
}
