import java.util.*;
import java.util.stream.Collectors;


public class Factor {
    private List<String> name; // Factor name represents all variable names that are independent from the sum
    private HashMap<List<String>, Double> rows;
    private int numRows, numCols;

//    /**
//     *
//     * @param v - Variable object. This method read's its CPT.
//     * @param evidence_outcomes - List<String> in the format: A1=a1, A2=a2, ..., An=an
//     */

//    public Factor(Variable v, List<String> evidence_outcomes) {
//        // P(J=T|B=T,M=T)
//        // IF all parents of `v` are given -> ? Return in O(1)
//        //
//
////        List<String> evidence = clearVarFromList(evidence_outcomes);
//
//        ArrayList<String> evidence = new ArrayList<>(evidence_outcomes);
//        List<Integer> parents_indices = new ArrayList<>();
//        String e_name, e_o;
//
//        if (!v.getParents().isEmpty()) {
//            /* Check if one of v`s parents is given as evidence */
//            for (int i = 0; i < evidence_outcomes.size(); ++i) {
//                e_o = evidence_outcomes.get(i).split("=")[0];
//                e_name = e_o.split("=")[0]; // get Variable name
//                if (v.getName().equals(e_name)) { // if t is found in evidence
//                    System.out.println("V: " + v.getName() + "\nt: " + e_name);
//                    System.out.println("index(" + e_name + "): " + evidence_outcomes.indexOf(e_name));
//                    evidence.remove(evidence_outcomes.indexOf(evidence_outcomes.get(i)));
//                }
//            }
//
//            /* Find parents indices in v`s CPT */
//            System.out.println("evidence_outs without " + v.getName() + ": " + evidence);
//            String u;
//            for (int i = 0; i < evidence_outcomes.size(); ++i) {
//                e_o = evidence_outcomes.get(i).split("=")[0];
//                u = e_o.split("=")[0];
//                if (v.getParents().contains(u)) {
//                    System.out.println("" + v.getName() + ".getParents(): " + v.getParents());
//                    parents_indices.add(v.getParents().indexOf(u));
//                }
//            }
//
//            System.out.println(v.getName() + " has given parents at: " + parents_indices);
//        }
//
//
//        if (parents_indices.isEmpty()) {
//            System.out.println(v.getName() + " has no parents");
//            // if no parents are given as evidence in the query - well, this:
//
//
////        evidence_outcomes.removeAll(non_parents);
//
////        System.out.println("non-parents: " + non_parents);
////        System.out.println("After filtering non parents from evidence: " + evidence_outcomes);
//
//
//            rows = v.getCPT();
//            if (evidence_outcomes.isEmpty()) return;
//
//            List<List<String>> rows_to_remove = new ArrayList<>();
//            System.out.println("Factor " + v.getName());
//
//
//            System.out.println("New Factor(" + v.getName() + "):");
//            System.out.println(toString());
//        }
//    }
    private Factor(){this.rows = new HashMap<>(); this.name = new ArrayList<>();}
    public Factor(Variable v, List<String> givenOutcomes) {

//        System.out.println("Factorizing " + v.getName());
        List<String> relevantVariabls = null;
        List<String> v_parents = v.getParents();
        String relevantVarName;
        int rv_size;
        int count;
        this.rows = v.getCPT();
        Set<List<String>> rows;


        rows = new HashSet<>(this.rows.keySet());

        for (List<String> cptRow : rows) {
            // iterate over rows and find rows to remove
            // which contains outcomes that are not in `givenOutcomes`
            relevantVariabls = new ArrayList<>();
            for (String s : givenOutcomes) {
                relevantVarName = s.split("=")[0]; // "E=e".split("=")
                if (v.getName().equals(relevantVarName))
                    relevantVariabls.add(s);
                if (v.getParents().contains(relevantVarName))
                    relevantVariabls.add(s);
            } // collect relevant variable parents from givenOutcomes

            count = 0;
            for (String rv : relevantVariabls) {

                if (cptRow.contains(rv)) // if a given variable is shown in row
                    count++;
            }
            rv_size = relevantVariabls.size();

            if (count != rv_size) {
                // remove row, it does not contain all given values that should be contained.
                this.rows.remove(cptRow);
//                System.out.println("Not all variables are in row: " + cptRow + "\nDiscard row.... ^");
            }
        }


        // Name this factor with all variables associated with it.
        name = new ArrayList<>();
        String X;
        name.add(v.getName());
        name.addAll(v.getParents());
        numCols = name.size();
        numRows = this.rows.keySet().size();
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


//        System.out.println(toString());
    } // Constructor

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public static Factor sumOut(String v_name, Factor f){

        System.out.println("Summing " + v_name + " Out!");
        Factor sum_result_factor = new Factor();

        List<String> allExceptV;

        List<String> key;
        double p1, p2;
        for (Map.Entry<List<String>, Double> entry : f.rows.entrySet()) {
            key = entry.getKey().stream() // all variables except for `v_name`
                    .filter(x -> !x.split("=")[0].equals(v_name.split("=")[0]))
                    .collect(Collectors.toList());

//            key = entry.getKey();
            if (sum_result_factor.rows.get(key) != null) {

                p1 = sum_result_factor.rows.get(key);
                p2 = entry.getValue();
                System.out.println("entry: " + entry);
                sum_result_factor.rows.put(key,
                        Double.parseDouble(String.format( "%.6f",/* %.5f is not good enough. it rounds numbers up*/
                        p1 + p2)));
            } else {
                sum_result_factor.rows.put(key,
                        Double.parseDouble(String.format( "%.5f",
                                entry.getValue())));
                System.out.println("entry: " + entry);

            }
        }
        sum_result_factor.name.add("BJASND");
        System.out.println("sum_result_factor: " + sum_result_factor);
        return sum_result_factor;
    }

