import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HollywoodGraphBuilder {

    public static Graph<String, DefaultEdge> buildGraphFromJson(String filePath) throws IOException {
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        Gson gson = new Gson();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineCount = 0;
            
            while ((line = br.readLine()) != null) {
                lineCount++;
                if (line.trim().isEmpty()) continue;
                
                try {
                    // Analyser chaque ligne comme un objet Movie séparé
                    Movie movie = gson.fromJson(line, Movie.class);
                    processMovie(movie, graph);
                } catch (JsonSyntaxException e) {
                    System.err.println("Erreur à la ligne " + lineCount + ": " + e.getMessage());
                    // Continuer à la prochaine ligne même si celle-ci est incorrecte
                }
            }
        }
        
        return graph;
    }
    
    private static void processMovie(Movie movie, Graph<String, DefaultEdge> graph) {
        if (movie != null && movie.cast != null) {
            Set<String> actors = new HashSet<>();
            for (String rawActor : movie.cast) {
                if (rawActor != null) {
                    String actor = cleanActorName(rawActor);
                    if (!actor.isBlank()) {
                        actors.add(actor);
                        graph.addVertex(actor);
                    }
                }
            }
            
            // Ajouter les arêtes entre tous les acteurs du film
            for (String a1 : actors) {
                for (String a2 : actors) {
                    if (!a1.equals(a2) && !graph.containsEdge(a1, a2)) {
                        graph.addEdge(a1, a2);
                    }
                }
            }
        }
    }

    private static String cleanActorName(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("\\[\\[|\\]\\]", "").split("\\|")[0].trim();
    }

    public static void main(String[] args) {
        try {
            Graph<String, DefaultEdge> graph = buildGraphFromJson("data/data_100.txt");
            System.out.println("Nombre d'acteurs : " + graph.vertexSet().size());
            System.out.println("Nombre de collaborations : " + graph.edgeSet().size());
        } catch (IOException e) {
            System.err.println("Erreur d'entrée/sortie: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
    }
}