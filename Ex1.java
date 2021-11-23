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
            in = new Input("C:\\Users\\User\\IdeaProjects\\BayesianNetworkAlgorithms\\src\\input.txt");
            bn.loadNetworkFromXML(new XMLParser(), in.getXMLFilePath());


//            for (Map.Entry<List<String>, Double> entry : bn.getCPT("A"))
//                System.out.println(entry.getKey() + " |-> " + entry.getValue());

            Algorithms algo = new Algorithms(bn);
            String start = "J";
            String target = "B";
            String evidence = "A,C";
            String bayesBallQuery = start + "-" + target + "|" + evidence;

            System.out.println(
                    algo.BayesBall(bayesBallQuery));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
