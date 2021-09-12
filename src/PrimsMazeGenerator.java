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
 * PrimsMazeGenerator - Utilizes Prim's algorithm to generate maze
 */
public class PrimsMazeGenerator implements MazeGeneratorAlgorithm{
    private Maze maze;
    private Timeline animation;
    private ArrayList<Wall> wallList;

    /**
     * PrimsMazeGenerator - constructor
     * @param maze - maze to be generated
     * @param animation - timeline to create animation
     */
    public PrimsMazeGenerator(Maze maze, Timeline animation){
        this.maze = maze;
        this.animation = animation;
        this.setup(maze);
        this.wallList = new ArrayList<>();
        this.generateMaze(maze,animation);
    }

    /**
     * setup - sets up the initial grid for the maze
     * @param maze - maze to be setup
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
        gc.fillRect(0,0,mazeSize,mazeSize);

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
     * generateMaze - generates maze using Prim's algorithm
     * @param maze - maze to be generated
     * @param animation - timeline for generating our animation
     */
    @Override
    public void generateMaze(Maze maze, Timeline animation) {
        Random random = new Random();
        Cell[][] grid = maze.getGrid();
        int count = 0;
        Stack<Cell> animate = new Stack<>();
        Cell randomCell = grid[random.nextInt(maze.getRowNum())][random.nextInt(maze.getColNum())];
        randomCell.setVisited();

        Wall[] randomCellWalls = randomCell.getWallBound();
        wallList.add(randomCellWalls[0]);
        wallList.add(randomCellWalls[1]);
        wallList.add(randomCellWalls[2]);
        wallList.add(randomCellWalls[3]);

        while(wallList.size() > 0){

            int length = wallList.size();
            Wall randomWall = wallList.get(random.nextInt(length));

            if( !randomWall.hasSingleCell() && !checkPassage(randomWall.getCell1(), randomWall.getCell2())){
                Cell pop;
                if(!animate.isEmpty()) pop = animate.pop();
                else pop = null;
                addWalls(randomWall,wallList);
                Cell unvisited = getUnvisitedCell(randomWall);
                unvisited.setVisited();
                animate.push(unvisited);
                makePassage(randomWall);
                animation.getKeyFrames().add(new KeyFrame(Duration.millis(50 + count * 0.5), event -> {
                    if(pop != null) paintCell(pop,maze,true,true);
                    paintCell(randomWall.getCell1(),maze,false,false);
                    paintCell(randomWall.getCell2(), maze,false,false);
                    paintCell(unvisited, maze,true,false);
                }));
            }
            wallList.remove(randomWall);
            count++;
        }

    }

    /**
     * initWalls - sets up walls for our maze
     * @param maze - maze whose cells are going to be set with walls
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
     * checkPassage - checks for passage between to cells
     * @param cell1 - cell no. 1
     * @param cell2 - cell no. 2
     * @return - true if passage and false otherwise
     */
    public boolean checkPassage(Cell cell1, Cell cell2){
        if(cell1.isVisited() && cell2.isVisited()) return true;
        else return false;
    }

    /**
     * makePassage - makes the dividing wall a passage
     * @param wall - wall to be removed
     */
    public void makePassage(Wall wall){
        Cell cell1 = wall.getCell1();
        Cell cell2 = wall.getCell2();

        if(wall.getType() == 'h'){
            cell1.getWallBound()[1].makePassage();
            cell1.getWalls()[1] = false;
            cell2.getWallBound()[3].makePassage();
            cell2.getWalls()[3] = false;
        }
        else{
            cell1.getWallBound()[2].makePassage();
            cell1.getWalls()[2] = false;
            cell2.getWallBound()[0].makePassage();
            cell2.getWalls()[0] = false;
        }
    }

    /**
     * getUnvisitedCell - gets the unvisited cell out of the 2 cells connected by the wall
     * @param wall - wall that divides the cell
     * @return - the univisted cell
     */
    public Cell getUnvisitedCell(Wall wall){
        Cell cell1 = wall.getCell1();
        Cell cell2 = wall.getCell2();

        if(!cell1.isVisited()) return cell1;
        else return cell2;
    }

    /**
     * paintCell - paints the cell
     * @param cell - cell to be painted
     * @param maze - maze where the cell is
     * @param tracker - leading cell
     * @param reset - recolors the leading cell
     */
    public void paintCell(Cell cell, Maze maze, boolean tracker, boolean reset) {
        int cellSize = cell.getSize();
        int row = cell.getRow() * cellSize;
        int col = cell.getCol() * cellSize;

        if (tracker) {
            GraphicsContext gc = maze.getGraphicsContext2D();
            if(reset){
                gc.setFill(Color.BLACK);
                gc.setLineWidth(2);
                gc.setStroke(Color.RED);
                gc.fillOval(col + cellSize * 0.1, row + cellSize * 0.1, 0.7 * cellSize , 0.7 * cellSize);
            }
            else {
                gc.setStroke(Color.BLACK);
                gc.setFill(Color.LIGHTGREEN);
                gc.fillOval(col + cellSize * 0.2, row + cellSize * 0.2, 0.5 * cellSize , 0.5 * cellSize);
            }
        } else {
            Wall[] wallBounds = cell.getWallBound();

            GraphicsContext gc = maze.getGraphicsContext2D();
            gc.setStroke(Color.WHITESMOKE);
            gc.setLineWidth(2);
            gc.setFill(Color.BLACK);
            gc.fillRoundRect(col, row, cellSize, cellSize, 3, 3);

            if (!wallBounds[0].isPassage()) gc.strokeLine(col, row, col + cellSize, row);
            if (!wallBounds[1].isPassage()) gc.strokeLine(col + cellSize, row, col + cellSize, row + cellSize);
            if (!wallBounds[2].isPassage()) gc.strokeLine(col + cellSize, row + cellSize, col, row + cellSize);
            if (!wallBounds[3].isPassage()) gc.strokeLine(col, row + cellSize, col, row);


        }
    }

    /**
     * addWalls - adds walls which has unvisited cells
     * @param wall - wall to be added
     * @param wallList - list of walls to add to
     */
    public void addWalls(Wall wall, ArrayList<Wall> wallList){
        Cell unvisitedCell = getUnvisitedCell(wall);
        if(wall.getType() == 'h'){
            if(wall.getCell1() == unvisitedCell){
                wallList.add(unvisitedCell.getWallBound()[0]);
                wallList.add(unvisitedCell.getWallBound()[2]);
                wallList.add(unvisitedCell.getWallBound()[3]);
            }
            else{
                wallList.add(unvisitedCell.getWallBound()[0]);
                wallList.add(unvisitedCell.getWallBound()[1]);
                wallList.add(unvisitedCell.getWallBound()[2]);
            }
        }
        else{
            if(wall.getCell1() == unvisitedCell){
                wallList.add(unvisitedCell.getWallBound()[0]);
                wallList.add(unvisitedCell.getWallBound()[1]);
                wallList.add(unvisitedCell.getWallBound()[3]);
            }
            else{
                wallList.add(unvisitedCell.getWallBound()[1]);
                wallList.add(unvisitedCell.getWallBound()[2]);
                wallList.add(unvisitedCell.getWallBound()[3]);
            }
        }
    }

    /**
     * getAnimation - gets the animation
     * @return - Timeline that generates our animation
     */
    @Override
    public Timeline getAnimation() {
        return null;
    }

}
