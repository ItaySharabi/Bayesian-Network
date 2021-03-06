package Code;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


/**
 * `Code.Input` class will handle parsing the input.txt file
 * given to read and load Bayesian Network object from an XML file.
 * This class will also parse the `Bayesian Code.Algorithms` queries from the input.txt file.
 */
public class Input {

    private String xmlFilePath;
    private Queue<String> queries;

    private Input(){}
    public Input(String inputFilePath) throws Exception{
        // Unload XML file path to a Bayesian Network
        // Unload queries for each algorithm
        // Unload Network Variables

        parse(inputFilePath);
    }

    private void parse(String filePath) throws Exception {
        Scanner sc = new Scanner(new File(filePath));

        if (!sc.hasNext()) throw new Exception("File is in wrong format");
        this.xmlFilePath = sc.nextLine();
        queries = new LinkedList<>();

        while (sc.hasNext())
            queries.add(sc.nextLine());

//        System.out.println("Queries: " + queries.toString());


//        System.out.println("------Parsing queries-------");
        // Parse queries as BayesBall and VariableElimination queries. (??)
//        System.out.println("------ Parsing Done  -------");
        sc.close();
    }

    public Queue<String> getQueries() {
        return this.queries;
    }

    public String getXMLFilePath() {
        return this.xmlFilePath;
    }

}
