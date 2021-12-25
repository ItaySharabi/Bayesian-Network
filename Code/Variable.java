package Code;

import java.math.BigDecimal;
import java.util.*;

public class Variable implements Comparable<Variable>{

    private String name;
    private List<String> outcomes, parents, children;
    private HashMap<List<String>, BigDecimal> CPT; // Conditional Probability Table

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
        if (this.outcomes == null) return null;
        return new ArrayList<>(this.outcomes);
    }

    public List<String> getParents() {
        if (this.parents == null) return null;
        return new ArrayList<>(this.parents);
    }

    public  List<String> getChildren() {
        if (this.children == null) return null;
        return new ArrayList<>(this.children);
    }



    public HashMap<List<String>, BigDecimal> getCPT() {
        if (this.CPT == null) return null;
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
     * This setter receives a Map object which contains this Code.Variable's Conditional Probability Table.
     * Example:
     *  111  011  110  010  101  001  100   000
     * [0.95 0.05 0.29 0.71 0.94 0.06 0.001 0.999]
     *
     * @param CPT - A Map object, where keys are String lists and values are doubles, representing
     *            the corresponding probability value to the list of Strings.
     */
    public void setCPT(HashMap<List<String>, BigDecimal> CPT) {
        if (null != this.CPT || null == CPT) return; // Do not allow changes in data
        this.CPT = copyCPT(CPT);
        Iterator<List<String>> it = this.CPT.keySet().iterator();
        List<String> parents_reorganized = it.next();
        parents = new ArrayList<>();
        for (String s : parents_reorganized) // remove `=s` from `S=s`
            parents.add(s.split("=")[0]);
        parents.remove(parents.indexOf(name)); // remove self.name from the list
    }

    private HashMap<List<String>, BigDecimal> copyCPT(HashMap<List<String>, BigDecimal> map) {

        HashMap<List<String>, BigDecimal> copy = new HashMap<>();

        List<String> copiedKey;
        for (List<String> key : map.keySet()) {
            copiedKey = new ArrayList<>(key);
            copy.put(copiedKey, map.get(key));
        }

        return copy;
    }

    @Override
    public String toString() {
        return "Code.Variable{" +
                "name='" + name + '\'' +
                ", outcomes=" + outcomes +
//                ", parents=" + parents +
//                ", children=" + children +
//                ", CPT=" + CPT +
                '}';
    }

    @Override
    public int compareTo(Variable o) {

        System.out.println("asciiSum("+this.getName()+"): " + asciiSum(this.getName()));
        System.out.println("asciiSum("+o.getName()+"): " + asciiSum(o.getName()));


        return asciiSum(this.getName()) - asciiSum(o.getName());
    }

    private int asciiSum(String s) {
        int sum = 0;
        for (char c : s.toCharArray())
            sum += (int)c;
        return sum;
    }
}