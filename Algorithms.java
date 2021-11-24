import java.util.*;

public class Algorithms {

    private /*static*/ BayesianNetwork network;

    private Algorithms() {}

    public Algorithms(BayesianNetwork network) {
        this.network = network;
    }

//    private String VariableElimination(){return "";}
    /**
     * Check Conditional independence between Vars:
     * @param varEliminationQuery
     * @return
     */
    public String VariableElimination(String varEliminationQuery) {

        /*
        1. Check Conditional independence between Vars:
        2. Start "Factorizing" variables
        3.
         */


        // Factorize Variables:

//        String v_outcome = "";
        String[] ev = varEliminationQuery.split("\\|");
        String e = ev[1].split("\\)")[0];

//        System.out.println(Arrays.toString(ev));

        List<String> evidence = Arrays.asList(e.split(","));
        System.out.println("Evidence parsed: " + evidence);

        List<Factor> factors = new ArrayList<>();

        List<String> parent_variables_given;

        // Set initial `Factors`
        for (Variable v : network.getNodes())
            factors.add(new Factor(v, evidence));






        return "Answer (double)";
    }



    /**
     * So far this is correct. Need to implement some t
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
        if (null == s) return "no, " + start + " is not a variable";
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

        return targetNotFoundText(start, target, evidence);
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
