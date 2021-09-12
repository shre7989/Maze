/**
 * @project - Maze
 * @course - CS351
 * @group - 2
 */
package maze;

/**
 * Wall - wall of a cell
 */
public class Wall {
    private Cell cell1;
    private Cell cell2;
    private char type;
    private boolean isPassage = false;

    /**
     * Wall - constructor that constructs the wall
     * @param cell1 - first cell divided by this wall
     * @param cell2 - second cell divided by this wall
     * @param type - type of wall (horizontal or vertical)
     */
    public Wall(Cell cell1, Cell cell2, char type){
        this.cell1 = cell1;
        this.cell2 = cell2;
        this.type = type;
    }

    /**
     * makePassage - makes the wall a passage
     */
    public void makePassage(){
        this.isPassage = true;
    }

    /**
     * getters and setters
     */
    public boolean isPassage() {
        return this.isPassage;
    }

    public boolean isSameSet(){
        if(cell1.getSet() == cell2.getSet()) return true;
        else return false;
    }

    public boolean hasSingleCell(){
        if(this.cell1 == null || this.cell2 == null) return true;
        return false;
    }

    public boolean hasSameSet() {
        if (this.cell1.getSet() == this.cell2.getSet()) return true;
        else return false;
    }

    public Cell getCell2(){
        return this.cell2;
    }

    public Cell getCell1(){
        return this.cell1;
    }

    public char getType() {
        return this.type;
    }
}
