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
        Input input = null;
        try {
            input = new Input("input.txt");
            bn.loadNetworkFromXML(new XMLParser(), input.getXMLFilePath());

            Algorithms algo = new Algorithms(bn);

            StringBuilder out = new StringBuilder();

            for (String query : input.getQueries()) {
                if (isVariableEliminationQuery(query))
                    out.append(algo.VariableEliminationMarginal(query) + "\n");

                else // BayesBall Query
                    out.append(algo.BayesBall(query) + "\n");
            }

            FileWriter fw = new FileWriter(new File("output.txt"));
            fw.write(out.toString().stripTrailing());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static boolean isVariableEliminationQuery(String query) {
        return query.charAt(0) == 'P' &&
                query.charAt(1) == '(';
    }
}
