import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


public class Factor {
    private List<String> name; // Factor name represents all variable names that are independent from the sum
    private HashMap<List<String>, BigDecimal> table;
    private int numRows, numCols;


    private Factor(){this.table = new HashMap<>(); this.name = new ArrayList<>();}

    public Factor(Variable v, List<String> givenOutcomes) {

        List<String> givenVariablesNames = new ArrayList<>();
        for (String ev : givenOutcomes)
            givenVariablesNames.add(ev.split("=")[0]);

        List<String> relevantVariables;
        List<String> v_parents = v.getParents();
        String relevantVarName;
        int rv_size;
        int count;
        this.table = v.getCPT();
        Set<List<String>> rows;


        rows = new HashSet<>(this.table.keySet());

        for (List<String> cptRow : rows) {
            // iterate over rows and find rows to remove
            // which contains outcomes that are not in `givenOutcomes`
            relevantVariables = new ArrayList<>();
            for (String s : givenOutcomes) {
                relevantVarName = s.split("=")[0]; // "X=x".split("=")
                if (v.getName().equals(relevantVarName))
                    relevantVariables.add(s);
                if (v.getParents().contains(relevantVarName))
                    relevantVariables.add(s);
            } // collect relevant variable parents from givenOutcomes

            //
            if (cptRow.stream().anyMatch(x -> x.split("=")[0].equals(v.getName()))) {
                if (!cptRow.containsAll(relevantVariables)) {
                    // remove row, it does not contain all given values that should be contained.
                        this.table.remove(cptRow);
//                System.out.println("Not all variables are in row: " + cptRow + "\nDiscard row.... ^");
                }
            }
            for (int i = 0; i < cptRow.size(); ++i) {
                if (givenVariablesNames.contains(cptRow.get(i).split("=")[0])) {
                    cptRow.remove(i--);
                }
            }
        }

        // Name this factor with all variables associated with it.
        name = new ArrayList<>();
        String X;
        name.add(v.getName());
        name.addAll(v.getParents());
        numCols = name.size();
        numRows = this.table.keySet().size();
        int p1, p2;
        for (String X_x : givenOutcomes) {
            X = X_x.split("=")[0];

            /* TODO: Maybe name list should contain the outcomes of given variables...
            *   ...................................................................... */
            if (v.getName().equals(X)) {
                name.remove(v.getName());
                name.add(X_x);
            }
            p1 = v_parents.indexOf(X);
            p2 = name.indexOf(X);
            if (p1 != -1){
                name.remove(p2);
                name.add(X_x);
            }
        }
    } // Constructor

    public static Factor sumOutFactor(String v_name, Factor f){

//        System.out.println("Summing " + v_name + " Out!");

        Factor sum_result_factor = new Factor();

        List<String> key = null;
        double p1, p2;
        for (Map.Entry<List<String>, BigDecimal> entry : f.table.entrySet()) {
            key = entry.getKey().stream() // all variables except for `v_name`
                    .filter(x -> !x.split("=")[0].equals(v_name.split("=")[0]))
                    .collect(Collectors.toList());

            if (sum_result_factor.table.get(key) != null) {

                p1 = sum_result_factor.table.get(key).doubleValue();
                p2 = entry.getValue().doubleValue();
                sum_result_factor.table.put(key,
                        new BigDecimal(p1).add(new BigDecimal(p2)));
            } else {
                sum_result_factor.table.put(key,
                        BigDecimal.valueOf(entry.getValue().doubleValue()));
            }
        }

        for (String k : key)
            sum_result_factor.name.add(k.split("=")[0]);
        sum_result_factor.numCols = sum_result_factor.getName().size();
        sum_result_factor.numRows = sum_result_factor.getTable().keySet().size();

//        System.out.println("sum_result_factor: " + sum_result_factor);
        return sum_result_factor;
    }

    public static Factor normalizeFactor(Factor f, String v_name) {

        System.out.println("Normalizing " + v_name);
        Factor normalized_factor = f;
        for (String key : f.name) {
            if (!key.split("=")[0].equals(v_name)) {
                System.out.println("key: " + key);
                normalized_factor = sumOutFactor(key, f);
            }
        }
        BigDecimal sum = new BigDecimal(0);

        for (BigDecimal bd : normalized_factor.getTable().values()) {
            sum = sum.add(bd);
//            System.out.println("Sum: " + sum);
        }

        for (Map.Entry<List<String>, BigDecimal> entry : normalized_factor.getTable().entrySet()) {
//            normalized_factor.getTable().put(entry.getKey(),
//                                    entry.getValue().precision());
            normalized_factor.getTable().put(entry.getKey(),
                    entry.getValue().divide(sum, 6, RoundingMode.FLOOR));
        }

        return normalized_factor;
    }

