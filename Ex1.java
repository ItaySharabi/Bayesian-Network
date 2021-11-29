public class Ex1 {

    public static void main(String[] args) {

        BayesianNetwork bn = new BayesianNetwork();

        // Input object that parses:
        // - An XML file path for xml representation of a bayesian network,
        // - Queries for BayesBall/VariableElimination algorithms.
        // - *** Practically, this class should also parse the nodes from the xml, but,
        // I've decided to let another class handle this job, XMLParser.
        Input in = null;
        try {
            in = new Input("data/input.txt");
            bn.loadNetworkFromXML(new XMLParser(), in.getXMLFilePath());


//            for (Map.Entry<List<String>, Double> entry : bn.getCPT("A"))
//                System.out.println(entry.getKey() + " |-> " + entry.getValue());

            Algorithms algo = new Algorithms(bn);
            String start = "B";
            String target = "E";
            String evidence = "A";
            String bayesBallQuery = start + "-" + target + "|" + evidence;
            String varEliminationQuery = start + "=T|" + target + "=T" + evidence;

            System.out.println(
                    algo.BayesBall("A-B|"));

            System.out.println(
                    algo.VariableEliminationMarginal("P(B=T|J=T,M=T) A-E"));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
