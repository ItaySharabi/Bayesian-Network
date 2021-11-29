import java.math.BigDecimal;
import java.util.*;

public class Algorithms {

//    private class BayesNetQuery {
//
//        private BayesNetQuery(){}
//        public BayesNetQuery(String query) {
//            String[] q_ev_hidden = query
//                    .split("\\|");
//            List<String> evidence = Arrays.asList(q_ev_hidden[1]
//                    .split("\\)")[0]);
//            String[] v_name_outcome = q_ev_hidden[0]
//                    .split("=");
//            String v_name = v_name_outcome[0].
//                    substring(2);
//            String v_outcome = v_name_outcome[1];
//            String var = v_name + "=" + v_outcome;
//            List<String> hidden = Arrays.asList(q_ev_hidden[1]
//                    .split(" ")[1]
//                    .split("-"));
//        }
//    }

    private static class SIZE_ASCII_Comparator implements Comparator<Factor>{

        @Override
        public int compare(Factor o1, Factor o2) {

            int o1_table_size = o1.getNumRows() * o1.getNumCols(),
                    o2_table_size = o2.getNumCols() * o2.getNumRows();

            System.out.println("o1 table size: " + o1_table_size);
            System.out.println("o2 table size: " + o2_table_size);
            if (o1_table_size ==
                    o2_table_size)
                return compareByASCII(o1.getName(), o2.getName());

            return o1_table_size - o2_table_size;
        }

        private int compareByASCII(List<String> f1, List<String> f2) {
            int ascii_sum1 = 0,
                    ascii_sum2 = 0;

            for (String s : f1)
                ascii_sum1 += sumASCII(s);
            for (String s : f2)
                ascii_sum2 += sumASCII(s);
            return ascii_sum1 - ascii_sum2;
        }

        private int sumASCII(String s) {
            int sum = 0;
            for (char c : s.toCharArray())
                sum += (int)c;
            return sum;
        }
    }

//    private class GIVEN_ORDER_Comparator implements Comparator<Factor> {
//
//        private List<String> order;
//        private int current,
//                next;
//        private GIVEN_ORDER_Comparator(){
//
//
//        }
//        public GIVEN_ORDER_Comparator(List<String> order) {
//            System.out.println("Comparator Constructor\n\tOrder: " + order);
//            if (order == null) return;
//            if (order.isEmpty()) return;
//            int n = order.size();
//            this.order = new ArrayList<>();
//            for (int i = 0; i < n; i++)
//                this.order.add(order.get(i));
//            this.current = 0;
//            this.next = 1;
//        }
//        @Override
//        public int compare(Factor o1, Factor o2) {
//            System.out.println("o1: " + o1.getName());
//            System.out.println("o2: " + o2.getName());
//            System.out.println("Index of o1: " + order.indexOf(o1.getName()));
//            System.out.println("Index of o2: " + order.indexOf(o2.getName()));
//            return order.indexOf(o1.getName()) -
//                    order.indexOf(o2.getName());
//        }
//    }


    private /*static*/ final BayesianNetwork network;

