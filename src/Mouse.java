/**
 * @project - Maze
 * @course - CS351
 * @group - 2
 */
package maze;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Mouse - a runnable instance that runs through the maze in a given direction until a junction is reached
 */
public class Mouse implements Callable<ArrayList<Object>> {
    private Cell start;
    private Cell current;
    private Maze maze;
    private char direction;
    private ArrayList<Cell> initialPath;
    private ArrayList<Cell> runPath;
    private ArrayList<Object> data;
    private boolean inJunction;
    private boolean inDeadEnd;
    private RandomMouseMazeSolver solver;

    /**
     * Mouse - constructor that constructs our mouse
     * @param start - starting cell for this mouse
     * @param direction - direction to go to
     * @param initialPath - initial path of parent mouse
     * @param solver - solver that uses this mouse
     * @param maze - maze where the mouse runs
     */
    public Mouse(Cell start,char direction, ArrayList<Cell> initialPath, RandomMouseMazeSolver solver,Maze maze){
        this.start = start;
        this.current = start;
        this.maze = maze;
        this.solver = solver;
        this.runPath = new ArrayList<>();
        if(!initialPath.isEmpty()) {
            this.initialPath = initialPath;
            runPath.addAll(this.initialPath);
        }
        this.direction = direction;
        this.inJunction = false;
        this.inDeadEnd = false;
    }


    @Override
    public ArrayList<Object> call() throws Exception {
        this.data = new ArrayList<>();
        startRunning(maze);
        if(!inDeadEnd || maze.isSolved()) {
            data.add(current);
            data.add(runPath);
            data.add(direction);
        }
        else return null;
        return data;
    }

    /**
     * startRunning - moves in the current direction until a junction is reached
     * @param maze
     */
    public void startRunning(Maze maze){
        /* loop until junction */
        while(!maze.isSolved() && !inJunction && !inDeadEnd) {
            move(direction, maze);
            junctionCheck(current, direction, maze);
            if (inDeadEnd) return;
        }
    }

    /**
     * move - helps the mouse to move in the direction configuring the maze logistics associated with it
     * @param direction - direction to move to
     * @param maze - maze where the mouse will move
     */
    private void move(char direction, Maze maze){
        Cell[][] grid = maze.getGrid();
        int row = current.getRow();
        int col = current.getCol();
        Wall[] walls = current.getWallBound();

        if(direction == 't' && walls[0].isPassage()){
            current = grid[row - 1][col];
            synchronized (this){
                current.setSeen();
                solver.addRandomMousePath(current);
                if(current == maze.getGrid()[maze.getRowNum() - 1][maze.getColNum() - 1]) maze.setSolved();
            }
            this.runPath.add(current);
        }
        else if(direction == 'r' && walls[1].isPassage()){
            current = grid[row][col + 1];
            synchronized (this){
                current.setSeen();
                solver.addRandomMousePath(current);
                if(current == maze.getGrid()[maze.getRowNum() - 1][maze.getColNum() - 1]) maze.setSolved();
            }
            this.runPath.add(current);
        }
        else if(direction == 'b' && walls[2].isPassage()){
            current = grid[row + 1][col];
            synchronized (this){
                current.setSeen();
                solver.addRandomMousePath(current);
                if(current == maze.getGrid()[maze.getRowNum() - 1][maze.getColNum() - 1]) maze.setSolved();
            }
            this.runPath.add(current);
        }
        else if(direction == 'l' && walls[3].isPassage()){
            current = grid[row][col - 1];
            synchronized (this){
                current.setSeen();
                solver.addRandomMousePath(current);
                if(current == maze.getGrid()[maze.getRowNum() - 1][maze.getColNum() - 1]) maze.setSolved();
            }
            this.runPath.add(current);
        }

    }

    /**
     * junctionCheck - checks the available path in the junctions
     * @param current - current cell
     * @param direction - current direction
     * @param maze - maze where our mouse is running
     */
    private void junctionCheck(Cell current, char direction, Maze maze){
        Wall[] walls = current.getWallBound();
        Cell[][] grid = maze.getGrid();
        int row = current.getRow();
        int col = current.getCol();
        if(direction == 't' ){
            boolean top = !walls[0].isPassage() || grid[row - 1][col].isSeen();
            boolean right = !walls[1].isPassage() || grid[row][col + 1].isSeen();
            boolean left = !walls[3].isPassage() || grid[row][col - 1].isSeen();

            boolean rightPassage = walls[1].isPassage() && !grid[row][col + 1].isSeen();
            boolean leftPassage = walls[3].isPassage() && !grid[row][col - 1].isSeen();;

            if(rightPassage || leftPassage){
                setInJunction();
            }
            else if(top && right && left){
                setInDeadEnd();
            }
        }
        else if(direction == 'r'){
            boolean top = !walls[0].isPassage() || grid[row - 1][col].isSeen();
            boolean right = !walls[1].isPassage() || grid[row][col + 1].isSeen();
            boolean down = !walls[2].isPassage() || grid[row + 1][col].isSeen();

            boolean topPassage = walls[0].isPassage() && !grid[row - 1][col].isSeen();
            boolean downPassage = walls[2].isPassage() && !grid[row + 1][col].isSeen();;


            if(topPassage || downPassage){
                setInJunction();
            }
            else if(right && top && down){
                setInDeadEnd();
            }
        }
        else if(direction == 'b'){
            boolean bottom = !walls[2].isPassage() || grid[row + 1][col].isSeen();
            boolean right = !walls[1].isPassage() || grid[row][col + 1].isSeen();
            boolean left = !walls[3].isPassage() || grid[row][col - 1].isSeen();

            boolean rightPassage = walls[1].isPassage() && !grid[row][col + 1].isSeen();
            boolean leftPassage = walls[3].isPassage() && !grid[row][col - 1].isSeen();;

           if(rightPassage || leftPassage){
                setInJunction();
           }
           else if(bottom && right && left){
               setInDeadEnd();
           }
        }
        else if(direction == 'l'){
            boolean top = !walls[0].isPassage() || grid[row - 1][col].isSeen();
            boolean bottom = !walls[2].isPassage() || grid[row + 1][col].isSeen();
            boolean left = !walls[3].isPassage() || grid[row][col - 1].isSeen();

            boolean topPassage = walls[0].isPassage() && !grid[row - 1][col].isSeen();
            boolean downPassage = walls[2].isPassage() && !grid[row + 1][col].isSeen();;

            if(topPassage || downPassage){
                setInJunction();
            }
            else if(top && bottom && left){
                setInDeadEnd();
            }
        }
    }


    /**
     * setters and getters
     */

    private void setInDeadEnd(){
        this.inDeadEnd = true;
    }
    private void setInJunction(){
        this.inJunction = true;
    }

}
