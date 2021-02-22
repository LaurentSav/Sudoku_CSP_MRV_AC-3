

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class Sudoku {

    private int taille = 9;
    private int[][] grille;
    private ArrayList<Variable> v;
    private ArrayList<Variable> bestVariable;
    private int cpt;

    public Sudoku(String name){
        grille = new int[taille][taille];
        loadSudoku(name);
        update();
        backtracking();
    }

    /* Backtracking */

    public boolean backtracking(){
        Variable variable = null;
        this.cpt = 1;
        update();
        return backtrackingRecursion(variable);

    }

    public boolean backtrackingRecursion(Variable variable){

        cpt++;
        System.out.println("Récursion : " +cpt);
        System.out.println(this);

        if(checkFull()){
            return true;
        }

        Variable v1 = v.remove(0);
        for(int i : v1.domains){
            Variable v2 = v1;
            v2.number = i;
           if(checkConstraint(v2)){
               grille[v1.p.x][v1.p.y] = v2.number;
               update();
               if(backtrackingRecursion(v2)){
                   return true;
               }
               grille[v1.p.x][v1.p.y] = 0;
               update();
           }
        }
        return false;
    }

    public void chooseVariable(){
        MRV();
        for(Variable variable : v){
            if(v.get(0).domains.size() == variable.domains.size()){
                bestVariable.add(variable);
            }
        }
        if(v.size() > 1){
            if(v.get(0).domains.size() == v.get(1).domains.size()){
                DegreeHeuristic();
            }
        }

    }

    public void MRV(){
        v.sort(Comparator.comparing(a -> a.domains.size()));
    }

    public void DegreeHeuristic(){
        for(Variable variable : bestVariable){
            int compteur = 0;
            for (int i = 0; i < taille; i++) {
                if(grille[i][variable.p.y] == 0){
                    compteur++;
                }
            }
            for (int j = 0; j < taille; j++) {
                if(grille[variable.p.x][j] == 0){
                    compteur++;
                }
            }
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
        bestVariable.sort(Comparator.comparing(a -> a.nbContrainte));
    }

    public void LeastConstraining(Variable variable){

        int[][] grilletemp = grille;

        for(int valeur : variable.domains){
            int compteur = 0;
            grilletemp[variable.p.x][variable.p.y] = valeur;

            for (int i = 0; i < taille; i++) {
                if(grilletemp[i][variable.p.y] == 0){
                    compteur += nbValeurPossible(variable);
                }
            }
            for (int j = 0; j < taille; j++) {
                if(grilletemp[variable.p.x][j] == 0){
                    compteur += nbValeurPossible(variable);
                }
            }
            int row = variable.p.x - variable.p.x%3;
            int col = variable.p.y - variable.p.y%3;

            for (int i = row; i < 3; i++) {
                for (int j = col; j < 3; j++){
                    if(grille[i][j] == 0){
                        compteur += nbValeurPossible(variable);
                    }
                }
            }
        }
    }

    public int nbValeurPossible(Variable variable){
        int compteur = 0;
        for (int i = 1; i < taille + 1; i++) {
            Variable vTemp = new Variable(i,variable.p);
            if(checkConstraint(vTemp)){
                compteur++;
            }
        }
        return compteur;
    }




    public void update(){
        v = new ArrayList<>();
        bestVariable = new ArrayList<>();
        updateVariable();
        updateDomains();
        //MRV();
        //DegreeHeuristic();
        chooseVariable();
    }

    public void updateVariable(){
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                if(grille[i][j] == 0){
                    v.add(new Variable(grille[i][j],new Point(i,j)));
                }
            }
        }
    }

    public void updateDomains(){
        for(Variable variable : v){
            for (int i = 1; i < taille + 1; i++) {
                Variable vTemp = new Variable(i,variable.p);
                if(checkConstraint(vTemp)){
                    variable.domains.add(i);
                }
            }
        }
    }




    public boolean checkConstraint(Variable variable){
        // Check Row
        for (int i = 0; i < taille; i++) {
            if(variable.number == grille[i][variable.p.y]){
                return false;
            }
        }
        for (int i = 0; i < taille; i++) {
            if(variable.number == grille[variable.p.x][i]){
                return false;
            }
        }
        // Check Square
        int row = variable.p.x - variable.p.x%3;
        int col = variable.p.y - variable.p.y%3;

        for (int i = row; i < 3; i++) {
            for (int j = col; j < 3; j++){
                if(variable.number == grille[i][j]){
                    return false;
                }
            }
        }
        return true;

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
