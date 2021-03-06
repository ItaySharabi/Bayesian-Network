package Code;

import Code.API.bn_xml_parser;

import java.math.BigDecimal;
import java.util.*;

public class BayesianNetwork {

    private HashMap<String, Variable> nodes;
    private HashMap<String, Collection<Variable>> inEdges;
    private HashMap<String, Collection<Variable>> outEdges;

    public BayesianNetwork(){
        nodes = new HashMap<>();
        inEdges = new HashMap<>();
        outEdges = new HashMap<>();
    }

    public BayesianNetwork(BayesianNetwork bn) {
        loadNetworkFromNodeCollection(bn.getNodes());
    }

    public boolean loadNetworkFromNodeCollection(Collection<Variable> bayesian_network_variables) {

        if (nodes == null) return false;

        this.nodes = new HashMap<>();

        for (Variable var : bayesian_network_variables)
            this.nodes.put(var.getName(), var);

        for (Variable var : nodes.values()) {

            String v_name = var.getName();
            Variable v = nodes.get(v_name);
            inEdges.put(v_name, new ArrayList<>());
            outEdges.put(v_name, new ArrayList<>());

            for (String parent : v.getParents())
                inEdges.get(v_name).add(
                        nodes.get(parent));

            for (String child : v.getChildren())
                outEdges.get(v_name).add(
                        nodes.get(child));
        }
        return true;
    }

    
    public Collection<Variable> getNodes() {
        return this.nodes.values();
    }

    public Variable getNode(String name) {
        name = name.split("=")[0];
        if (!nodes.containsKey(name)) {System.out.println(name + " does not exist in the network!"); return null;}
        return this.nodes.get(name);
    }

    public Set<Map.Entry<List<String>, BigDecimal>> getCPT(String name) {
        return this.nodes.get(name).getCPT().entrySet();
    }

    public boolean loadNetworkFromXML(bn_xml_parser xml_parser, String xml_file_path) {
        try {
                loadNetworkFromNodeCollection(
                        xml_parser.parseNodesFromXML(xml_file_path));
                return true;
        } catch (Exception e) {
            System.out.println("An error occurred while loading the network from " + xml_file_path);
            e.printStackTrace();
        }
        return false;
    }
}
//    public List<Code.Variable> getParents(String name) {
//        if (!nodes.containsKey(name)) return new ArrayList<>();
//        List<Code.Variable> parents = new ArrayList<>();
//        for (String s : nodes.get(name).getParents())
//            parents.add(nodes.get(s));
//        return parents;
//    }
//    public Collection<Code.Variable> getChildren(String name) {
//        if (!nodes.containsKey(name)) return new ArrayList<>();
//        List<Code.Variable> children = new ArrayList<>();
//        for (String s : nodes.get(name).getChildren())
//            children.add(nodes.get(s));
//        return children;
//    }
