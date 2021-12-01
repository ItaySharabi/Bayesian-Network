import java.math.BigDecimal;
import java.util.*;

public class Algorithms implements operations_count_observer {

    @Override
    public void updateSumOperations(int operations) {
        this.sumOperations += operations;
    }
    @Override
    public void updateMultiplicationOperations(int operations) {
        this.multOperations += operations;
    }

    private static class SIZE_ASCII_Comparator implements Comparator<Factor> {

        @Override
        public int compare(Factor o1, Factor o2) {

            int o1_table_size = o1.getTable().keySet().size() * o1.getName().size(),
                    o2_table_size = o2.getTable().keySet().size() * o2.getName().size();
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
                sum += (int) c;
            return sum;
        }
    }

    private final BayesianNetwork network;
    private int sumOperations, multOperations;

    // Used to determine whether a node came from it`s parents or it`s children
    // if direction == up:
    // we got the node we're currently inspecting from below (from one of it`s children)
    private enum Direction {
        UP,
        DOWN
    }

    public Algorithms(BayesianNetwork network) {
        this.network = network;
    }

    /**
     * This is an algorithm which answers probability queries over
     * a Bayesian Network with n variables.
     * The algorithm processes the query and prepares
     * `Factor` objects, which have the CPT (Conditional Probability Table)
     * of and variable.
     *
     * @param varEliminationQuery - A probability query. Format: P(X=x|Y1=y1,...Yk=yk) [Z1-...-Z(n-k-1)]
     * @return - The probability P(X=x|Evidence)
     */
    public String VariableEliminationMarginal(String varEliminationQuery) {
//        System.out.println("---- Variable Elimination ----");
//        System.out.println(varEliminationQuery);
        sumOperations = 0;
        multOperations = 0;
        String v;
        List<String> evidence,
                hiddenVariables;

        String[] processed_query = null;
        try {
            processed_query = processVarEliminationQuery(varEliminationQuery);
        } catch (Exception e) {
//            System.out.println("***Variable Elimination***\nCould not process query");
            e.printStackTrace();
        }
        if (processed_query == null) return "-1";
        v = processed_query[0];

        evidence = Arrays.asList(processed_query[1].split(","));//));
        hiddenVariables = Arrays.asList(processed_query[2].split("-"));

        if (!evidence.isEmpty() && evidence.get(0).equals(""))
            evidence = new ArrayList<>();

        // According to presentation slides of VE. calculate P(Q) instead of P(Q=q)
        List<String> givenValues = new ArrayList<>(evidence);

        // Initialize factors using evidence:
        HashSet<Factor> factors = new HashSet<>();

        // initialize factor of v with a list of all variables and their given outcomes.
        List<String> relevantVars = new ArrayList<>();
        List<String> hidden_variables_cleared = new ArrayList<>();

        relevantVars.add(v.split("=")[0]); // Choose to discard `v`s outcome, calculate over all values.

        for (String ev : evidence) { // add only evidence variable that don`t have parents
            if (!network.getNode(ev.split("=")[0]).getParents().isEmpty())
                relevantVars.add(ev);
        }

        for (String h : hiddenVariables) {
            String bayesBallQuery = v.split("=")[0] + "-" + h + "|";
            for (String e : evidence) {
                bayesBallQuery = bayesBallQuery.concat(e + ",");
            }
            bayesBallQuery = bayesBallQuery.substring(0, bayesBallQuery.length() - 1);

            if (isAncestor(h, relevantVars) &&
                    !BayesBall(bayesBallQuery).equals("yes")) {
                hidden_variables_cleared.add(h);
            }
        }
        relevantVars.addAll(hidden_variables_cleared);

//        System.out.println("Relevant query vars: " + relevantVars);

//        System.out.println("Do I have the query: " + v + "|" + evidence + " ?");
        if (canAnswerImmediately(v, evidence)) {
            System.out.println("Can return in O(1)");
            return immediateAnswer(v, evidence);
        }


        // Set initial factors - only those who are relevant for the query (cond. independent)
        for (String V : relevantVars) /* NOT ITERATING OVER ALL VARIABLES - Only filtered ones! */
            factors.add(new Factor(
                    network.getNode(V.split("=")[0]),
                    givenValues, this));

        // initialize a priority queue with a given priority over elimination order

        Factor result = EliminationProcedure(factors, hidden_variables_cleared);

        Factor query_result_factor = Factor.normalizeFactor(result, v.split("=")[0]);

        for (Map.Entry<List<String>, BigDecimal> entry : query_result_factor.getTable().entrySet())
            if (entry.getKey().contains(v))
                return String.format("%.5f", entry.getValue()) + "," + (sumOperations-1) + "," + multOperations;

        return "-1 (An error has occurred)";
    }