    public static Factor joinFactors(Factor f1, Factor f2) {

        Factor join_result = new Factor();

        List<String> varsToJoinOver = findVarsToJoinOver(f1, f2); // Intersection of f1.names and f2.names

        if (varsToJoinOver.isEmpty()) {

            System.out.println("NO VARS TO JOIN OVER!!!!!!!!!!!!!!!!!!!");

            varsToJoinOver.addAll(f1.name.stream()
            .filter(x -> x.split("=").length == 1)
            .collect(Collectors.toList()));
            varsToJoinOver.addAll(f2.name.stream()
                    .filter(x -> x.split("=").length == 1)
                    .collect(Collectors.toList()));
        }

        List<String> joint_entries;
        List<String> intersectingVarsOutcomes = null;

        for (Map.Entry<List<String>, BigDecimal> entry1 : f1.getTable().entrySet()) {

            intersectingVarsOutcomes = getIntersectingVarsAndOutcomes(entry1.getKey(), varsToJoinOver);
            System.out.println("intersecting variables and outcomes: " + intersectingVarsOutcomes);
            for (Map.Entry<List<String>, BigDecimal> entry2 : f2.getTable().entrySet()) {

                if (entry2.getKey().containsAll(intersectingVarsOutcomes)) {
                    // found corresponding rows
                    joint_entries = new ArrayList<>(entry1.getKey().stream()
                            .filter(x -> varsToJoinOver.contains(x.split("=")[0]))
                            .collect(Collectors.toList())); // all intersecting variables

                    // TODO: Check this if getting errors on COMPLEX JOINS
                    joint_entries.addAll(entry1.getKey().stream() // get all non-intersecting variables
                            .filter(x -> f1.name.contains(x.split("=")[0]) &&
                                    !varsToJoinOver.contains(x.split("=")[0]))
                            .collect(Collectors.toList()));

                    joint_entries.addAll(entry2.getKey().stream() // get all non-intersecting variables
                            .filter(x -> f2.name.contains(x.split("=")[0]) &&
                                    !varsToJoinOver.contains(x.split("=")[0]))
                            .collect(Collectors.toList()));

                    join_result.table.put(joint_entries, // add row in the new factor
                            new BigDecimal(entry1.getValue().doubleValue() *
                                    entry2.getValue().doubleValue()));
                }
            }
        }

        if (null == intersectingVarsOutcomes) {System.out.println("No intersecting variables!!!");}


        join_result.name.addAll(varsToJoinOver.stream()
        .filter(x -> x.split("=").length == 1)
        .collect(Collectors.toList()));

        join_result.name.addAll(f1.name.stream()
        .filter(x -> !join_result.name.contains(x) &&
                x.split("=").length == 1)
        .collect(Collectors.toList()));

        join_result.name.addAll(f2.name.stream()
                .filter(x -> !join_result.name.contains(x) &&
                        x.split("=").length == 1)
                .collect(Collectors.toList()));
        join_result.numCols = join_result.name.size();
        join_result.numRows = join_result.getTable().keySet().size();

//        System.out.println("Join Result: " + join_result);
        return join_result;
    }

    /**
     * This function returns the variables and outcomes
     * that intersect in both lists in the format ["X=x", "Y=y", ...]
     * @param factorEntry - an entry (List<String>) of a factor.
     * @param intersection - the intersection of two factor tables.
     * @return - A list of intersecting variables in the format: ["X=x", "Y=y", ...]
     */
    private static List<String> getIntersectingVarsAndOutcomes(List<String> factorEntry, List<String> intersection) {
        return factorEntry.stream().filter(entry -> intersection.contains(entry.split("=")[0])).collect(Collectors.toList());
    }

    private static List<String> findVarsToJoinOver(Factor f1, Factor f2) {
        List<String> varsToJoinOver = new ArrayList<>();
        List<String> f1_names = f1.getName(),
                f2_names = f2.getName();
//        int n1 = f1_names.size();

        for (String name : f1_names) {
            if (f2_names.contains(name))
                varsToJoinOver.add(name);
        }
        return varsToJoinOver;
    }


    public HashMap<List<String>, BigDecimal> getTable() {
        return table;
    }

    public List<String> getName() {
        return this.name;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    @Override
    public String toString() {
        String s = getName() + "\n";
        for (Map.Entry<List<String>, BigDecimal> entry : table.entrySet())
            s += entry.getKey() + "|-> " + String.format("%.5f", entry.getValue().doubleValue()) + "\n";
        return s;
    }
}
