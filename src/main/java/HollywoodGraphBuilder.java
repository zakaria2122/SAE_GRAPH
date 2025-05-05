import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HollywoodGraphBuilder {

    public static Graph<String, DefaultEdge> buildGraphFromJson(String filePath) throws IOException {
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(filePath));
        reader.setLenient(true);

        reader.beginArray();  // Commence le tableau JSON
        while (reader.peek() != com.google.gson.stream.JsonToken.END_DOCUMENT) {
            Movie movie = gson.fromJson(reader, Movie.class);
            if (movie.cast != null) {
                Set<String> actors = new HashSet<>();
                for (String rawActor : movie.cast) {
                    String actor = cleanActorName(rawActor);
                    if (!actor.isBlank()) {
                        actors.add(actor);
                        graph.addVertex(actor);
                    }
                }
                for (String a1 : actors) {
                    for (String a2 : actors) {
                        if (!a1.equals(a2)) {
                            graph.addEdge(a1, a2);
                        }
                    }
                }
            }
        }
        reader.endArray();  // Fin du tableau
        reader.close();

        return graph;
    }

    private static String cleanActorName(String raw) {
        return raw.replaceAll("\\[\\[|\\]\\]", "").split("\\|")[0].trim();
    }

    public static void main(String[] args) {
        try {
            Graph<String, DefaultEdge> graph = buildGraphFromJson("data/data_100.txt");
            System.out.println("Nombre d'acteurs : " + graph.vertexSet().size());
            System.out.println("Nombre de collaborations : " + graph.edgeSet().size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}