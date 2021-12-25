package Tests;

import Code.Algorithms;
import Code.BayesianNetwork;
import Code.Input;
import Code.XMLParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.*;

class Ex1Test {

    String[] files = {"Data/input.txt", "Data/input2.txt", "Data/my_input.txt"};
    int fileNum = 0;

    @Test
    void testInputs() {
        run(files[fileNum++]);
        run(files[fileNum++]);
        run(files[fileNum++]);
    }


    void run(String inputFile) {
        BayesianNetwork bn = new BayesianNetwork();

        Input input = null;
        /* Code.Input object will parse data from a file name input.txt */
        try {
            input = new Input(inputFile);
            bn.loadNetworkFromXML(new XMLParser(), input.getXMLFilePath());
            assertNotNull(input);
            Algorithms algo = new Algorithms(bn);

            StringBuilder out = new StringBuilder();

            for (String query : input.getQueries()) {
                if (isVariableEliminationQuery(query))
                    out.append(algo.VariableEliminationMarginal(query) + "\n");

                else // BayesBall Query
                    out.append(algo.BayesBall(query) + "\n");
            }

            FileWriter fw = new FileWriter(new File("Data/output" + fileNum + ".txt"));
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