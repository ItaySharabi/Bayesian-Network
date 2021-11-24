import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Factor {

    private List<String> name; // Factor name represents all variable names that are independent from the sum
    private HashMap<List<String>, Double> rows;


    /**
     *
     * @param v - Variable object. This method read's its CPT.
     * @param parents_outcomes - List<String> in the format: A1=a1, A2=a2, ..., An=an
     */
    public Factor(String v, List<String> parents_outcomes) {
        // P(B=T|J=T,M=T)
        // IF all parents of `v` are given -> ?
        //



//        rows = v.getCPT();
        if (null == parents_outcomes || parents_outcomes.isEmpty()) return;

//        System.out.println("Factor " + v.getName());
//
//        for (List<String> keyEntry : v.getCPT().keySet()) {
//
//            for (String p_o : parents_outcomes) {
//
//                if (!keyEntry.contains(p_o)) // Rows to keep
//                    System.out.println("Keep row: " + keyEntry);
//                else // Rows to discard
//                    rows.remove(keyEntry);
////                    System.out.println("Discard row: " + keyEntry);
//            }
//        }

        System.out.println("New Factor("+name+"):");
        for (Map.Entry<List<String>, Double> entry : rows.entrySet())
            System.out.println(entry.getKey() + "|-> " + entry.getValue());
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

    public Factor(String var, List<String> parents,List<String> evidence) {}

    public Factor SumOut(){
//        return new Factor()
        return null;
    }

}
