package Code;

import java.io.File;
import java.io.FileWriter;


/**
 * Artificial Intelligence Assignment:
 * @author Itay Sharabi
 * This is an implementation of a `Bayesian Network` (Graph) model.
 * A Bayesian Network is a graphical representation of a set of
 * `Random Variable`s (Probability Theory) which are the graph's nodes
 * and the conditional relation between them as the graph's edges.
 * This model can answer queries such as:
 * 1. What is the probability of `Q` happening, given the evidence: E1, E2, ..., Ek ?
 * This query is notated: P(Q|E1,E2,...,Ek)
 *
 * 2. What is the conditional relation between A and B, given E1, E2, ..., Ek ?
 * This query is notated: A-B|E1,E2,...,Ek
 */

public class Ex1 {

    public static void main(String[] args) {

        BayesianNetwork bn = new BayesianNetwork();

        /* Code.Input object will parse data from a file name input.txt */
        Input input = null;
        try {
            input = new Input("Data/input.txt");
            bn.loadNetworkFromXML(new XMLParser(), input.getXMLFilePath());

            Algorithms algo = new Algorithms(bn);

            StringBuilder out = new StringBuilder();

            for (String query : input.getQueries()) {
                if (isVariableEliminationQuery(query))
                    out.append(algo.VariableEliminationMarginal(query) + "\n");

                else // BayesBall Query
                    out.append(algo.BayesBall(query) + "\n");
            }

            FileWriter fw = new FileWriter(new File("Data/output.txt"));
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