    public static Factor joinFactors(Factor f1, Factor f2) {

        Factor join_result = new Factor();
//        HashMap<List<String>, Double> cpt = new HashMap<>();



        List<String> varsToJoinOver = findVarsToJoinOver(f1, f2); // Intersection of f1.names and f2.names
        System.out.println("***Join Factors (Over "+varsToJoinOver+")***");

        System.out.println("\t" + f1.getRows());
        System.out.println("\t" + f2.getRows());
        List<String> joint_entries;
        List<String> intersectingVarsOutcomes = null;
//        String prob;
        for (Map.Entry<List<String>, Double> entry1 : f1.getRows().entrySet()) {

            intersectingVarsOutcomes = getIntersectingVarsAndOutcomes(entry1.getKey(), varsToJoinOver);
            System.out.println("intersecting variables and outcomes: " + intersectingVarsOutcomes);
            for (Map.Entry<List<String>, Double> entry2 : f2.getRows().entrySet()) {

                if (entry2.getKey().containsAll(intersectingVarsOutcomes)) {
                    // found corresponding rows
                    // TODO: change entry2 for the name of the row
//                    joint_entries = entry1.getKey().stream()
//                                            .filter(x -> x.split("=").length == 1)
//                                            .collect(Collectors.toList());
//                    joint_entries.addAll(entry2.getKey().stream()
//                            .filter(x -> !varsToJoinOver.contains(x.split("=")[0]))
//                            .collect(Collectors.toList()));
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

                    join_result.rows.put(joint_entries, // add row in the new factor
                            Double.parseDouble(String.format( "%.5f",
                                    entry1.getValue() * entry2.getValue())));
                }
            }
        }

        if (null == intersectingVarsOutcomes) {System.out.println("No intersecting variables!!!");}

//        join_result.name.addAll(intersectingVarsOutcomes.stream()
//                                    .filter(v -> v.split("=").length == 1)
//                                    .collect(Collectors.toList()));
//        for (String varName : varsToJoinOver) {
//            System.out.println(varName);
//            if (varName.split("=").length == 1) {
//
//                join_result.name.add(varName);
//            }
//        }
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

//        join_result.name.addAll()
//        System.out.println("Name of join_result: " + join_result.name);

//        System.out.println(join_result);

//        System.out.println("New table: " + cpt);

//        System.out.println();

        return join_result;
    }

    private static List<String> createEntryKey(List<String> entry1,
                                               List<String> entry2,
                                               List<String> intersectingVariables) {
        List<String> ans = new ArrayList<>(entry1.stream()
                .filter(x -> intersectingVariables.contains(x.split("=")[0]))
                .collect(Collectors.toList())); // all intersecting variables

        ans.addAll(entry1.stream() // get all non-intersecting variables
                .filter(x -> !intersectingVariables.contains(x.split("=")[0]))
                .collect(Collectors.toList()));
        ans.addAll(entry2.stream() // get all non-intersecting variables
                .filter(x -> !intersectingVariables.contains(x.split("=")[0]))
                .collect(Collectors.toList()));

        return ans;
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

//    private static boolean existsInRow(String)

//    private static HashMap<List<String>, Double> createRowsOfJointCPT(Factor f1, Factor f2) {
//
//        HashMap<List<String>, Double> res = new HashMap<>();
//        Set<List<String>> f1CPT = f1.getRows().keySet();
//        Set<List<String>> f2CPT = f2.getRows().keySet();
//
//        System.out.println("-------------createRowsOfJoinCPT`s");
//
//
//    }


    private static List<String> findVarsToJoinOver(Factor f1, Factor f2) {
        List<String> varsToJoinOver = new ArrayList<>();
        List<String> f1_names = f1.getName(),
                f2_names = f2.getName();
        int n1 = f1_names.size(),
                n2 = f2_names.size();
        String nameI, nameJ;
        for (int i = 0; i < n1; i++) {
            if (f2_names.contains(f1_names.get(i)))
                varsToJoinOver.add(f1_names.get(i));
        }
        return varsToJoinOver;
    }



    public HashMap<List<String>, Double> getRows() {
        return rows;
    }
    public List<String> getName() {
        return this.name;
    }

    @Override
    public String toString() {
        String s = getName() + "\n";
        for (Map.Entry<List<String>, Double> entry : rows.entrySet())
            s += entry.getKey() + "|-> " + entry.getValue() + "\n";
        return s;
    }


    //    public Factor(Variable v, List<String> parent_evidence) { // No parents, no evidence
//
//        for (List<String> row : v.getCPT().keySet()) { // iterating over `v`'s CPT to avoid conflict on removing.
//
//            for (String parent : parent_evidence) {
//                if (row.contains(parent))
//                    System.out.println("Keep row: " + row);
//                else
//                    System.out.println("Discard row: " + row);
//            }
//        }
//    }

//    public Factor(String var, List<String> parents,List<String> evidence) {}

}
