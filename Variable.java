import java.util.*;

public class Variable {

    private String name;
    private List<String> outcomes, parents, children;
    private HashMap<List<String>, Double> CPT; // Conditional Probability Table

    private Variable(){}

    public Variable(String name, String[] outcomes){
        this.name = name;

        this.outcomes = new ArrayList<>();
        this.outcomes.addAll(Arrays.asList(outcomes));
    }

    public String getName() {
        return this.name;
    }

    public List<String> getOutcomes() {
        return new ArrayList<>(this.outcomes);
    }

    public List<String> getParents() {
        return new ArrayList<>(this.parents);
    }

    public  List<String> getChildren() {
        return new ArrayList<>(this.children);
    }



    public HashMap<List<String>, Double> getCPT() {
        return copyCPT(this.CPT);
    }

    public void setChildren(String[] children) {
        if (null != this.children) return; // Do not allow changes in data
        this.children = new ArrayList<>();
        this.children.addAll(Arrays.asList(children));
    }

    public void setParents(String[] parents) {
        if (null != this.parents) return; // Do not allow changes in data
        this.parents = new ArrayList<>();
        this.parents.addAll(Arrays.asList(parents));
    }

    /**
     * This setter receives a Map object which contains this Variable's Conditional Probability Table.
     * Example:
     *  111  011  110  010  101  001  100   000
     * [0.95 0.05 0.29 0.71 0.94 0.06 0.001 0.999]
     *
     * @param CPT - A Map object, where keys are String lists and values are doubles, representing
     *            the corresponding probability value to the list of Strings.
     */
    public void setCPT(HashMap<List<String>, Double> CPT) {
        if (null != this.CPT || null == CPT) return; // Do not allow changes in data
        this.CPT = copyCPT(CPT);
    }

    private HashMap<List<String>, Double> copyCPT(HashMap<List<String>, Double> map) {

        HashMap<List<String>, Double> copy = new HashMap<>();

        for (Map.Entry<List<String>, Double> entry : map.entrySet())
            copy.put(entry.getKey(), entry.getValue());

        return copy;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", outcomes=" + outcomes +
//                ", parents=" + parents +
//                ", children=" + children +
                ", CPT=" + CPT +
                '}';
    }
}