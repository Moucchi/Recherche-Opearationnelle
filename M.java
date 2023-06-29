import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class M extends Tableau {
    double[] coefficientBase;
    double[] zi;
    double[] ci_zi;
    String[] artificiels;
    ArrayList disabledColumn;

    public M(double[] coefficientBase, String[] base, double[][] contraintes, double[] fonctionObjective,
            String[] variables, String[] artificiels) {
        super(base, contraintes, fonctionObjective, variables);
        this.setArtificiels(artificiels);
        this.setCoefficientBase(coefficientBase);
        setZi();
        setCi_zi();
        this.disabledColumn = new ArrayList<>();
    }

    public void printHeader() {
        String result = "CB \t";
        result += "base \t";

        for (String variable : variables) {
            result += variable + "\t";
        }

        result += "Valeurs";

        System.out.println(result);
    }

    public void printBody() {
        for (int i = 0; i < contraintes.length; i++) {
            String result = coefficientBase[i] + "\t";
            result += base[i] + "\t";

            for (double contrainte : contraintes[i]) {
                result += fractionner(contrainte) + "\t";
            }

            System.out.println(result);
        }
    }

    public void printObjective() {
        String result = "\n\t Z \t";

        for (double coefficient : fonctionObjective) {
            result += fractionner(coefficient) + "\t";
        }

        System.out.println(result);
    }

    public void print() {
        printObjective();
        printHeader();
        printBody();
        printZi();
        printCi_Zi();
    }

    public void printZi() {
        String result = "\t Zi \t";

        for (double value : zi) {
            result += fractionner(value) + "\t";
        }

        System.out.println(result);
    }

    public void printCi_Zi() {
        String result = "\t ci-zi \t";

        for (double value : ci_zi) {
            result += fractionner(value) + "\t";
        }

        System.out.println(result);
    }

    public int getPivotColumnIndex(int problemType) {
        int result = 0;
        double comparator = (-1) * Integer.MAX_VALUE * problemType;

        switch (problemType) {
            case 1:
                for (int i = 0; i < ci_zi.length - 1; i++) {
                    if (comparator < ci_zi[i] && !isDisabled(i)) {
                        comparator = ci_zi[i];
                        result = i;
                    }
                }

                break;

            case -1:
                for (int i = 0; i < ci_zi.length - 1; i++) {
                    if (comparator > ci_zi[i] && !isDisabled(i)) {
                        comparator = ci_zi[i];
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

        if (isArtificial(base[result])) {
            disable(getVariablesIndex(base[result]));
        }

        base[result] = variables[pivotColumnIndex];
        coefficientBase[result] = fonctionObjective[pivotColumnIndex];

        return result;
    }

    public int getVariablesIndex(String toBeSearched) {
        int result = 0;

        for (int index = 0; index < variables.length; index++) {
            if (variables[index].equalsIgnoreCase(toBeSearched)) {
                return index;
            }
        }

        return -1;
    }

    public boolean isArtificial(String varibale) {
        for (String artificiel : artificiels) {
            if (artificiel.equalsIgnoreCase(varibale)) {
                return true;
            }
        }

        return false;
    }

    public boolean isDisabled(int i) {
        if (disabledColumn.contains(i)) {
            return true;
        }

        return false;
    }

    public void disable(int columnIndex) {
        this.disabledColumn.add(columnIndex);

        for (double[] line : contraintes) {
            line[columnIndex] = 0;
        }

        fonctionObjective[columnIndex] = 0;
    }

    public void pivoter(int problemType) {
        int pivotColumnIndex = getPivotColumnIndex(problemType);
        int pivotLineIndex = getPivotLineIndex(pivotColumnIndex);

        try {
            double[] pivotLine = contraintes[pivotLineIndex];
            double pivot = pivotLine[pivotColumnIndex];

            for (int i = 0; i < pivotLine.length; i++) {
                pivotLine[i] = pivotLine[i] / pivot;
            }

            for (int i = 0; i < contraintes.length; i++) {
                if (i != pivotLineIndex && !isDisabled(i)) {
                    double[] line = contraintes[i];

                    fill(line, pivotLine, pivotColumnIndex);
                }
            }

            setZi();
            setCi_zi();
            fill(fonctionObjective, pivotLine, pivotColumnIndex);

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void fill(double[] line, double[] pivotLine, int pivotColumnIndex) {
        double[] oldLine = new double[line.length];

        for (int i = 0; i < oldLine.length; i++) {
            if (!isDisabled(i)) {
                oldLine[i] = line[i];
            }
        }

        for (int j = 0; j < line.length; j++) {
            if (!isDisabled(j)) {
                line[j] = oldLine[j] - oldLine[pivotColumnIndex] * pivotLine[j];
            }
        }
    }

    public boolean isDone(int problemType) {
        switch (problemType) {
            case -1:
                for (int i = 0; i < ci_zi.length; i++) {
                    if (ci_zi[i] < 0) {
                        return false;
                    }
                }

                break;

            case 1:
                for (int i = 0; i < ci_zi.length; i++) {
                    if (ci_zi[i] > 0) {
                        return false;
                    }
                }
                break;
        }

        return true;
    }

    public void maximiser() {
        if (!this.isDone(1)) {
            this.pivoter(1);
            this.print();
            this.maximiser();
        } else {
            System.out.println("\nResultat Final : ");
            print();
        }
    }

    public double[] getCoefficientBase() {
        return coefficientBase;
    }

    public void setCoefficientBase(double[] coefficientBase) {
        this.coefficientBase = coefficientBase;
    }

    public double[] getZi() {
        return zi;
    }

    public void setZi(double[] zi) {
        this.zi = zi;
    }

    public void setZi() {
        this.zi = new double[contraintes[0].length];

        for (int i = 0; i < zi.length; i++) {
            zi[i] = 0;
        }

        for (int i = 0; i < contraintes[0].length; i++) {
            for (int j = 0; j < base.length; j++) {
                zi[i] += contraintes[j][i] * coefficientBase[j];
            }
        }
    }

    public double[] getCi_zi() {
        return ci_zi;
    }

    public void setCi_zi(double[] ci_zi) {
        this.ci_zi = ci_zi;
    }

    public void setCi_zi() {
        if (ci_zi == null) {
            ci_zi = new double[variables.length];
        }

        for (int i = 0; i < variables.length; i++) {
            ci_zi[i] = fonctionObjective[i] - zi[i];
        }
    }

    public String[] getArtificiels() {
        return artificiels;
    }

    public void setArtificiels(String[] artificiels) {
        this.artificiels = artificiels;
    }

    public ArrayList getDisabledColumn() {
        return disabledColumn;
    }

    public void setDisabledColumn(ArrayList disabledColumn) {
        this.disabledColumn = disabledColumn;
    }
}