    public Algorithms(BayesianNetwork network) {
        this.network = network;
    }

//    public String VariableElimination(String query){
//
//        System.out.println("----Variable Elimination----");
//
//        String var_name;
//        Variable v;
//        List<String> evidence, hidden_vars;
//        String[] query_evidence = query.split("\\|"); // "P(Q=q" | "E1=e1, ...) H1-H2-..."
//
//        var_name = query_evidence[0].substring(2); // var = "Q=q".split("=")[0];
//        v = network.getNode(var_name.split("=")[0]); // get queried variable
//        evidence = Arrays.asList(query_evidence[1].split())
////        System.out.println(var_name); // prints "Q=q"
//
//        System.out.println("evidence: " + evidence);
//
//
//        return "";
//    }
    /**
     * Check Conditional independence between Vars:
     * @param varEliminationQuery
     * @return
     */
    public String VariableEliminationMarginal(String varEliminationQuery) {

        /*
        1. Check Conditional independence between Vars:
        2. Start "Factorizing" variables
        3.
         */

//        String v_outcome = "";
//        Variable V;
        System.out.println("---- Variable Elimination ----");
        System.out.println(varEliminationQuery);
        String v;
        List<String> evidence,
                     hiddenVariables;

        String[] processed_query = null;
        try {
            processed_query = processVarEliminationQuery(varEliminationQuery);
        } catch (Exception e) {
            System.out.println("***Variable Elimination***\nCould not process query");
            e.printStackTrace();
        }
        if (processed_query == null) return "-1";
        v = processed_query[0];
//        V = network.getNode(v.split("=")[0]);
//        evidence = clearConditionallyIndependentEvidence(v,
        evidence = Arrays.asList(processed_query[1].split(","));//));
        hiddenVariables = Arrays.asList(processed_query[2].split("-"));
//        hiddenVariables.add(v.split("=")[0]);




        System.out.println("V: " + v);
        System.out.println("evidence: " + evidence);
        System.out.println("hidden variables: " + hiddenVariables);
        System.out.println("-----------------------------------------");

        //        givenValues.add(v); // According to presentation slides of VE. calculate P(Q)
        List<String> givenValues = new ArrayList<>(evidence);
        BigDecimal bd;

        // Initialize factors using evidence:
        HashSet<Factor> factors = new HashSet<>();

        // initialize factor of v with a list of all variables and their given outcomes.
        List<String> relevantVars = new ArrayList<>();
        relevantVars.add(v.split("=")[0]);
        for (String ev : evidence)
                relevantVars.add(ev);
        for (String h : hiddenVariables)
            if (!isAncestor(h, relevantVars))
                relevantVars.add(h);



        for (String V : relevantVars)
            factors.add(new Factor(
                    network.getNode(V.split("=")[0]),
                    givenValues));


        // initialize a priority queue with a given priority over elimination order

        EliminationProcedure(factors, hiddenVariables);
//        Factor query_result_factor =;
//        for (Map.Entry<List<String>, Double> entry : query_result_factor.getRows().entrySet())
//            if (entry.getKey().contains(v))
//                return "" + entry.getValue();
        System.out.println("============================");

        return "-1";
    }

    /**
     * This method answers the question:
     * is `v` an ancestor of any of the variables in `vars`.
     * @param v
     * @param vars
     * @return
     */
    private boolean isAncestor(String v, List<String> vars) {

        for (String u : vars) {
            if (network.getNode(u.split("=")[0]).getParents()
                                .contains(v)) {
                System.out.println(v + " is an ancestor of " + u);
                return true;
            }

        }
        for (String u : vars) {
            for (String child : network.getNode(u.split("=")[0]).getChildren()) {
                return isAncestor(child, vars);
            }
        }

        return false;
    }

    /**
     *
     * @param factors - Set of all factors.
     * @param vars_to_be_eliminated - An order in which factors will be eliminated.
     *          Elimination order will be Z1, Z2, ..., Zk.
     */
    private String EliminationProcedure(Set<Factor> factors, List<String> vars_to_be_eliminated) {

        System.out.println("----Elimination----");
//        System.out.println("Factors: " + factors);
//        System.out.println("Z: " + Z);

        for (String var : vars_to_be_eliminated) {
            factors = Eliminate(network.getNode(var)
                                , factors);
        }
        System.out.println("Factors after elimination: " + factors);

        List<Factor> factors_left_to_compute = new ArrayList<>(factors);
        Factor result = null;
        if (factors_left_to_compute.size() > 1) {
            for (int i = 0; i < factors_left_to_compute.size() - 1; ++i) {
                factors_left_to_compute.sort(new SIZE_ASCII_Comparator());
                result = Factor.joinFactors(factors_left_to_compute.remove(0),
                        factors_left_to_compute.remove(0));
            }
        } else {
            result = factors_left_to_compute.get(0); // only one factor left
        }

        result = normalize(result);
        System.out.println("Result after normalizing: " + result);

        for (Map.Entry<List<String>, Double> entry : result.getRows().entrySet())
            if (entry.getKey().contains(result.getName().get(0))) // assuming only one name left in entry!
                return "" + entry.getValue();
        System.out.println("----Elimination Procedure----");
        return "-1 (EliminationProcedure)";
    }

    private Factor normalize(Factor f) {
        double sum = 0;

        for (Map.Entry<List<String>, Double> entry : f.getRows().entrySet()) {
            /* Need to check if no other variables get in this factor`s KeySet
            * -> They are.... Or not??*/
            sum += entry.getValue();
        }
        System.out.println("sum: " + sum);
        for (Map.Entry<List<String>, Double> entry : f.getRows().entrySet()) {
            f.getRows().put(entry.getKey(), entry.getValue()/sum);
        }
        return f;
    }

