import java.util.HashMap;
import java.util.Map;

public class Tableau {
    String[] base;
    double[][] contraintes;
    double[] fonctionObjective;
    String[] variables;

    public Tableau(String[] base, double[][] contraintes, double[] fonctionObjective, String[] variables) {
        this.base = base;
        this.contraintes = contraintes;
        this.fonctionObjective = fonctionObjective;
        this.variables = variables;
    }

    public void print() {
        printHeader();
        printBody();
        printObjective();
    }

    public void printHeader() {
        String result = "Base \t";

        for (String variable : variables) {
            result += variable + "\t";
        }

        result += "Valeurs";

        System.out.println(result);
    }

    public void printBody() {
        for (int i = 0; i < contraintes.length; i++) {
            String result = "";
            result += base[i] + "\t";

            for (double contrainte : contraintes[i]) {
                result += fractionner(contrainte) + "\t";
            }

            System.out.println(result);
        }
    }

    public void printObjective() {
        String result = "Z \t";

        for (double coefficient : fonctionObjective) {
            result += fractionner(coefficient) + "\t";
        }

        System.out.println(result);
    }

    public int getPivotColumnIndex(int problemType) {
        int result = 0;
        double comparator = (-1) * Integer.MAX_VALUE * problemType;

        switch (problemType) {
            case 1:
                for (int i = 0; i < fonctionObjective.length - 1; i++) {
                    if (comparator < fonctionObjective[i]) {
                        comparator = fonctionObjective[i];
                        result = i;
                    }
                }

                break;

            case -1:
                for (int i = 0; i < fonctionObjective.length - 1; i++) {
                    if (comparator > fonctionObjective[i]) {
                        comparator = fonctionObjective[i];
                        result = i;
                    }
                }
                break;
        }

        return result;
    }

    public int getPivotLineIndex(int pivotColumnIndex) {
        int result = -1;
        double[] valeurs = extractResult();
        HashMap<Integer, Double> data = new HashMap<>();

        for (int i = 0; i < base.length; i++) {
            double[] line = contraintes[i];

            if (line[pivotColumnIndex] > 0 && valeurs[i] > 0) {
                data.put(i, valeurs[i] / line[pivotColumnIndex]);
            }
        }

        double min = Integer.MAX_VALUE;

        for (Map.Entry<Integer, Double> entry : data.entrySet()) {
            if (min > entry.getValue()) {
                result = entry.getKey();
                min = entry.getValue();
            }
        }

        base[result] = variables[pivotColumnIndex];

        return result;
    }

    public void pivoter(int problemType) {
        int pivotColumnIndex = getPivotColumnIndex(problemType);
        int pivotLineIndex = getPivotLineIndex(pivotColumnIndex);

        if (pivotLineIndex != -1) {
            double[] pivotLine = contraintes[pivotLineIndex];
            double pivot = pivotLine[pivotColumnIndex];

            for (int i = 0; i < pivotLine.length; i++) {
                pivotLine[i] = pivotLine[i] / pivot;
            }

            for (int i = 0; i < contraintes.length; i++) {
                if (i != pivotLineIndex) {
                    double[] line = contraintes[i];

                    fill(line, pivotLine, pivotColumnIndex);
                }
            }

            fill(fonctionObjective, pivotLine, pivotColumnIndex);
        }
    }

    public void fill(double[] line, double[] pivotLine, int pivotColumnIndex) {
        double[] oldLine = new double[line.length];

        for (int i = 0; i < oldLine.length; i++) {
            oldLine[i] = line[i];
        }

        for (int j = 0; j < line.length; j++) {
            line[j] = oldLine[j] - oldLine[pivotColumnIndex] * pivotLine[j];
        }
    }

    public boolean isDone(int problemType) {
        switch (problemType) {
            case -1:
                for (int i = 0; i < fonctionObjective.length; i++) {
                    if (fonctionObjective[i] < 0) {
                        return false;
                    }
                }

                break;

            case 1:
                for (int i = 0; i < fonctionObjective.length; i++) {
                    if (fonctionObjective[i] > 0) {
                        return false;
                    }
                }
                break;

            case 0:
                int index = fonctionObjective.length - 1;

                if ((long) fonctionObjective[index] != 0) {
                    System.out.println(fonctionObjective[index]);
                    return false;
                }

                break;
        }

        return true;
    }

    public void maximiser() {
        if (!isDone(1)) {
            pivoter(1);
            maximiser();
        } else {
            System.out.println("\nResultat Final : ");
            print();
        }
    }

    public void minimiser() {
        if (!isDone(-1)) {
            pivoter(-1);
            minimiser();
        } else {
            System.out.println("\nResultat Final : ");
            print();
        }
    }

    public void phase1() {
        if (!isDone(0)) {
            pivoter(-1);
            phase1();
        } else {
            System.out.println("\nResultat Final : ");
            print();
        }
    }

    public double[] extractResult() {
        double[] result = new double[base.length];

        for (int i = 0; i < contraintes.length; i++) {
            double[] line = contraintes[i];

            result[i] = line[contraintes[i].length - 1];
        }

        return result;
    }

    static String fractionner(double nombre) {
        int numerateur = 0;
        int denominateur = 1;
        double epsilon = (double) 0.000001;
        String signe = "";

        if (nombre < 0) {
            signe = "-";
            nombre = -1 * nombre;
        }

        double temp = Math.abs((double) 0 - nombre);

        while (temp > epsilon) {
            if ((double) numerateur / (double) denominateur > nombre)
                denominateur += 1;
            else
                numerateur += 1;
            temp = Math.abs((double) numerateur / (double) denominateur - nombre);
        }

        String result = signe + "" + numerateur + "/" + denominateur;

        if (denominateur == 1)
            result = signe + "" + numerateur;
        else if (numerateur == 0)
            result = "0";

        return result;
    }

    public void setResult(double[] data) {
        for (int i = 0; i < contraintes.length; i++) {
            double[] line = contraintes[i];

            line[i] = data[i];
        }
    }

    public String[] getBase() {
        return base;
    }

    public void setBase(String[] base) {
        this.base = base;
    }

    public double[][] getContraintes() {
        return contraintes;
    }

    public void setContraintes(double[][] contraintes) {
        this.contraintes = contraintes;
    }

    public double[] getFonctionObjective() {
        return fonctionObjective;
    }

    public void setFonctionObjective(double[] fonctionObjective) {
        this.fonctionObjective = fonctionObjective;
    }

    public String[] getVariables() {
        return variables;
    }

    public void setVariables(String[] variables) {
        this.variables = variables;
    }

}
