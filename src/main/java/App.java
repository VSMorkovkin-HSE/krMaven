import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        String tag = scan.nextLine();

        String queryString = makeGetQueryString(tag);

        run();
    }


    static Logger log = LoggerFactory.getLogger(App.class);
    private static final String ontoFile = "C:\\Users\\thegr\\Desktop\\familyTest.owl";
    private static OntModel ontoModel;

    private static void run() {
        makeModel();

        printClasses();
        System.out.println();

        executeQuery("PREFIX myont: <http://www.semanticweb.org/thegramanhuth2/ontologies/2022/5/famalyTest.owl#>\n" +
                "\n" +
                "\n" +
                "SELECT ?child WHERE { \n" +
                "\t?child myont:hasParent ?parent \n" +
                "\tFILTER ( ?parent=\"Ivan\")\n" +
                " }");

        ontoModel.
    }

    private static void makeModel() {
        OntDocumentManager mgr = new OntDocumentManager();
        OntModelSpec s = new OntModelSpec(OntModelSpec.OWL_MEM);
        s.setDocumentManager(mgr);

        ontoModel = ModelFactory.createOntologyModel(s);
    }

    private static String makeGetQueryString(String tag) {
        return "PREFIX bycOnt: <http://www.semanticweb.org/thegramanhuth2/ontologies/2022/5/famalyTest.owl#>\n" +
                "SELECT ?s ?p ?o WHERE { ?s ?p ?o FILTER (?s = bycOnt:" + tag + ")}";
    }

    private static void printClasses() {
        try {
            //InputStream in = FileManager.get().open(ontoFile);
            InputStream in = RDFDataMgr.open(ontoFile);
            try {
                ontoModel.read(in, null);
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
            log.info("Ontology" + ontoFile + " loaded");
        } catch (JenaException je) {
            System.err.println("ERROR" + je.getMessage());
            je.printStackTrace();
            System.exit(0);
        }
    }

    private static void executeQuery(String query) {
        try (QueryExecution qExec = QueryExecutionFactory.create(query, ontoModel)) {
            ResultSet results = qExec.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();

                System.out.println(solution.getLiteral("child"));

                //Literal name = solution.getLiteral("child");
                //System.out.println(name);
            }
        }
    }
}
