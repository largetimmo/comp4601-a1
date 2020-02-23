package dao.indexer;

import Jama.Matrix;
import dao.CrawlDataDAO;
import dao.impl.CrawlDataDAOImpl;
import dao.modal.CrawlDataEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PageRank {

    CrawlDataDAO graph;
    Matrix matrix;
    double a = 0.1;
    HashMap<Integer, double[]> pageRanks = new HashMap<Integer, double[]>();

    public PageRank() {
        graph = CrawlDataDAOImpl.getInstance();
    }

    public void Rank() {

        double[][] arrayDummy = new double[graph.findAll().size()][graph.findAll().size()];
        matrix = new Matrix(arrayDummy);
        int row = 0;
        int col = 0;

        List<CrawlDataEntity> currentVertices = graph.findAll();

        for(CrawlDataEntity v1 : currentVertices) {
            for(CrawlDataEntity v2 : currentVertices) {
                if(v1.getChildUrl().contains(v2.getUrl())) {
                    matrix.set(row, col, 1);
                }
                else {
                    matrix.set(row, col, 0);
                }
                col++;
            }
            row++;
            col = 0;
        }

        System.out.println("--------------");

        for(int i=0; i<matrix.getRowDimension(); i++) {
            Matrix row1 = matrix.getMatrix(i, i, 0, matrix.getColumnDimension() - 1);

            if(row1.norm2() < 1) {
                row1.timesEquals(1/matrix.getRowDimension());
            }
            else {
                int count = 0;
                for(int k=0; k<matrix.getColumnDimension(); k++) {
                    if(row1.get(0, k) == 1) {
                        count++;
                    }
                }
                row1.timesEquals(1/count);
            }

            matrix.setMatrix(i, i, 0, matrix.getColumnDimension() - 1, row1);
        }

        matrix.timesEquals(1-a);
        matrix.plusEquals(createAdditionMatrix(matrix.getColumnDimension()));

        System.out.println("--------------");

        for(int i=0; i<matrix.getRowDimension(); i++) {
            Matrix row2 = matrix.getMatrix(i, i, 0, matrix.getColumnDimension() - 1);
            for(int k=0; k<10; k++) {
                if(k == 0) {
                    row2 = matrix.times(row2.transpose());
                }
                else {
                    row2 = matrix.times(row2);
                }
            }

            pageRanks.put(i, row2.getRowPackedCopy());
        }
    }

    public Matrix createAdditionMatrix(int dimension) {
        double[][] dummyArray = new double[dimension][dimension];
        Matrix addMatrix = new Matrix(dummyArray);
        for(int i=0; i<dimension; i++) {
            for(int k=0; k<dimension; k++) {
                addMatrix.set(i, k, a/dimension);
            }
        }
        return addMatrix;
    }

    public double calculateTotal() {
        double total = 0;
        for(int i=0; i<matrix.getRowDimension(); i++) {
            for(int k=0; k<matrix.getColumnDimension(); k++) {
                total += matrix.get(i, k);
            }
        }
        return total;
    }

    public void setResult() {
        for(int i = 0; i < pageRanks.size(); i++) {
            double total = 0;
            double[] score = pageRanks.get(i);

            print("Row: " + i + " Score: " + Arrays.toString(score));

            for(int j = 0; j < score.length; j++) {
                total += score[j];
            }

            graph.findOneById(i).setScore((float) total);
            print("Total: " + total);
            graph.update(graph.findOneById(i));
        }

        System.out.println(graph.findOneById(0).getUrl());
        System.out.println(graph.findOneById(0).getScore());

    }

    public void print(String value) {
        System.out.println(value);
    }

    public static void main(String[] args) {
        PageRank p = new PageRank();
        p.Rank();
        p.setResult();

        System.out.println("--------------");

    }
}