    private String immediateAnswer(String v, List<String> evidence) {
        List<String> key = new ArrayList<>();
        key.add(v);
        key.addAll(evidence);
        System.out.println("Looking for row: " + key);
        for (Map.Entry<List<String>, BigDecimal> entry : network.getNode(v.split("=")[0])
                .getCPT().entrySet())
            if (entry.getKey().containsAll(key))
                return String.format("%.5f", entry.getValue()) + "," + sumOperations + "," + multOperations;
        return "-1";
    }

    private boolean canAnswerImmediately(String q_o, List<String> evidence) {

        List<String> key = new ArrayList<>();
        key.add(q_o);
        key.addAll(evidence);
        return network.getNode(q_o.split("=")[0])
                .getCPT().get(key) != null;
//        return true;
    }

    private boolean isAncestor(String ancestorQ, List<String> vars) {

//        System.out.println("isAncestor(" + ancestorQ + ", " + vars + ")");

        for (String v : vars) {
            if (network.getNode(v.split("=")[0]).getParents().contains(ancestorQ)) {
//                System.out.println(ancestorQ + " is in " + v + ".getParents()");
                return true;
            }
        }

        for (String v : network.getNode(ancestorQ.split("=")[0]).getChildren()) {
//            System.out.println("Check " + v + "`s children" + network.getNode(v).getChildren());
            if (vars.contains(v) || vars.contains(v.split("=")[0]))
                return true;
            else if (isAncestor(v, vars)) {
//                System.out.println("ancestorQ: " + ancestorQ);
//                System.out.println("vars: " + vars);
                return true;
            }
        }

//        System.out.println(ancestorQ + " is NOT an Ancestor of " + vars);
        return false;
    }

    /**
     * @param factors               - Set of all factors.
     * @param vars_to_be_eliminated - An order in which factors will be eliminated.
     *                              Elimination order will be Z1, Z2, ..., Zk.
     */
    private Factor EliminationProcedure(Set<Factor> factors, List<String> vars_to_be_eliminated) {

//        System.out.println("----Elimination----");

        for (String var : vars_to_be_eliminated)
            factors =
                    Eliminate(network.getNode(var.split("=")[0])
                    , factors);

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

        return result;
    }

    private Set<Factor> Eliminate(Variable var_to_eliminate, Set<Factor> factors) {
        // How to Eliminate A?
        // - join all factors that A is a part of them.
        // - sum out over all values of A
        HashSet<Factor> result_factor_set = new HashSet<>(factors);
        List<Factor> factors_of_var = new ArrayList<>();

        for (Factor f : factors) { // find factors such that `f` is in their scope (name)
            if (f.getName().contains(var_to_eliminate.getName())) {
                factors_of_var.add(f);
            }
        }

//        System.out.println("Factors of " + var_to_eliminate.getName() + ": " + factors_of_var);
        Factor f1, f2, join_result_factor;
        while (factors_of_var.size() > 1) { // while there are factors left
            factors_of_var.sort(new SIZE_ASCII_Comparator());
            f1 = factors_of_var.remove(0); // remove both factors
            f2 = factors_of_var.remove(0); // and join them into one
            System.out.println("Pick \n" + f1);
            System.out.println(f2);
            System.out.println("--------------------------------------");
            join_result_factor = Factor.joinFactors(f1, f2); // keep the join result
            factors_of_var.add(join_result_factor); // add it to keep the loop
            result_factor_set.remove(f1); // remove from the resulting factor set
            result_factor_set.remove(f2);
        }

        if (factors_of_var.size() == 1) {
            result_factor_set.remove(factors_of_var.get(0));

            result_factor_set.add(Factor.sumOutFactor(var_to_eliminate.getName(),
                    factors_of_var.get(0)));
        }


        return result_factor_set;
//        return factors_after_elimination;
    }