    private Set<Factor> Eliminate(Variable var_to_eliminate, Set<Factor> factors) {
        // How to Eliminate A?
        // - join all factors that A is a part of them.
        // - sum out over all values of A
        HashSet<Factor> result_factor_set = new HashSet<>(factors);
        System.out.println("\tEliminating " + var_to_eliminate.getName());
        List<Factor> factors_of_var = new ArrayList<>();

        for (Factor f : factors) { // find factors such that `f` is in their scope (name)
            if (f.getName().contains(var_to_eliminate.getName())) {
                factors_of_var.add(f);
            }
        }

        System.out.println("Factors of " + var_to_eliminate.getName() + ": " + factors_of_var);
        Factor f1, f2, join_result_factor;
        while (factors_of_var.size() > 1) { // while there are factors left
            factors_of_var.sort(new SIZE_ASCII_Comparator());
            f1 = factors_of_var.remove(0); // remove both factors
            f2 = factors_of_var.remove(0); // and join them into one
            join_result_factor = Factor.joinFactors(f1, f2); // keep the join result
            factors_of_var.add(join_result_factor); // add it to keep the loop
            result_factor_set.remove(f1); // remove from the resulting factor set
            result_factor_set.remove(f2);
        }

        result_factor_set.add(Factor.sumOutFactor(var_to_eliminate.getName(),
                                factors_of_var.get(0)));



        return result_factor_set;
//        return factors_after_elimination;
    }

    // returns an array of size 3:
    // ["Q=q", "E1=e1,...,Ek=ek", H1-H2-...-Hl]
    private String[] processVarEliminationQuery(String query) throws Exception{



        String[] q__ev_hidden = query // => ["Q=q", "E1=e1,...,) H1-H2..."]
                .split("\\|");
        String evidence = q__ev_hidden[1]
                .split("\\)")[0];
        String[] v_name_outcome = q__ev_hidden[0]
                .split("=");
        String v_name = v_name_outcome[0].
                substring(2);
        String v_outcome = v_name_outcome[1];
        String var = v_name + "=" + v_outcome;
//        if (q__ev_hidden.length == 1) throw new IllegalFormatException("")
        String hiddenVariables = q__ev_hidden[1]
                .split(" ")[1];

        if (null == network.getNode(v_name))
            throw new NoSuchElementException("No variable name `" + v_name + "` in the network");
        if (evidence.length()>0) {
            for (String ev_out : Arrays.asList(evidence.split(","))) {
                String s = ev_out.split("=")[0];
                if (null == network.getNode(s))
                    throw new NoSuchElementException("No variable name `" + s + "` in the network");
            }
        }

        return new String[]{var, evidence, hiddenVariables};
    }



    /**
     * So far this is correct. Need to implement some tests
     */
    private HashMap<String, String> _visited = new HashMap<>();
    public String BayesBall(String bayesBallQuery) {
        // A-B|E1=e1,E2=e2,...,Ek=ek    =>  Are A and B conditionally independent given Ei=ei ?

        String[] vars_evidence = bayesBallQuery.split("\\|");
        String[] vars = vars_evidence[0].split("-");
        List<String> evidence, e;
        if (vars_evidence.length <= 1) { // Evidence or vars are missing
            evidence = new ArrayList<>();
            evidence.add("");
        }
        else { // Positive number of evidence
            // Temp evidence
            e = Arrays.asList(vars_evidence[1].split(","));
            evidence = new ArrayList<>();
            for (String s : e) {
                if (network.getNodes().contains(network.getNode(s.split("=")[0])))
                    evidence.add(s.split("=")[0]);
            }
        }

        String start = vars[0],
               target = vars[1];
        Variable s = network.getNode(start);
        if (null == s) return start + " is not a variable (BayesBall)";
        Variable p, c;

        Collection<String> parents = s.getParents();
        Collection<String> children = s.getChildren();
        if (null == parents || children.isEmpty()) {
            System.out.println("No Children for Var " + start);
            children = new ArrayList<>();
        } else if (null == children || parents.isEmpty()) {
            System.out.println("Var "+start+" has no parents");
            parents = new ArrayList<>();
        }

        for (Variable v : network.getNodes())
            _visited.put(v.getName(), "WHITE"); // All nodes are white as default. When processed they become BLACK.

        for (String parent : parents){ // Execute DFS from each parent
            p = network.getNode(parent);
            if (target.equals(parent)) {
//                System.out.println(parent + " ==? " + target);
                return targetFoundText(start, target, evidence);
            }
//            System.out.println("DFS From: " + parent);
            if (isTargetReachable(
                    network.getNode(p.getName()),      // Source
                    network.getNode(target),           // Target
                    network.getNode(start),            // Previous node
                    evidence))
                return targetFoundText(
                        start, target, evidence);
//            System.out.println(parent + " did not find " + target);
        }

        for (String child : children) {
            if (target.equals(child)) {
//                System.out.println(child + " ==? " + target);
                return targetFoundText(start, target, evidence);
            }
            c = network.getNode(child);
//            System.out.println("DFS From: " + child);
            if (isTargetReachable(
                    network.getNode(c.getName()),     // Source
                    network.getNode(target),          // Target
                    network.getNode(start),           // Previous node
                    evidence))
                return targetFoundText(
                        start, target, evidence);
//            System.out.println(child + " did not find " + target);
        }

//        return targetNotFoundText(start, target, evidence);
        return "yes";
    }

