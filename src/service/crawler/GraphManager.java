package service.crawler;

import dao.modal.CrawlDataEntity;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphManager {

    private static final GraphManager INSTANCE = new GraphManager();

    public static final GraphManager getInstance() {
        return INSTANCE;
    }

    public void add(Graph graph, String id, String parent_id) {
        if (!graph.vertexSet().contains(parent_id)) {
            graph.addVertex(parent_id);
        }
        if (!graph.vertexSet().contains(id)){
            graph.addVertex(id);
        }
        graph.addEdge(parent_id, id);


        System.out.println("Current size:" + graph.vertexSet().size());

    }

    public Graph<String,DefaultEdge> generateGraph(List<CrawlDataEntity> list){
        Graph<String,DefaultEdge>graph = new Multigraph<>(DefaultEdge.class);
        for(int i = 0; i < list.size();i++){
            for (int j = 0; j< list.get(i).getChildUrl().size();j++){
                add(graph,list.get(i).getUrl(),list.get(i).getChildUrl().get(j));
            }
        }
        return graph;

    }
}


