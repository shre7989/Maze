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
import java.util.concurrent.*;

/**
 * RandomMouseMazeSolver - utilizes multi-threading to generate mice that traverses the maze and solves it
 */
public class RandomMouseMazeSolver implements MazeSolverAlgorithm{
    private boolean init = true;
    private final ExecutorService executorService;
    private ArrayList<Cell> solvedPath;
    private ArrayList<Cell> randomMouses;
    private List<Future<ArrayList<Object>>> futureTasks;

    /**
     * RandomMouseMazeSolver - Constructor which constructs our solver
     * @param maze - maze to be solved
     * @param animation - Timeline to generate solving animation
     */
    public RandomMouseMazeSolver(Maze maze, Timeline animation){
        this.executorService = Executors.newFixedThreadPool(7);
        this.futureTasks = new ArrayList<>();
        this.solvedPath = new ArrayList<>();
        this.randomMouses = new ArrayList<>();
        solveMaze(maze);
        createSolvingAnimation(solvedPath, animation,maze);
    }

    /**
     * solveMaze - solves the maze
     * @param maze - maze to be solved
     */
    @Override
    public void solveMaze(Maze maze) {
        ArrayList<Mouse> mice = new ArrayList<>();
        Cell start = maze.getGrid()[0][0];

       while(!maze.isSolved()){
            if(init){
                mice.addAll(spawnThreads(start,new ArrayList<>(),'i',maze));
                System.out.println("This is the size of threads in mice: " + mice.size());
                this.init = false;

                  //run all the spawned threads
                try {
                    futureTasks = executorService.invokeAll(mice);
                } catch (InterruptedException e) {e.printStackTrace();}
                mice.clear();
                System.out.println("finally");
            }
            else{
                System.out.println("Baby beggin , beggin youuuuuu");
                ArrayList<Object> data = null;
                for(Future<ArrayList<Object>> f: futureTasks){
                    try {
                        data = f.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    if(data != null) {
                        mice.addAll(spawnThreads((Cell) data.get(0), (ArrayList<Cell>) data.get(1), (char) data.get(2), maze));
                    }
                }

                try {
                    futureTasks = executorService.invokeAll(mice);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                checkIfSolved(futureTasks,maze);
                mice.clear();
            }
        }
       executorService.shutdown();

    }

    /**
     * availablePaths - checks for available paths from the current cell
     * @param current - current cell
     * @param direction - current direction
     * @return - a boolean array representing the paths from the cell
     */
    private boolean[] availablePaths(Cell current, char direction){
        Wall[] walls = current.getWallBound();
        boolean[] paths = new boolean[4];

        if(init){
            /* starting cell does not have top and left passages open */
            paths[0] = false;
            paths[3] = false;
            paths[1] = walls[1].isPassage();
            paths[2] = walls[2].isPassage();

        }
        else {
            paths[0] = walls[0].isPassage();
            paths[1] = walls[1].isPassage();
            paths[2] = walls[2].isPassage();
            paths[3] = walls[3].isPassage();

            /* cannot go backwards */
            if(direction == 't') paths[2] = false;
            if(direction == 'r') paths[3] = false;
            if(direction == 'b') paths[0] = false;
            if(direction == 'l') paths[1] = false;
        }
        return paths;
    }

    /**
     * spwanThreads - spawns our Mouse runnable where new paths are available
     * @param current - current cell
     * @param path - initial path of parent Mouse
     * @param direction - direction of parent Mouse
     * @param maze - maze where the mouse runs
     * @return - a list of Mouse Callables
     */
    private ArrayList<Mouse> spawnThreads(Cell current, ArrayList<Cell> path, char direction, Maze maze){
        boolean[] paths;
        paths = availablePaths(current, direction);
        ArrayList<Mouse> mice = new ArrayList<>();

        if(direction == 'i'){
            if(paths[1]) {
               mice.add(new Mouse(current,'r',path,this,maze));
            }
            if(paths[2]){
               mice.add(new Mouse(current,'b',path,this,maze));
            }
        }
        else{
            if(paths[0]){
                mice.add(new Mouse(current,'t',path,this,maze));
            }
            if(paths[1]){
                mice.add(new Mouse(current,'r',path,this,maze));
            }
            if(paths[2]){
                mice.add(new Mouse(current,'b',path,this,maze));
            }
            if(paths[3]){
                mice.add(new Mouse(current,'l',path,this,maze));
            }
        }
        return mice;
    }

    /**
     * checkIfSolved - checks if our maze is solved
     * @param futureTasks - list of data returned by our Mouse Callbles
     * @param maze - maze where the mouse runs
     */
    private synchronized void checkIfSolved(List<Future<ArrayList<Object>>> futureTasks, Maze maze){
        ArrayList<Object> data = null;
        Cell last = maze.getGrid()[maze.getRowNum() - 1][maze.getColNum() - 1];
        for(Future<ArrayList<Object>> f: futureTasks){
            try {
                if(f.get() != null) {
                    try {
                        data = f.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    /* check if solved */
                    if ((Cell) data.get(0) == last) {
                        maze.setSolved();
                        solvedPath = (ArrayList<Cell>) data.get(1);
                        return;
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * createRandomMouseAnimation - generates animation for all the randomly generated mouse callables
     * @param randomMouse - list of all the paths taken by all our spawned mouse callables
     * @param animation - Timeline to record and generate the animation
     * @param maze - maze where the visualization occurs
     * @return - count of the frames
     */
    private int createRandomMouseAnimation(ArrayList<Cell> randomMouse,Timeline animation, Maze maze){
        GraphicsContext gc = maze.getGraphicsContext2D();

        Color[] colors = makeArrayOfColors();
        Random random = new Random();

        int count  = 0;
        for(Cell c: randomMouse){
            animation.getKeyFrames().add(new KeyFrame(Duration.millis(50 + count),event -> {
                gc.setFill(colors[random.nextInt(colors.length)]);
                paintCell(c, gc);
            }));
            count++;
        }
        return count;
    }

    /**
     * clearRandomMouse - clears the animation of random Mouse to set up for generating animation for the solved path
     * @param randomMouse - list of all the paths taken by all our spawned mouse callables
     * @param animation - Timeline to record and generate the animation
     * @param maze - maze where the visualization occurs
     * @param count - count of the frames
     * @return
     */
    private int clearRandomMouse(ArrayList<Cell> randomMouse,Timeline animation,Maze maze,int count){
        GraphicsContext gc = maze.getGraphicsContext2D();

        for(Cell c: randomMouse){
            Wall[] walls = c.getWallBound();
            int cellSize = c.getSize();
            int row = c.getRow() * cellSize;
            int col = c.getCol() * cellSize;

            animation.getKeyFrames().add(new KeyFrame(Duration.millis(count),event -> {
                gc.setStroke(Color.WHITESMOKE);
                gc.setLineWidth(2);
                gc.setFill(Color.BLACK);
                gc.fillRoundRect(col, row, cellSize, cellSize, 3, 3);

                if (!walls[0].isPassage()) gc.strokeLine(col, row, col + cellSize, row);
                if (!walls[1].isPassage()) gc.strokeLine(col + cellSize, row, col + cellSize, row + cellSize);
                if (!walls[2].isPassage()) gc.strokeLine(col + cellSize, row + cellSize, col, row + cellSize);
                if (!walls[3].isPassage()) gc.strokeLine(col, row + cellSize, col, row);
            }));
            count++;
        }
        return count;
    }

    /**
     * createSolvingAnimation - generates animation of the final path
     * @param solvedPath - the final path
     * @param animation - Timeline for generating our solved path
     * @param maze - maze where the visualization occurs
     */
    private void createSolvingAnimation(ArrayList<Cell> solvedPath, Timeline animation, Maze maze){
        int count;
        count = createRandomMouseAnimation(randomMouses,animation,maze);
        count = clearRandomMouse(randomMouses,animation,maze,count);

        GraphicsContext gc = maze.getGraphicsContext2D();

        for(Cell c: solvedPath){
            animation.getKeyFrames().add(new KeyFrame(Duration.millis(50 + count),event -> {
                gc.setFill(Color.RED);
                paintCell(c, gc);
            }));
            count++;
        }
    }

    /**
     * addRandomMousePath - adds a cell to the randomMouse list
     * @param cell - cell to be added
     */
    public synchronized void addRandomMousePath(Cell cell){
        this.randomMouses.add(cell);
    }

    /**
     * makeArrayOfColors - makes and array of different colors and returns them
     * @return - array of colors
     */
    private Color[] makeArrayOfColors(){
        Color[] colors = new Color[5];
        colors[0] = Color.rgb(100,0,200); //purple
        colors[1] = Color.YELLOW;
        colors[2] = Color.LIGHTGREEN;
        colors[3] = Color.RED;
        colors[4] = Color.PINK;

        return colors;
    }

    /**
     * paintCell - paints the cell
     * @param cell - cell to be painted
     * @param gc - graphics context to paint of the canvas (maze)
     */
    private void paintCell(Cell cell, GraphicsContext gc) {
        Wall[] wallBounds = cell.getWallBound();

        int cellSize = cell.getSize();
        int row = cell.getRow() * cellSize;
        int col = cell.getCol() * cellSize;

        gc.setStroke(Color.WHITESMOKE);
        gc.setLineWidth(2);
        gc.fillRoundRect(col, row, cellSize, cellSize, 5, 5);

        if (!wallBounds[0].isPassage()) gc.strokeLine(col, row, col + cellSize, row);
        else{
            gc.setStroke(Color.BLACK);
            gc.strokeLine(col, row, col + cellSize, row);
            gc.setStroke(Color.WHITESMOKE);
        }
        if (!wallBounds[1].isPassage()) gc.strokeLine(col + cellSize, row, col + cellSize, row + cellSize);
        else{
            gc.setStroke(Color.BLACK);
            gc.strokeLine(col + cellSize, row, col + cellSize, row + cellSize);
            gc.setStroke(Color.WHITESMOKE);
        }
        if (!wallBounds[2].isPassage()) gc.strokeLine(col + cellSize, row + cellSize, col, row + cellSize);
        else{
            gc.setStroke(Color.BLACK);
            gc.strokeLine(col + cellSize, row + cellSize, col, row + cellSize);
            gc.setStroke(Color.WHITESMOKE);
        }
        if (!wallBounds[3].isPassage()) gc.strokeLine(col, row + cellSize, col, row);
        else{
            gc.setStroke(Color.BLACK);
            gc.strokeLine(col, row + cellSize, col, row);
            gc.setStroke(Color.WHITESMOKE);
        }
    }
}
