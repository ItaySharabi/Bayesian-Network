import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Input {

//    private class XMLParser implements bn_xml_parser{
//        private XMLParser(){}
//        public XMLParser(String xml_file_path) {}
//
//        @Override
//        public Collection<DummyVar> parseNodesFromXML(String xml_file_path) {
//            return null;
//        }
//    }
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
        String xmlFilePath = sc.nextLine();
        this.xmlFilePath = xmlFilePath;
        queries = new LinkedList<>();

        while (sc.hasNext())
            queries.add(sc.nextLine());

        System.out.println("Queries: " + queries.toString());


        System.out.println("------Parsing queries-------");
        // Parse queries as BayesBall and VariableElimination queries.
        System.out.println("------ Parsing Done  -------");
        sc.close();
    }

    public String getXMLFilePath() {
        return this.xmlFilePath;
    }

//    public Collection<Query> getBayesBallQueries();
//    public Collection<Query> getVarEliminationQueries();

    public Collection<bn_node> getNetworkVariables() {
        return null;
    }
}
