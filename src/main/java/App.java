import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.io.InputStream;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {

        makeOntologyModel();

        String tag = getStringFromConsole();
        String queryString = makeGetQueryString(tag);
        executeQuery(queryString);

        //printClasses();
    }

    private static final String ONTO_FILE = "BicycleOntology.owl";
    private static final String ONTO_IRI = "http://www.semanticweb.org/vsmorkovkin/ontologies/2022/5/kr";
    private static OntModel ontoModel;


    private static void makeOntologyModel() throws JenaException {
        OntDocumentManager mgr = new OntDocumentManager();
        OntModelSpec s = new OntModelSpec(OntModelSpec.OWL_MEM);
        s.setDocumentManager(mgr);

        ontoModel = ModelFactory.createOntologyModel(s);

        InputStream in = RDFDataMgr.open(ONTO_FILE);
        ontoModel.read(in, null);
    }

    private static String makeGetQueryString(String tag) {
        return "PREFIX bycOnt: <" + ONTO_IRI + "#>\n" +
                "SELECT DISTINCT ?s ?p ?o WHERE { ?s ?p ?o FILTER (?s = bycOnt:" + tag + " ) }";
    }

    private static void printClasses() {
        try {
            ExtendedIterator<OntClass> classes = ontoModel.listClasses();
            while (classes.hasNext()) {
                OntClass theClass = classes.next();
                String className = theClass.getLocalName();
                if (className != null) {
                    System.out.println("ClassName: " + className);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeQuery(String query) {
        try (QueryExecution qExec = QueryExecutionFactory.create(query, ontoModel)) {
            ResultSet results = qExec.execSelect();

            if (!results.hasNext()) {
                System.out.println("Elements not found");
            }

            while (results.hasNext()) {
                QuerySolution solution = results.next();
                System.out.print(extractName(solution.get("s").toString()) + " ");
                System.out.print(extractName(solution.get("p").toString()) + " ");
                System.out.println(extractName(solution.get("o").toString()));
            }
        }
    }

    private static String extractName(String resource) {
        boolean gridWasFound = false;
        int startIndex = 0;
        for (int i = 0; i < resource.length(); ++i) {
            if (resource.charAt(i) == '#') {
                startIndex = i + 1;
                gridWasFound = true;
                break;
            }
        }

        if (gridWasFound) {
            return resource.substring(startIndex);
        }
        return resource;
    }

    private static String extractName(String resource, String prefix) {
        if (prefix.length() > resource.length()) {
            return resource;
        }

        StringBuilder sb = new StringBuilder();
        boolean prefixContains = true;

        for (int i = 0; i < prefix.length(); ++i) {
            if (resource.charAt(i) != prefix.charAt(i)) {
                prefixContains = false;
                break;
            }
        }

        if (!prefixContains) {
            return resource;
        }
        sb.append(resource.substring(prefix.length()));
        return sb.toString();
    }

    private static String getStringFromConsole() {
        Scanner scan = new Scanner(System.in);
        return scan.nextLine();
    }
}
