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

/**
 * KruskalMazeGenerator - Utilizes Kruskal's algorithm to generate a maze
 */
public class KruskalMazeGenerator implements MazeGeneratorAlgorithm{
    private Maze maze;
    private Timeline animation;
    private ArrayList<Wall> wallList;

    /**
     * KruskalMazeGenerator - constructs to setup our generation algorithm
     * @param maze - maze to be generated
     * @param animation - timeline to record events for creating animation
     */
    public KruskalMazeGenerator(Maze maze, Timeline animation){
        this.maze = maze;
        this.animation = animation;
        this.wallList = new ArrayList<>();
        setup(maze);
        this.generateMaze(maze,animation);
    }

    /**
     * setup - sets up the initial grid for generating our maze
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
        System.out.println("The size of list is: " + wallList.size());
    }

    /**
     * generateMaze - generates maze using Kruskal's algorithm
     * @param maze - maze to be generated
     * @param animation - timeline to generate animation
     */
    @Override
    public void generateMaze(Maze maze, Timeline animation) {
        Random random = new Random();
        int size;
        int count = 0;
        while(wallList.size() > 1){
            size = wallList.size();
            Wall wall = wallList.get(random.nextInt(size));

            if((!wall.hasSameSet())) {
                makeSameSet(wall.getCell1(), wall.getCell2());
                removeDividingWalls(wall);

                animation.getKeyFrames().add(new KeyFrame(Duration.millis(50 + count), event -> {
                    Cell cell1 = wall.getCell1();
                    Cell cell2 = wall.getCell2();

                    if (cell1 != null) paintCell(cell1, maze);
                    if (cell2 != null) paintCell(cell2, maze);
                }));
            }
            wallList.remove(wall);
            System.out.println("The size of wall is: " + wallList.size());
            count++;
        }

        System.out.println("Hello world!!!");
    }

    /**
     * getAnimation - returns timeline which generates our maze
     * @return - return the animation
     */
    @Override
    public Timeline getAnimation() {
        return this.animation;
    }

    /**
     * paintCell - paints our cell
     * @param cell - cell to be painted
     * @param maze - maze where the cell is located
     */
    public void paintCell(Cell cell, Maze maze){
        int row = cell.getRow() * cell.getSize();
        int col = cell.getCol() * cell.getSize();
        int cellSize = cell.getSize();
        //Wall[] walls = cell.getWallBound();
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
     * initWalls - sets up walls for our maze
     * @param maze - maze whose cells are going to be set with walls
     */
    public void initWalls(Maze maze){
        int rows = maze.getRowNum();
        int cols = maze.getColNum();
        Cell[][] grid = maze.getGrid();

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                configureWalls(grid[i][j],maze);
            }
        }
    }

    /**
     * removeDividingWalls - removes dividing wall if the cells are of different set
     * @param wall - wall to be removed
     */
    public void removeDividingWalls(Wall wall){
        Cell cell1 = wall.getCell1();
        Cell cell2 = wall.getCell2();

        System.out.println("Cell 1-> Row: " + cell1.getRow() + " Col: " + cell1.getCol());
        System.out.println("Cell 2-> Row: " + cell2.getRow() + " Col: " + cell2.getCol());

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

        System.out.println("\n");
    }

    /**
     * configureWalls - helps to set up the walls properly following the maze logic
     * @param cell - whose walls we are going to configure
     * @param maze - maze where the cell is located
     */
    public void configureWalls(Cell cell, Maze maze){

        Cell[][] grid = maze.getGrid();
        int row = cell.getRow();
        int col = cell.getCol();
        Wall[] walls = cell.getWallBound();
        /* top */
        try{
            Cell up = grid[row - 1][col];
            walls[0] = new Wall(up,cell,'v');
            wallList.add(walls[0]);
        }catch(ArrayIndexOutOfBoundsException a){
            walls[0] = new Wall(null,cell,'v');
        }

        /* right */
        try{
            Cell right = grid[row][col + 1];
            walls[1] = new Wall(cell,right,'h');
            wallList.add(walls[1]);
        }catch(ArrayIndexOutOfBoundsException a){
            walls[1] = new Wall(cell, null,'h');
        }

        /* bottom */
        try{
            Cell down = grid[row + 1][col];
            walls[2] = new Wall(cell,down,'v');
            wallList.add(walls[2]);
        }catch(ArrayIndexOutOfBoundsException a){
            walls[2] = new Wall(cell,null,'v');
        }

        /* left */
        try{
            Cell left = grid[row][col - 1];
            walls[3] = new Wall(left,cell,'h');
            wallList.add(walls[3]);
        }catch(ArrayIndexOutOfBoundsException a){
            walls[3] = new Wall(null,cell,'h');
        }
    }

    /**
     * makeSameSet - joins the set of the cells
     * @param cell1 - cell no. 1
     * @param cell2 - cell no. 2
     */
    public void makeSameSet(Cell cell1, Cell cell2){
        cell1.getSet().addAll(cell2.getSet());
        for(Cell c: cell2.getSet()){
            c.setSet(cell1.getSet());
        }
    }

}

