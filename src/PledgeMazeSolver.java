/**
 * @project - Maze
 * @course - CS351
 * @group - 2
 */
package maze;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * PledgeMazeSolver - utilizes multi-threading and pledge algorithm to solve the maze
 */
public class PledgeMazeSolver implements MazeSolverAlgorithm{
    private Maze maze;
    private Timeline animation;
    private ExecutorService executorService;
    private boolean madeContact;
    private List<Future<ArrayList<Object>>> futureList;

    /**
     * PledgeMazeSolver - constructor that constructs our solver
     * @param maze - maze to be solved
     * @param animation - Timeline to generate solving animation
     */
    public PledgeMazeSolver(Maze maze, Timeline animation){
        this.maze = maze;
        this.animation = animation;
        this.executorService = Executors.newFixedThreadPool(2);
        this.madeContact = false;
        solveMaze(maze);
    }

    /**
     * solveMaze - solves the maze
     * @param maze - maze to be solved
     */
    @Override
    public void solveMaze(Maze maze) {
        Cell start = maze.getGrid()[0][0];
        Cell end = maze.getGrid()[maze.getRowNum() - 1][maze.getColNum() - 1];

        /* wall followers at the start and the end of the maze */
        PledgeRobot startRobot = new PledgeRobot(start,randomDirection('r','b'),'r',this,maze);
        PledgeRobot endRobot = new PledgeRobot(end,randomDirection('l','t'),'r',this,maze);

        ArrayList<PledgeRobot> robots = new ArrayList<>();
        robots.add(startRobot);
        robots.add(endRobot);

        try {
            futureList = executorService.invokeAll(robots);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Future<ArrayList<Object>> f1 = futureList.get(0);
        Future<ArrayList<Object>> f2 = futureList.get(1);

        ArrayList<Object> data1 = null;
        ArrayList<Object> data2 = null;

        try {
            data1 = f1.get();
            data2 = f2.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ArrayList<Cell> path1 = (ArrayList<Cell>) data1.get(1);
        ArrayList<Cell> path2 = (ArrayList<Cell>) data2.get(1);

        printPath((ArrayList<Cell>) data1.get(1));
        printPath((ArrayList<Cell>) data2.get(1));

        Cell crossPoint;
        if((Boolean) data1.get(2)) crossPoint = (Cell) data1.get(0);
        else crossPoint = (Cell) data2.get(0);

        path1 = removeUnwantedMoves(path1,crossPoint);
        path2 = removeUnwantedMoves(path2,crossPoint);

        createSolvingAnimation(path1,animation,maze,1);
        createSolvingAnimation(path2,animation,maze,2);

    }

    /**
     * randomDirection - randomly chooses between two directions
     * @param one - direction one
     * @param two - direction one
     * @return - randomly choosen direction
     */
    public char randomDirection(char one, char two){
        Random random = new Random();
        int rand = random.nextInt(2);
        if(rand == 0) return one;
        else return two;
    }

    /**
     * removeUnwantedMoves - removes unwanted moves before the meeting point
     * @param path - path of the follower
     * @param cap - meeting point
     * @return - arraylist of cell
     */
    public ArrayList<Cell> removeUnwantedMoves(ArrayList<Cell> path, Cell cap){
        int size = path.size();
        ArrayList<Cell> meetPath = new ArrayList<>();
        for(int i = 0; i < size; i++){
            Cell current = path.get(i);
            meetPath.add(current);
            if(current == cap) return meetPath;
        }
        return meetPath;
    }

    /**
     * createSolvingAnimation - creates solving animation
     * @param solvedPath - solved path
     * @param animation - animation
     * @param maze - our maze
     * @param followerNo - follower id
     */
    private void createSolvingAnimation(ArrayList<Cell> solvedPath, Timeline animation, Maze maze, int followerNo){
        GraphicsContext gc = maze.getGraphicsContext2D();

        int count  = 0;
        for(Cell c: solvedPath){
            animation.getKeyFrames().add(new KeyFrame(Duration.millis(50 + count * 5), event -> {
                if(followerNo == 1) gc.setFill(Color.WHITE);
                else gc.setFill(Color.YELLOW);
                paintCell(c, gc);
            }));
            count++;
        }
    }

    private void paintCell(Cell cell, GraphicsContext paint) {
        int cellSize = cell.getSize();
        double row = cell.getRow() * cell.getSize() + 0.2 * cellSize;
        double col = cell.getCol() * cell.getSize() + 0.2 * cellSize;
        double cellSizes = cell.getSize() * 0.5;

        paint.fillRect(col, row, cellSizes, cellSizes);
    }

    /**
     * getters and setters
     */
    public boolean hasMadeContact(){
        return this.madeContact;
    }

    public synchronized void setMadeContact(){
        this.madeContact = true;
    }

    private void printPath(ArrayList<Cell> path){
        for(Cell c: path){
            System.out.print(c.getRow() + ":" + c.getCol() + "-->");
        }
        System.out.println();
    }
}
