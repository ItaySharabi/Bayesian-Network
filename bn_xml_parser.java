import java.util.Collection;

public interface bn_xml_parser {
    public Collection<Variable> parseNodesFromXML(String path_to_bn_xml_file) throws Exception;
}
