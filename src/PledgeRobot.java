/**
 * @project - Maze
 * @course - CS351
 * @group - 2
 */
package maze;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * PledgeRobot - a callable instance that traverses the maze using pledge algorithm
 */
public class PledgeRobot implements Callable<ArrayList<Object>> {
    /* counter to count our turns */
    private int counter;

    private Cell current;
    private char direction;
    private char type;
    private PledgeMazeSolver solver;
    private ArrayList<Cell> path;
    private Maze maze;
    private boolean isSolver;

    /**
     * PledgeRobot - Constructs our pledge robot
     * @param start - start point
     * @param direction - starting direction
     * @param robotType - type of robot (right hand or left hand)
     * @param solver - solver that utilizes this robot
     * @param maze - maze to be solved
     */
    public PledgeRobot(Cell start, char direction, char robotType, PledgeMazeSolver solver, Maze maze){
        this.current = start;
        this.counter = 0;
        this.direction = direction;
        this.type = robotType;
        this.solver = solver;
        this.maze = maze;
        this.path = new ArrayList<>();
        this.isSolver = false;
    }
    @Override
    public ArrayList<Object> call() throws Exception {
        solveUsingRobot(maze);
        ArrayList<Object> data = new ArrayList<>();
        data.add(current);
        data.add(path);
        data.add(isSolver);
        return data;
    }

    /**
     * solveUsingRobot - helps traverse the maze using pledge algorithm
     * @param maze
     */
    private void solveUsingRobot(Maze maze){
        Wall[] walls;
        while(!solver.hasMadeContact()){
            walls = current.getWallBound();
            if(counter == 0) loopUntilJunction(maze);
            if(type == 'l'){
                if(counter == 0) {
                    direction = turn('r');
                    decCounter();
                }
                else if(noLeftWall(walls,direction)){
                    direction = turn('l');
                    moveForward(direction,maze);
                    incCounter();
                }
                else if(noFrontWall(walls,direction)){
                    moveForward(direction,maze);
                }
                else if(!noFrontWall(walls,direction) && !noLeftWall(walls,direction)){
                    direction = turn('r');
                    decCounter();
                }
            }
            else if(type == 'r'){
                if(counter == 0) {
                    direction = turn('l');
                    decCounter();
                }
                else if(noRightWall(walls,direction)){
                    direction = turn('r');
                    moveForward(direction,maze);
                    incCounter();
                }
                else if(noFrontWall(walls,direction)){
                    moveForward(direction,maze);
                }
                else if(!noFrontWall(walls,direction) && !noRightWall(walls,direction)){
                    direction = turn('l');
                    decCounter();
                }
            }
        }
    }

    /**
     * turn - helps to turn in the specified direction
     * @param type - type of turn
     * @return - direction after making the turn
     */
    public char turn(char type){
        if(type == 'l'){
            printCellWalls(current);
            System.out.println("The direction is: " + direction);
            System.out.println("The current cell is: " + current.getRow() + " " + current.getCol());
            System.out.println("Rotating left");
            /* counter-clockwise rotation */
            if(direction == 't') return 'l';
            else if(direction == 'r') return 't';
            else if(direction == 'b') return 'r';
            else if(direction == 'l') return 'b';
        }
        else if(type == 'r'){
            printCellWalls(current);
            synchronized (this) {
                System.out.println(counter);
                System.out.println("The direction is: " + direction);
                System.out.println("The current cell is: " + current.getRow() + " " + current.getCol());
                System.out.println("Rotatting right\n");
            }
            /* clock-wise rotation */
            if(direction == 't') return 'r';
            else if(direction == 'r') return 'b';
            else if(direction == 'b') return 'l';
            else if(direction == 'l') return  't';
        }
        return ' ';
    }

