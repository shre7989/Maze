/**
 * @project - Maze
 * @course - CS351
 * @group - 2
 */
package maze;

import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Maze - Our maze which will be generated using generation and solving algorithms
 */
public class Maze extends Canvas {
    private int rowNum;
    private int colNum;
    private int size;
    private boolean solved;
    private Cell[][] grid;
    private Timeline animationG;
    private Timeline animationS;
    private MazeGeneratorAlgorithm generator;
    private MazeSolverAlgorithm solver;

    /**
     * Maze - constructor that constructs our maze
     * @param rowNum - total no. of rows
     * @param colNum - total no. of columns
     * @param size - size of the maze
     */
    public Maze(int rowNum, int colNum, int size, ArrayList<String> config){
        super(size,size);
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.size = size;
        this.solved = false;
        this.grid = new Cell[rowNum][colNum];
        this.animationG = new Timeline();
        this.animationS = new Timeline();
        setGenAndSol(config);
    }

    public void setGenAndSol(ArrayList<String> data){
        String gen = data.get(0);
        String sol = data.get(1);

        if(gen.equals("dfs")) generator = new DfsMazeGenerator(this,this.animationG);
        else if(gen.equals("kruskal")) generator = new KruskalMazeGenerator(this,this.animationG);
        else generator = new PrimsMazeGenerator(this,this.animationG);

        if(sol.equals("mouse") || sol.equals("mouse_thread")) solver = new RandomMouseMazeSolver(this,this.animationS);
        if(sol.equals("wall") || sol.equals("wall_thread")) solver = new WallFollowerMazeSolver(this,animationS);
        else solver = new PledgeMazeSolver(this,this.animationS);

    }
    /**
     * getters for our instance variables
     */
    public Timeline getAnimationG() {
        return this.animationG;
    }

    public int getRowNum(){
        return this.rowNum;
    }

    public int getColNum(){
        return this.colNum;
    }

    public Cell[][] getGrid() {
        return this.grid;
    }

    public int getSize(){
        return this.size;
    }

    public boolean isSolved() {
        return this.solved;
    }

    public void setSolved(){
        this.solved = true;
    }

    public Timeline getAnimationS() {
        return this.animationS;
    }
}