    private boolean isTargetReachable(Variable src, Variable target,Variable prev, List<String> evidence) {
        if (null == src || null == target || null == prev || null == evidence) return false;
        if(_visited.get(src.getName()).equals("BLACK")) return false;

//        System.out.println("\t" + src.getName() + " is looking for " + target.getName());
//        System.out.println("\t" + src.getName() + " came from " + prev.getName());
        if (evidence.contains(src.getName())) {
//            System.out.println("\t\t" + src.getName() + " is Evidence!");
        // Observed Node (Evidence node)
        // Can only move to parents, if came from a parent
            if (src.getParents().contains(prev.getName())) {

//                System.out.println("\t\t" + src.getName() + " came from parent");
//                System.out.println("\t\t" + src.getName() + " looking to move up");
                for (String parent : src.getParents()) {
                    System.out.println("\t\t\t- " + parent + ":");
                    if (target.getName().equals(parent)) return true;
                    if (_visited.get(parent).equals("BLACK"))
//                        System.out.println("Var " + parent + " is BLACK (Processed)");

//                    _prev.put(parent, src.getName());
//                    System.out.println("\t\t\ttry from: " + parent + "...");
                    if (isTargetReachable(network.getNode(parent), target, src, evidence))
                        return true;
//                    _prev.put(src.getName(), parent); // set prev[src] = parent
                }
            }
        }
        else { // Not observed(Not Evidence). Can move to any child, or, from child to parent.
//            System.out.println("\t\t" + src.getName() + " is NOT Evidence");

//            System.out.println("\t\t" + src.getName() + " came from parent");
            for (String child : src.getChildren()) {
//                System.out.println("\t\t\t" + src.getName() + " looking for children");
                if (target.getName().equals(child)) return true;
                if (_visited.get(child).equals("BLACK"))
                    System.out.println("Var " + child + " is BLACK (Processed)");

//                    _prev.put(child, src.getName());
//                System.out.println("\t\t\ttry from " + child + "...");
                if (isTargetReachable(network.getNode(child), target, src, evidence))
                    return true;
//                    _prev.put(src.getName(), child); // set prev[src] = parent
            }

            if (src.getChildren().contains(prev.getName())) { // if prev[src] in children(src)
                // Coming from child => can move to any parent
//                System.out.println("\t\t" + src.getName() + " came from child");
                for (String parent : src.getParents()) {
//                    System.out.println("\t\t\t" + src.getName() + " looking for parents...");
                    if (target.getName().equals(parent)) return true;
                    if (_visited.get(parent).equals("BLACK"))
                        System.out.println("Var " + parent + " is BLACK (Processed)");

//                    _prev.put(parent, src.getName());
//                    System.out.println("\t\t\ttry from " + parent + "...");
                    if (isTargetReachable(network.getNode(parent), target, src, evidence))
                        return true;
//                    _prev.put(src.getName(), parent); // set prev[src] = parent
                }
            }

        }
//        _visited.put(src.getName(), "BLACK"); // Mark as processed


        return false;
    }

    private String targetFoundText(String start, String target, List<String> evidence) {

        String out = start + " and " + target + " are NOT conditionally independent";

        String evd = " given: ";
        if (evidence.size()>0) {
            for (String v : evidence)
                evd += v + " ";
            out += evd;
        }
        return out;
    }
    private String targetNotFoundText(String start, String target, List<String> evidence) {

        String out = start + " and " + target + " are conditionally independent";

        String evd = " given: ";
        if (evidence.size() > 0) {
            for (String v : evidence)
                evd += v + " ";
            out += evd;
        }
        return out;
    }
}

