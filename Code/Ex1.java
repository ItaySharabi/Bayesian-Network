package Code;

import java.io.File;
import java.io.FileWriter;

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
