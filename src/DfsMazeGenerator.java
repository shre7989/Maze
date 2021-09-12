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
import java.util.Random;
import java.util.Stack;

/**
 * DfsMazeGenerator - Uses depth first search maze generating algorithm to generate a maze
 */
public class DfsMazeGenerator implements MazeGeneratorAlgorithm{
    private Maze maze;
    private Timeline animation;

    /**
     * DfsMazeGenerator - constructor to setup our generation algorithm
     * maze - maze to be generated
     * animation - timeline to record events for creating animation
     */
    public DfsMazeGenerator(Maze maze, Timeline animation){
        this.maze = maze;
        this.animation = animation;
        this.setup(maze);
        this.generateMaze(maze,animation);
    }

    /**
     * setup - sets up the initial grid where our maze will be generated
     * @param maze - maze to be generated
     */
    @Override
    public void setup(Maze maze) {
        int rows = maze.getRowNum();
        int cols = maze.getColNum();
        int cellSize = maze.getSize()/ rows;
        int mazeSize = maze.getSize();
        Cell[][] grid = maze.getGrid();

        GraphicsContext gc = maze.getGraphicsContext2D();
        gc.setFill(Color.GRAY);
        gc.setStroke(Color.BLACK);

        gc.fillRect(0,0,mazeSize, mazeSize);
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                grid[i][j] = new Cell(i,j,cellSize,grid);
                grid[i][j].setupWallBounds();
                gc.strokeRect(j * cellSize, i * cellSize, j * cellSize + cellSize,i * cellSize + cellSize);
            }
        }
        initWalls(maze);
    }

    /**
     * generateMaze - generates maze using DFS algorithm
     * @param maze - maze to be generated
     * @param animation - timeline to record events for our animation
     */
    @Override
    public void generateMaze(Maze maze, Timeline animation) {

        int count = 0;
        ArrayList<Cell> neighbours = new ArrayList<>();
        Cell[][] grid = maze.getGrid();
        Random random = new Random();
        Cell start = grid[random.nextInt(maze.getRowNum())][random.nextInt(maze.getColNum())];
        Stack<Cell> stack = new Stack<>();

        /* Our starting cell */
        start.setVisited();
        stack.push(start);

        System.out.println("Hello");
        /* backtrack if reached a dead end */

        while(!stack.isEmpty()) {
            Cell current = start;
            Cell randomNeighbour;

            if(!neighbours.isEmpty()) neighbours.clear();
            neighbours = checkNeighbours(current,grid);
            if(!neighbours.isEmpty()) {

                random = new Random();
                if(neighbours.size() == 1) randomNeighbour = neighbours.get(0);
                else randomNeighbour = neighbours.get(random.nextInt(neighbours.size()));

                removeWall(current, randomNeighbour);
                randomNeighbour.setVisited();
                stack.push(randomNeighbour);

                animation.getKeyFrames().add(new KeyFrame(Duration.millis(50 + count), event -> {
                    paintCell(current,maze);
                    paintCell(randomNeighbour, maze);
                }));
            }
            else stack.pop();
            if(!stack.isEmpty()) start = stack.lastElement();
            count++;

        }
    }

    /**
     * checkNeighbours - checks neighbouring cells and finds unvisited neighbours
     * @param cell - the cell whose neighbours we want to find
     * @param grid - maze grid to locate neighbours of the cell
     * @return - an array list of available neighbours
     */
    public ArrayList<Cell> checkNeighbours(Cell cell, Cell[][] grid){
        /* top, right, bottom, left */
        ArrayList<Cell> neighbours = new ArrayList<>();
        int row = cell.getRow();
        int col = cell.getCol();

        /* try-catch to deal with edge cases */
        try{
            /* top */
            Cell top = grid[row - 1][col];
            if(!top.isVisited()) neighbours.add(top);
        }catch(ArrayIndexOutOfBoundsException ignored){}

        try{
            /* right */
            Cell right = grid[row][col + 1];
            if(!right.isVisited()) neighbours.add(right);
        }catch(ArrayIndexOutOfBoundsException ignored){}

        try{
            /* bottom */
            Cell bottom = grid[row + 1][col];
            if(!bottom.isVisited())neighbours.add(bottom);
        }catch(ArrayIndexOutOfBoundsException ignored){}

        try{
            /* left */
            Cell left = grid[row][col - 1];
            if(!left.isVisited())neighbours.add(left);
        }catch(ArrayIndexOutOfBoundsException ignored){}

        return neighbours;
    }

    /**
     * removeWall - remove walls joining the neighbouring cell
     * @param cell - current cell
     * @param neighbour - the neighbouring cell
     */
    public void removeWall(Cell cell, Cell neighbour){
        boolean[] cellWall = cell.getWalls();
        Wall[] cellWalls = cell.getWallBound();
        boolean[] neighbourWall = neighbour.getWalls();
        Wall[] neighbourWalls = neighbour.getWallBound();
        char position = 'n';

        if(neighbour.getRow() == (cell.getRow() - 1)) position = 't';
        if(neighbour.getCol() == (cell.getCol() + 1)) position = 'r';
        if(neighbour.getRow() == (cell.getRow() + 1)) position = 'b';
        if(neighbour.getCol() == (cell.getCol() - 1)) position = 'l';

        if(position == 't'){
            cellWall[0] = false;
            cellWalls[0].makePassage();
            neighbourWall[2] = false;
            neighbourWalls[2].makePassage();
        }
        else if(position == 'r'){
            cellWall[1] = false;
            cellWalls[1].makePassage();
            neighbourWall[3] = false;
            neighbourWalls[3].makePassage();
        }
        else if(position == 'b'){
            cellWall[2] = false;
            cellWalls[2].makePassage();
            neighbourWall[0] = false;
            neighbourWalls[0].makePassage();
        }
        else if(position == 'l'){
            cellWall[3] = false;
            cellWalls[3].makePassage();
            neighbourWall[1] = false;
            neighbourWalls[1].makePassage();
        }

    }

    /**
     * paintCell - paints the cell
     * @param cell - cell to be painted
     * @param maze - maze where the cell is
     */
    public void paintCell(Cell cell, Maze maze){
        int row = cell.getRow() * cell.getSize();
        int col = cell.getCol() * cell.getSize();
        int cellSize = cell.getSize();
        boolean[] walls = cell.getWalls();

        GraphicsContext paint = maze.getGraphicsContext2D();
        paint.setFill(Color.BLACK);
        paint.setStroke(Color.WHITE);
        paint.setLineWidth(2);
        paint.fillRoundRect(col, row,cellSize, cellSize,2,2);

        /* paint walls, if cell has walls */
        if(walls[0]) paint.strokeLine(col,row,col + cellSize, row);
        if(walls[1]) paint.strokeLine(col + cellSize, row, col + cellSize, row + cellSize);
        if(walls[2]) paint.strokeLine(col + cellSize, row + cellSize,col, row + cellSize);
        if(walls[3]) paint.strokeLine(col, row + cellSize, col, row);

    }

    /**
     * initWalls - helps to set up walls for cell
     * @param maze - maze where the cells are at
     */
    public void initWalls(Maze maze){
        int row = maze.getRowNum();
        int col = maze.getColNum();
        Cell[][] grid = maze.getGrid();
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                createWallBounds(grid[i][j],maze);
            }
        }
    }

    /**
     * createWallBounds  - creates wall bounds for cells
     * @param cell - cell whose walls will be built
     * @param maze - maze where the cells are at
     */
    public void createWallBounds(Cell cell, Maze maze){
        Cell[][] grid = maze.getGrid();
        int row = cell.getRow();
        int col = cell.getCol();
        Wall[] walls = cell.getWallBound();
        /* top */
        try{
            Cell up = grid[row - 1][col];
            walls[0] = new Wall(up,cell,'v');
        }catch(ArrayIndexOutOfBoundsException a){
            walls[0] = new Wall(null,cell,'v');
        }

        /* right */
        try{
            Cell right = grid[row][col + 1];
            walls[1] = new Wall(cell,right,'h');
        }catch(ArrayIndexOutOfBoundsException a){
            walls[1] = new Wall(cell, null,'h');
        }

        /* bottom */
        try{
            Cell down = grid[row + 1][col];
            walls[2] = new Wall(cell,down,'v');
        }catch(ArrayIndexOutOfBoundsException a){
            walls[2] = new Wall(cell,null,'v');
        }

        /* left */
        try{
            Cell left = grid[row][col - 1];
            walls[3] = new Wall(left,cell,'h');
        }catch(ArrayIndexOutOfBoundsException a){
            walls[3] = new Wall(null,cell,'h');
        }
    }

    /**
     * getAnimation - returns animation
     * @return - returns the timeline
     */
    public Timeline getAnimation(){
        return this.animation;
    }
}
