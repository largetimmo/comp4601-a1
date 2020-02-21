package dao.indexer;

import Jama.Matrix;
import dao.CrawlGraphDAO;
import dao.impl.CrawlGraphDAOImpl;
import dao.modal.CrawlGraphEntity;

import java.util.List;

public class PageRank {
    Double factor = 0.85;
    int max_iterations = 10;
    CrawlGraphDAO graph;

    public PageRank(){
        graph = CrawlGraphDAOImpl.getInstance();
    }

    public Matrix createConnectionMatrix(){
        List<CrawlGraphEntity> vertex = graph.findAll();
        Matrix matrix = new Matrix(vertex.size(),vertex.size());
        int row=0;
        for (CrawlGraphEntity c : vertex){
            int n = c.getEdges().size();
            List<String> l = c.getEdges();
            int col = 0;
            for (CrawlGraphEntity c2 : vertex){
                if (l.contains(c2.getId())){

                    double val = 1 * 1.0 / n;
                    matrix.set(row,col,val);

                    if (row == 0){
                        System.out.println("--------------------------------------------"+n);
                        System.out.println("--------------------------------------------"+matrix.get(row,col));
                    }
                }
                col++;
            }
            row++;
        }
        return matrix;
    }

    public static void main(String[] args) {
        PageRank p = new PageRank();
        Matrix matrix = p.createConnectionMatrix();


        //for(int i=0;i<matrix.getRowDimension();i++){
            for(int j=0;j<matrix.getColumnDimension();j++){
                System.out.println(matrix.get(0,j)+" ");
            }
            System.out.println();
        //}
    }
}