    /**
     * noFrontWall - checks if their is no wall in the current direction
     * @param walls - wall bounds of the cell
     * @param direction - direction of the follower
     * @return - true if passage and false otherwise
     */
    public boolean noFrontWall(Wall[] walls, char direction){
        if(direction == 't') return walls[0].isPassage();
        else if(direction == 'r') return walls[1].isPassage();
        else if(direction == 'b') return walls[2].isPassage();
        else if(direction == 'l') return walls[3].isPassage();
        return true;
    }

    /**
     * noLeftWall - checks if their is no left Wall
     * @param walls - wall bounds of the current cell
     * @param direction - direction of the follower
     * @return - true if has left wall and false otherwise
     */
    public boolean noLeftWall(Wall[] walls, char direction){
        if(direction == 't') return walls[3].isPassage();
        else if(direction == 'r') return walls[0].isPassage();
        else if(direction == 'b') return walls[1].isPassage();
        else if(direction == 'l') return walls[2].isPassage();
        return false;
    }

    /**
     * noRightWall - checks if there is not right wall
     * @param walls - wall bounds of the current cell
     * @param direction - direction of the follower
     * @return - true if has right wall and false otherwise
     */
    public boolean noRightWall(Wall[] walls, char direction){
        if(direction == 't')  return walls[1].isPassage();
        else if(direction == 'r') return walls[2].isPassage();
        else if(direction == 'b') return walls[3].isPassage();
        else if (direction == 'l') return walls[0].isPassage();
        return false;
    }

    /**
     * loopUntilJunction - moves in the current direction until the junction is reached
     * @param maze - maze where the robot moves
     */
    private void loopUntilJunction(Maze maze){
        if(direction == 't'){
            while(current.getWallBound()[0].isPassage()) {
                moveForward(direction,maze);
            }
        }
        else if(direction == 'r'){
            while(current.getWallBound()[1].isPassage()) {
                moveForward(direction,maze);
            }
        }
        else if(direction == 'b'){
            while(current.getWallBound()[2].isPassage()) {
                moveForward(direction,maze);
            }
        }
        else if(direction == 'l'){
            while(current.getWallBound()[3].isPassage()) {
                moveForward(direction,maze);
            }
        }
    }

    /**
     * moveForward - helps the robot to move forward in the given direction
     * @param direction - direction to move towards
     * @param maze - maze where the robot moves
     */
    public synchronized void moveForward(char direction, Maze maze){
        Cell[][] grid = maze.getGrid();
        int row = current.getRow();
        int col = current.getCol();

        if(direction == 't'){
            if(!path.contains(current))path.add(current);
            current = grid[row - 1][col];
            if(current.isSeen() && !path.contains(current)){
                solver.setMadeContact();
                this.isSolver = true;
            }
            else synchronized (this){current.setSeen();}
        }
        else if(direction == 'r'){
            if(!path.contains(current))path.add(current);
            current = grid[row][col + 1];
            if(current.isSeen() && !path.contains(current)){
                solver.setMadeContact();
                this.isSolver = true;
            }
            else synchronized (this){current.setSeen();}
        }
        else if(direction == 'b'){
            if(!path.contains(current))path.add(current);
            current = grid[row + 1][col];
            if(current.isSeen() && !path.contains(current)){
                solver.setMadeContact();
                this.isSolver = true;
            }
            else synchronized (this){current.setSeen();}
        }
        else if(direction == 'l'){
            if(!path.contains(current))path.add(current);
            current = grid[row][col - 1];
            if(current.isSeen() && !path.contains(current)){
                solver.setMadeContact();
                this.isSolver = true;
            }
            else synchronized (this){current.setSeen();}
        }
    }

    /**
     * decCounter - decrement the counter
     */
    private synchronized void decCounter(){
        this.counter--;
    }

    /**
     * incCounter - increments the counter
     */
    private synchronized void incCounter(){
        this.counter++;
    }

    private void printCellWalls(Cell cell){
        Wall[] walls = cell.getWallBound();
        System.out.println("[ " + !walls[0].isPassage() + " " + !walls[1].isPassage() + " " + !walls[2].isPassage() + " " + !walls[3].isPassage() + " ]");
    }

}
