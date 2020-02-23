package dao.indexer;

import dao.CrawlDataDAO;
import dao.impl.CrawlDataDAOImpl;
import dao.modal.CrawlDataEntity;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;
import java.util.Map;

public class PageRank {

    CrawlDataDAO graph;

    public PageRank() {
        graph = CrawlDataDAOImpl.getInstance();
    }


    public void calculatePageRank(){
        List<CrawlDataEntity> list = graph.findAll();
        DirectedMultigraph<String, DefaultEdge> directedMultigraph = new DirectedMultigraph<>(DefaultEdge.class);

        for(CrawlDataEntity entity: list){
            if(!directedMultigraph.containsVertex(entity.getUrl())){
                directedMultigraph.addVertex(entity.getUrl());
            }
            for(String childUrl: entity.getChildUrl()){
                if(!directedMultigraph.containsVertex(childUrl)){
                    directedMultigraph.addVertex(childUrl);
                }
                if(entity.getUrl().equals(childUrl)){
                    continue;
                }
                directedMultigraph.addEdge(entity.getUrl(),childUrl);
            }

        }
        System.out.println(directedMultigraph.vertexSet().size());
        org.jgrapht.alg.scoring.PageRank<String,DefaultEdge> rank = new org.jgrapht.alg.scoring.PageRank<>(directedMultigraph);
        Map<String,Double> rankMap = rank.getScores();
        System.out.println(rankMap.get("https://sikaman.dyndns.org/courses/4601/lecture-9/N-5.html"));
        for(Map.Entry<String,Double> entry : rankMap.entrySet()){
            System.out.println(entry.getKey());
            CrawlDataEntity crawlDataEntity = graph.findByUrl(entry.getKey());
            if (crawlDataEntity == null){
                continue;
            }
            crawlDataEntity.setScore(entry.getValue());
            graph.update(crawlDataEntity);
        }
    }

    public static void main(String[] args) {
        PageRank p = new PageRank();
        p.calculatePageRank();


    }
}