    // returns an array of size 3:
    // ["Q=q", "E1=e1,...,Ek=ek", H1-H2-...-Hl]
    private String[] processVarEliminationQuery(String query) throws Exception {


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
        if (evidence.length() > 0) {
            for (String ev_out : Arrays.asList(evidence.split(","))) {
                String s = ev_out.split("=")[0];
                if (null == network.getNode(s))
                    throw new NoSuchElementException("No variable name `" + s + "` in the network");
            }
        }
        return new String[]{var, evidence, hiddenVariables};
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

    /**
     *
     * @param bayesBallQuery
     * @return
     */
    private HashMap<Variable, Boolean> cameFromChildList;
    private HashMap<Variable, Boolean> cameFromParentList;

    public String BayesBall(String bayesBallQuery) {
        cameFromChildList = new HashMap<>(); // ?
        cameFromParentList = new HashMap<>(); // ?

        for (Variable v : network.getNodes()) {
            cameFromChildList.put(v, false);
            cameFromParentList.put(v, false);
        }

        String[] vars_evidence = bayesBallQuery.split("\\|");
        String[] vars = vars_evidence[0].split("-");
        HashSet<Variable> evidence;
        List<String> e;
        if (vars_evidence.length <= 1) { // Evidence or vars are missing
            evidence = new HashSet<>();
        }
        else { // Positive number of evidence
            // Temp evidence
            e = Arrays.asList(vars_evidence[1].split(","));
            evidence = new HashSet<>(); // init a set of evidence
            for (String s : e) {
                evidence.add(network.getNode(s));
            }
        }

        Variable start = network.getNode(vars[0]),
                target = network.getNode(vars[1]);

        if (BayesBallAlgorithm(start, target, null, evidence, Direction.UP))
            return "no";

        return "yes";
    }

    public boolean BayesBallAlgorithm(Variable src, Variable dest, Variable prev, HashSet<Variable> evidence, Direction direction) {
        // This means we reach our Goal there for the Variables are not Independent
        Variable parent, child;

        if (src.equals(dest)) {
            return true;
        }
        // This means we have reached an evidence variable
        if (evidence.contains(src)) {
            // Observed (Evidence) nodes can only go to parents, if they came from a parent.
            if (direction == Direction.DOWN) {
                // change direction
                for (String p : src.getParents()) {
                    parent = network.getNode(p);
                    if (!cameFromChild(parent)) {
//                        if (null != prev)
//                            if (parent.getName().equals(prev.getName())) continue;
                        cameFromChildList.put(parent, true);
                        if (BayesBallAlgorithm(parent, dest, src, evidence, Direction.UP))
                            return true;
                    }
                }
            }

        } else {
            // Unobserved Nodes (Not in evidence)
            // Can go to any child, and to any parent if coming from a child.
            if (direction == Direction.DOWN) {
                // If came from parent
                // Node is in downward direction
                // try reaching any children
                for (String c : src.getChildren()) {
                    child = network.getNode(c);
                    if (!cameFromParent(child)) {
//                        if (null != prev)
//                            if (child.getName().equals(prev.getName())) continue;
                        cameFromParentList.put(child, true);
                        if (BayesBallAlgorithm(child, dest, src, evidence, Direction.DOWN))
                            return true;
                    }
                }
                cameFromChildList.put(prev, true);
            } else {
                // If node is in an upward direction
                // Came from parent
                // try visiting parents:
                for (String p : src.getParents()) {
                    parent = network.getNode(p);
                    if (!cameFromChild(parent)) {
//                        if (null != prev)
//                            if (parent.getName().equals(prev.getName())) continue;
                        cameFromChildList.put(parent, true);
                        if (BayesBallAlgorithm(parent, dest, src, evidence, Direction.UP))
                            return true;
                    }
                }
                // Node is in an upward direction
                // try reaching any children
                for (String c : src.getChildren()) {
                    child = network.getNode(c);
                    if (!cameFromParent(child)) { // if child was not set as `came from parent` -> set it
//                        if (prev != null)
//                            if (child.getName().equals(prev.getName())) continue;
                        cameFromParentList.put(child, true);
                        if (BayesBallAlgorithm(child, dest, src, evidence, Direction.DOWN))
                            return true;
                    }
                }
            }
        }
        return false;
    } // BouncingBall

    private boolean cameFromParent(Variable v) {
        return cameFromParentList.get(v);
    }
    private boolean cameFromChild(Variable v) {
        return cameFromChildList.get(v);
    }
}

//    public String BayesBall(String bayesBallQuery) {
//        // A-B|E1=e1,E2=e2,...,Ek=ek    =>  Are A and B conditionally independent given Ei=ei ?
//
//        String[] vars_evidence = bayesBallQuery.split("\\|");
//        String[] vars = vars_evidence[0].split("-");
//        List<String> evidence, e;
//        if (vars_evidence.length <= 1) { // Evidence or vars are missing
//            evidence = new ArrayList<>();
//        }
//        else { // Positive number of evidence
//            // Temp evidence
//            e = Arrays.asList(vars_evidence[1].split(","));
//            evidence = new ArrayList<>();
//            for (String s : e) {
//                if (network.getNodes().contains(network.getNode(s.split("=")[0])))
//                    evidence.add(s.split("=")[0]);
//            }
//        }
//
//        String start = vars[0],
//                target = vars[1];
//        Variable s = network.getNode(start);
//        if (null == s) return start + " is not a variable (BayesBall)";
//        Variable p, c;
//
//        Collection<String> parents = s.getParents();
//        Collection<String> children = s.getChildren();
//        if (children.isEmpty()) {
////            System.out.println("No Children for Var " + start);
//            children = new ArrayList<>();
//        } else if (parents.isEmpty()) {
////            System.out.println("Var "+start+" has no parents");
//            parents = new ArrayList<>();
//        }
//
//        for (Variable v : network.getNodes())
//            _visited.put(v.getName(), "WHITE"); // All nodes are white as default. When processed they become BLACK.
//
//        for (String parent : parents){ // Execute DFS from each parent
//            p = network.getNode(parent);
//            if (target.equals(parent)) {
////                System.out.println(parent + " ==? " + target);
//                return "no";
//            }
////            System.out.println("DFS From: " + parent);
//            if (isTargetReachable(
//                    network.getNode(p.getName()),      // Source
//                    network.getNode(target),           // Target
//                    network.getNode(start),            // Previous node
//                    evidence))
//                return "no";
////            System.out.println(parent + " did not find " + target);
//        }
//
//        for (String child : children) {
//            if (target.equals(child)) {
////                System.out.println(child + " ==? " + target);
//                return "no";
//            }
//            c = network.getNode(child);
////            System.out.println("DFS From: " + child);
//            if (isTargetReachable(
//                    network.getNode(c.getName()),     // Source
//                    network.getNode(target),          // Target
//                    network.getNode(start),           // Previous node
//                    evidence))
//                return "no";
////            System.out.println(child + " did not find " + target);
//        }
//
////        return targetNotFoundText(start, target, evidence);
//        return "yes";
//    }

//    private boolean isTargetReachable(Variable src, Variable target,Variable prev, List<String> evidence) {
//        if (null == src || null == target || null == prev || null == evidence) return false;
//        if(_visited.get(src.getName()).equals("BLACK")) return false;
//
////        System.out.println("\t" + src.getName() + " is looking for " + target.getName());
////        System.out.println("\t" + src.getName() + " came from " + prev.getName());
//        if (isObserved(src.getName(), evidence)) {
////            System.out.println("\t\t" + src.getName() + " is Evidence!");
//            // Observed Node (Evidence node)
//            // Can only move to parents, if came from a parent
//            if (src.getParents().contains(prev.getName())) {
//
////                System.out.println("\t\t" + src.getName() + " came from parent");
////                System.out.println("\t\t" + src.getName() + " looking to move up");
//                for (String parent : src.getParents()) {
////                    System.out.println("\t\t\t- " + parent + ":");
//                    if (target.getName().equals(parent)) return true;
//                    if (_visited.get(parent).equals("BLACK"))
////                        System.out.println("Var " + parent + " is BLACK (Processed)");
//
////                    _prev.put(parent, src.getName());
////                    System.out.println("\t\t\ttry from: " + parent + "...");
//                        if (isTargetReachable(network.getNode(parent), target, src, evidence))
//                            return true;
////                    _prev.put(src.getName(), parent); // set prev[src] = parent
//                }
//            }
//        }
//        else { // Not observed(Not Evidence). Can move to any child, or, from child to parent.
////            System.out.println("\t\t" + src.getName() + " is NOT Evidence");
//
////            System.out.println("\t\t" + src.getName() + " came from parent");
//            for (String child : src.getChildren()) {
////                System.out.println("\t\t\t" + src.getName() + " looking for children");
//                if (target.getName().equals(child)) return true;
//                if (_visited.get(child).equals("BLACK"))
//                    System.out.println("Var " + child + " is BLACK (Processed)");
//
////                    _prev.put(child, src.getName());
////                System.out.println("\t\t\ttry from " + child + "...");
//                if (isTargetReachable(network.getNode(child), target, src, evidence))
//                    return true;
////                    _prev.put(src.getName(), child); // set prev[src] = parent
//            }
//
//            if (src.getChildren().contains(prev.getName())) { // if prev[src] in children(src)
//                // Coming from child => can move to any parent
////                System.out.println("\t\t" + src.getName() + " came from child");
//                for (String parent : src.getParents()) {
////                    System.out.println("\t\t\t" + src.getName() + " looking for parents...");
//                    if (target.getName().equals(parent)) return true;
//                    if (_visited.get(parent).equals("BLACK"))
//                        System.out.println("Var " + parent + " is BLACK (Processed)");
//
////                    _prev.put(parent, src.getName());
////                    System.out.println("\t\t\ttry from " + parent + "...");
//                    if (isTargetReachable(network.getNode(parent), target, src, evidence))
//                        return true;
////                    _prev.put(src.getName(), parent); // set prev[src] = parent
//                }
//            }
//
//        }
////        _visited.put(src.getName(), "BLACK"); // Mark as processed
//
//
//        return false;
//    }
