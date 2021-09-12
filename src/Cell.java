/**
 * @project - Maze
 * @course - CS351
 * @group - 2
 */
package maze;

import java.util.ArrayList;

/**
 * Cell - the most basic unit of our maze grid
 */
public class Cell {
    private final int row;
    private final int col;
    private final int size;
    private Cell[][] parentGrid;
    private boolean visited;
    private boolean seen;
    private boolean[] walls;
    private Wall[] wallBound;
    private ArrayList<Cell> set;

    /**
     * Cell - constructor to construct our cell
     * @param row - row position
     * @param col - col position
     * @param size - cell size
     * @param parentGrid - grid where the will be
     */
    public Cell(int row, int col, int size, Cell[][] parentGrid){
        this.row = row;
        this.col = col;
        this.size = size;
        this.parentGrid = parentGrid;
        this.visited = false;
        this.walls = new boolean[4];
        this.set = new ArrayList<>();
        set.add(this);
        this.seen = false;
        setWalls();
        this.set.add(this);
    }

    private void setWalls(){
        /* top, right, bottom, left */
        walls[0] = true;
        walls[1] = true;
        walls[2] = true;
        walls[3] = true;
    }

    /**
     * getters and setters
     */
    public boolean isVisited(){
        return this.visited;
    }

    public int getRow(){
        return this.row;
    }

    public int getCol(){
        return this.col;
    }

    public boolean[] getWalls(){
        return this.walls;
    }

    public int getSize(){
        return this.size;
    }

    public void setVisited(){
        this.visited = true;
    }


    public ArrayList<Cell> getSet(){
        return this.set;
    }

    public void setSet(ArrayList<Cell> set){
        this.set = set;
    }

    public void setupWallBounds(){
        this.wallBound = new Wall[4];
    }

    public Wall[] getWallBound() {
        return this.wallBound;
    }

    public boolean isSeen() {
        return this.seen;
    }

    public void setSeen(){
        this.seen = true;
    }

}
