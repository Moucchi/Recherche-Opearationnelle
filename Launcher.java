public class Launcher {

    public static void main(String[] args) {
        int M = 50;
        double[][] contraintes = {
                { -3, 2, 1, 0, 1, 0, 0, 1 }, 
                { 1, -1, -1, 1, 0, 1, 0, 3 },
                { 1, 4, 2, -2, 0, 0, 1, 1 }
        };

        double[] fonctionObjective = { 1, -1, 1, 0, -M, -M, -M,0 };
        double[] coefficientBase = { -M, -M, -M };
        String[] base = { "a1", "a2", "a3" };
        String[] artificiels = { "a1", "a2", "a3" };
        String[] variables = { "x1", "x2", "x3", "x4", "a1", "a2", "a3" };

        M tableau = new M(coefficientBase, base, contraintes, fonctionObjective, variables, artificiels);
        // Tableau temp = new Tableau(base, contraintes, fonctionObjective, variables);
        tableau.print();
        tableau.maximiser();
    }

}