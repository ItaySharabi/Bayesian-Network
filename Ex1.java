import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Scanner;

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
            in = new Input("input.txt");
            bn.loadNetworkFromXML(new XMLParser(), in.getXMLFilePath());

            Algorithms algo = new Algorithms(bn);

            String out = "";

            for (String query : in.getQueries()) {
                if (query.charAt(0) == 'P' &&
                    query.charAt(1) == '(')
//                     Variable Elimination Query:
                    out += algo.VariableEliminationMarginal(query) + "\n";
                else
                    out += algo.BayesBall(query) + "\n";
            }

//            System.out.println(out);

            FileWriter fw = new FileWriter(new File("output.txt"));
            fw.write(out);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
