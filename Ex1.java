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
            in = new Input("data/input2.txt");
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
                    algo.VariableEliminationMarginal("" +
                            "P(D1=T|C2=v1,C3=F) A2-C1-B0-A1-B1-A3-B2-B3"));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
