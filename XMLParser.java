import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.*;

public class XMLParser implements bn_xml_parser {
    private HashMap<String, Variable> vars;

    public XMLParser(){}

    @Override
    public Collection<Variable> parseNodesFromXML(String xmlFilePath) {
        try {
            File inputFile = new File(xmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement();

            NodeList variables = doc.getElementsByTagName("VARIABLE");
            NodeList definitions = doc.getElementsByTagName("DEFINITION");

            String name = "",
                    outcomes = "",
                    parents = "", children = "",
                    probabilities[] = null;
            vars = new HashMap<>();

            String c;
//            DummyVar dummyVar;

            for (int n = 0; n < variables.getLength(); n++) {
//                dummyVar = new DummyVar();
                Node nNode = variables.item(n);

                // Get variable's name
                Element varElement = (Element) nNode;
                name = varElement.getElementsByTagName("NAME").item(0).getTextContent();

                // Get variables outcomes
                outcomes = "";
                NodeList outTags = ((Element) nNode).getElementsByTagName("OUTCOME");
                for (int i = 0; i < outTags.getLength(); i++)
                    outcomes += outTags.item(i).getTextContent() + " ";

                // Save name and outcomes as a new variable. More data will be added to each var, i.e. Probability table
//                dummyVar.name = name;
//                dummyVar.outcomes = outcomes;
                vars.put(name, new Variable(name, outcomes.split(" ")));

                // Get variable's children from all tags which are not not meant for current node
                // but contains current node as a parent
                children = "";
                for (int i = 0; i < definitions.getLength(); i++) {
                    Element def = (Element) definitions.item(i);
                    String givenFor = def.getElementsByTagName("FOR").item(0).getTextContent();

                    if (name.equals(givenFor)) continue; // skip current variable's <FOR> tag

                    NodeList givenTags = def.getElementsByTagName("GIVEN");
                    for (int j = 0; j < givenTags.getLength(); j++) {
                        Element given = (Element) givenTags.item(j);
                        c = given.getTextContent();
                        if (name.equals(c))
                            children += givenFor + " ";
                    }
                }
                if (!children.isEmpty())
                    vars.get(name).setChildren(children.split(" ")); // Set children
                else
                    vars.get(name).setChildren(new String[0]); // Prevents Null Ptr Excpt.
            }

            String g;
            // Get current variable's parents
            for (int i = 0; i < definitions.getLength(); i++) {
                Element def = ((Element) definitions.item(i));
                name = def.getElementsByTagName("FOR").item(0).getTextContent();

                NodeList givenTags = def.getElementsByTagName("GIVEN");
                parents = "";

                for (int j = 0; j < givenTags.getLength(); j++) {
                    Element given = (Element) givenTags.item(j);
                    g = given.getTextContent();
                    parents += g + " ";
                }
//                vars.get(name).setParents(parents.split(" ")); // Append parents
                if (!parents.isEmpty())
                    vars.get(name).setParents(parents.split(" ")); // Set parents
                else
                    vars.get(name).setParents(new String[0]); // Prevents Null Ptr Excpt.

                // Set CPT: (TEMPORARY)
                // TODO: 11/4/2021 : Check what to do about CPT values!
                Element tableTag = (Element) ((Element) definitions.item(i)).getElementsByTagName("TABLE").item(0);
                probabilities = tableTag.getTextContent().split(" ");

                // TODO: 11/22/2021 Done!  Change name, add comments
                createCPTSomehow(vars.get(name), probabilities);
            }



            return vars.values();
        } catch (FileNotFoundException e) {
            System.out.println("File was not found! " + getClass().getName());
            return null;
        }
        catch (Exception e) {
            System.out.println("Error occurred!\n" + e.getMessage());
            return null;
        }
    }


    private void createCPTSomehow(Variable v, String[] probabilities) {

        // Should I reverse parents arraylist? because the given input sample supports that...

        List<String> parents = vars.get(v.getName()).getParents();
//        List<String> v_outcomes = vars.get(v.getName()).getOutcomes();
        Collections.reverse(parents); // ?

        HashMap<List<String>, BigDecimal> CPT;
        int
            n = probabilities.length,
            leap = v.getOutcomes().size(); // |Support(v)| [- 1 ??]


        List<List<String>> keys = new ArrayList<>();

//        double probability;

        for (int i = 0; i < n; i++) {
            keys.add(new ArrayList<>());
            keys.get(i).add(v.getName() + "=" + v.getOutcomes().get(i%v.getOutcomes().size()));
        }


        Variable parent;

        // If a variable has no parents he will skip the next section of code
        // and return the next Map object:
        if (parents.size() == 0 || parents.get(0).equals("") || null == parents.get(0)) {
            CPT = new HashMap<>();
            int i = 0;
            List<String> key;
            for (String outcome : v.getOutcomes()) {
                key = new ArrayList<>();
                key.add(v.getName() + "=" + outcome);
                CPT.put(key,
                        BigDecimal.valueOf(Double.parseDouble(probabilities[i++])));
//                        Double.parseDouble(probabilities[i++]));
            }
            v.setCPT(CPT);
            return;
        }

        int pi = 0; // current parent index
        int p_outcome = 0;

        while (pi < parents.size()) { // Iterating over `parents` list (Reversed)
            parent = vars.get(parents.get(pi)); // apply next

            for (int i = 0; i < n;) { // Iterate over `rows` of the CPT

                for (int j = 0; j < leap && leap < n; j++)
                    keys.get(i+j)
                            // Append to i'th row the term (String) "A=a1"
                            .add(parents.get(pi) + "=" + parent.getOutcomes().get(p_outcome));

                p_outcome = (p_outcome + 1) % parent.getOutcomes().size();
                i += leap;
            }
            leap *= parent.getOutcomes().size(); // for next parent we multiply the leap param with the support size
            // of the parent
            pi++; // next parent
        }
        // Create the Map object representing Variable `v` CPT.
        CPT = new HashMap<>();

        int i = 0;
        for (List<String> list : keys) // Append to Map:  <["A1=a1", ..., "Ek=ek"], p1>
            CPT.put(list,
                    BigDecimal.valueOf(Double.parseDouble(probabilities[i++])));

        v.setCPT(CPT);
    }
}