//
//    public String BayesBall(String start, String target, List<String> evidence) {
//
//        if (start.equals(target)) return targetFoundText(start, target, evidence);
//
//
//
//        HashMap<String, String> nodeColors;
//
//        boolean target_found = false;
//        for (Variable parent : network.getParents(start)) {
//            nodeColors = createVisitedMapAndMarkStartNode(parent.getName());
////            nodeColors.put(parent.name(), "GREY");
//            target_found |= canReachTarget(parent.getName(), target, start, evidence, nodeColors);
//            if (target_found) return targetFoundText(start, target, evidence);
//        }
//
//        if (target_found) return targetFoundText(start, target, evidence);
//
//        for (Variable child : network.getChildren(start)) {
//            nodeColors = createVisitedMapAndMarkStartNode(child.getName());
////            nodeColors.put(child.name(), "GREY");
//            target_found |= canReachTarget(child.getName(), target, start, evidence, nodeColors);
//            if (target_found) return targetFoundText(start, target, evidence);
//        }
//
//        return target_found ? targetFoundText(start, target, evidence)
//                : targetNotFoundText(start, target, evidence);
//    }
//
//    private HashMap<String, String> createVisitedMapAndMarkStartNode(String start) {
//        HashMap<String, String> nodeColors = new HashMap<>();
//        for (Variable v : network.getNodes()) {
//            nodeColors.put(v.getName(), "WHITE");
////            System.out.println(nodeColors.toString());
//        }
//
//        nodeColors.put(start, "GREY");
//        return nodeColors;
//    }
//
//    private boolean canReachTarget(String src, String target, String prev, List<String> evidence, Map<String, String> colors) {
//        if (src.equals(target)) return true;
////        System.out.println("src: " + src);
////        if (null != prev)
////            System.out.println("previous node: " + prev);
////        System.out.println("target: " + target);
//
//        boolean target_found = false;
////        colors.put(src, "GREY");
//
//        if (colors.get(src).equals("BLACK")) {
//            System.out.println("src was already visited"); return false;}
//
//        if (evidence.contains(src)) {
//            if (null != prev && network.getParents(src).contains(network.getNode(prev))) { // From parent to parent
//                for (Variable parent : network.getParents(src)) {
//                    if (!colors.get(parent.getName()).equals("WHITE")) continue;
//                    if (target.equals(parent.getName())) return true;
//                    colors.put(parent.getName(), "GREY"); // ****
//
//                    target_found |= canReachTarget(parent.getName(), target, src, evidence, colors);
//                }
//            }
//        }
//        else { // src not in evidence (not observed)
////            System.out.println("*** " + network.children(src).contains(prev));
////            System.out.println("children: " + network.children(src));
//            if (null != prev && network.getChildren(src).contains(network.getNode(prev))) { // From child to parent
//
//                for (Variable parent : network.getParents(src)) {
////                    if (visited.contains(parent)) continue;
//                    if (!colors.get(parent.getName()).equals("WHITE")) continue;
//                    if (target.equals(parent.getName())) return true;
////                    visited.add(src); // ****
//                    colors.put(parent.getName(), "GREY");
//                    target_found |= canReachTarget(parent.getName(), target, src, evidence, colors);
//                }
//            }
//
//            // To any child
//            for (Variable child : network.getChildren(src)) {
////                if (colors.get(child.name()).equals("GREY")) continue;
//                if (!colors.get(child.getName()).equals("WHITE")) continue;
//                if (target.equals(child.getName())) return true;
////                visited.add(src); // ****
//                colors.put(child.getName(), "GREY");
//                target_found |= canReachTarget(child.getName(), target, src, evidence, colors);
//            }
//        }
//        colors.put(src, "BLACK");
//        return target_found;
//    }
//
//    private String targetFoundText(String start, String target, List<String> evidence) {
//
//        String out = start + " and " + target + " are NOT conditionally independent";
//
//        String evd = " given: ";
//        if (evidence.size()>0) {
//            for (String v : evidence)
//                evd += v + " ";
//            out += evd;
//        }
//        return out;
//    }
//    private String targetNotFoundText(String start, String target, List<String> evidence) {
//
//        String out = start + " and " + target + " are conditionally independent";
//        String evd = " given: ";
//        if (evidence.size() > 0) {
//            for (String v : evidence)
//                evd += v + " ";
//            out += evd;
//        }
//        return out;
//    }
