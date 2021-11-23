import java.util.*;

public class Algorithms {

    private /*static*/ BayesianNetwork network;

    private Algorithms() {}

    public Algorithms(BayesianNetwork network) {
        this.network = network;
    }

    /**
     * So far this is correct. Need to implement some t
     */
    private HashMap<String, String> _visited;
    private HashMap<String, String> _prev;
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
            for (int i = 0; i < e.size(); i++) {
                if (network.getNodes().contains(network.getNode(e.get(i).split("=")[0])))
                    evidence.add(e.get(i).split("=")[0]);
            }
        }

        String start = vars[0],
               target = vars[1];

        Collection<Variable> parents = network.getParents(start);
        Collection<Variable> children = network.getChildren(start);
        if (children.contains(null)) {
            System.out.println("No Children for Var " + start);
            children = new ArrayList<>();
        }

        _visited = new HashMap<>();
        _prev = new HashMap<>();

        for (Variable v : network.getNodes())
            _visited.put(v.getName(), "WHITE"); // All nodes are white as default. When processed they become BLACK.

        for (Variable parent : parents){ // Execute DFS from each parent
            if (isTargetReachable(
                    network.getNode(parent.getName()), // Source
                    network.getNode(target),           // Target
                    network.getNode(start),            // Previous node
                    evidence))
                return targetFoundText(
                        start, target, evidence);
        }

        for (Variable child : children) {
            if (isTargetReachable(
                    network.getNode(child.getName()), // Source
                    network.getNode(target),          // Target
                    network.getNode(start),           // Previous node
                    evidence))
                return targetFoundText(
                        start, target, evidence);
        }

        return targetNotFoundText(start, target, evidence);
    }

    private boolean isTargetReachable(Variable src, Variable target,Variable prev, List<String> evidence) {
        if (null == src || null == target || null == prev || null == evidence) return false;
        if(_visited.get(src.getName()).equals("BLACK")) return false;


        if (evidence.contains(src.getName())) {
        // Observed Node (Evidence node)
        // Can only move to parents, if came from a parent
            if (src.getParents().contains(_prev.get(src.getName()))) {

                for (String parent : src.getParents()) {
                    if (target.getName().equals(parent)) return true;
                    if (_visited.get(parent).equals("BLACK"))
                        System.out.println("Var " + parent + " is BLACK (Processed)");

//                    _prev.put(parent, src.getName());
                    if (isTargetReachable(network.getNode(parent), target, src, evidence))
                        return true;
//                    _prev.put(src.getName(), parent); // set prev[src] = parent
                }
            }
        }
        else { // Not observed. Can move to any child, or, from child to parent.

            if (src.getChildren().contains(prev.getName())) { // if prev[src] in children(src)
                // Coming from child => can move to any parent
                for (String parent : src.getParents()) {
                    if (target.getName().equals(parent)) return true;
                    if (_visited.get(parent).equals("BLACK"))
                        System.out.println("Var " + parent + " is BLACK (Processed)");

//                    _prev.put(parent, src.getName());
                    if (isTargetReachable(network.getNode(parent), target, src, evidence))
                        return true;
//                    _prev.put(src.getName(), parent); // set prev[src] = parent
                }
            }
            else { // Can move to any child
                for (String child : src.getChildren()) {
                    if (target.getName().equals(child)) return true;
                    if (_visited.get(child).equals("BLACK"))
                        System.out.println("Var " + child + " is BLACK (Processed)");

//                    _prev.put(child, src.getName());
                    if (isTargetReachable(network.getNode(child), target, src, evidence))
                        return true;
//                    _prev.put(src.getName(), child); // set prev[src] = parent
                }
            }
        }
        _visited.put(src.getName(), "BLACK"); // Mark as processed


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

    /**
     * Check Conditional independence between Vars:
     * @param var
     * @param outcome
     * @param evidence
     * @param vars_to_eliminate
     * @return
     */
    public String VariableElimination(String var, String outcome, List<String> evidence, List<String> vars_to_eliminate) {

        /*
        1. Check Conditional independence between Vars:
        2. Start "Factorizing" variables
        3.
         */

        // Factorize Variables:

        List<Factor> factors = new ArrayList<>();




        return null;
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
