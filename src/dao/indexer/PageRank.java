package dao.indexer;

import Jama.Matrix;
import dao.CrawlDataDAO;
import dao.impl.CrawlDataDAOImpl;
import dao.modal.CrawlDataEntity;

import java.util.List;

public class PageRank {
    Double factor = 0.85;
    int max_iterations = 10;
    CrawlDataDAO data;

    public PageRank(){
        data = CrawlDataDAOImpl.getInstance();
    }

    public Matrix createConnectionMatrix(){
        List<CrawlDataEntity> vertex = data.findAll();
        Matrix matrix = new Matrix(vertex.size(),vertex.size());
        int row=0;
        for (CrawlDataEntity c : vertex){
            int n = c.getChildUrl().size();
            List<String> l = c.getChildUrl();
            int col = 0;
            for (CrawlDataEntity c2 : vertex){
                if (l.contains(c2.getUrl())){

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
