/**
 * @project - Maze
 * @course - CS351
 * @group - 2
 */
package maze;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * WallFollower - a callable instance that utilizes the wall follower algorithm to solve the maze
 */
public class WallFollower implements Callable<ArrayList<Object>> {
    private Cell current;
    private ArrayList<Cell> path;
    private ArrayList<Character> directions;
    private WallFollowerMazeSolver solver;
    private Maze maze;
    private char type;
    private boolean isSolver;
    private char direction;

    /**
     * WallFollower - constructor that constructs our wall follower
     * @param start - start point of this follower
     * @param direction - direction of this follower
     * @param followerType - type of follower : left or right
     * @param solver - solver that utilizes this
     * @param maze
     */
    public WallFollower(Cell start, char direction, char followerType, WallFollowerMazeSolver solver, Maze maze){
        this.current = start;
        this.direction = direction;
        this.path = new ArrayList<>();
        this.directions = new ArrayList<>();
        this.maze = maze;
        this.solver = solver;
        this.type = followerType;
        this.isSolver = false;
    }

    @Override
    public ArrayList<Object> call() throws Exception {
        followWall(maze);
        ArrayList<Object> data = new ArrayList<>();
        data.add(current);
        data.add(path);
        data.add(isSolver);
        return data;
    }

    /**
     * followWall - helps the follower to move according to its follower type
     * @param maze - maze where the follower moves
     */
    public void followWall(Maze maze){
        Wall[] walls;
        while(!solver.hasMadeContact()) {
            walls = current.getWallBound();
            //if(isInDeadEnd(current,direction))
            if(type == 'l') {
                if (solver.hasMadeContact()) {
                    return;
                }
                if (noLeftWall(walls, direction)){
                    direction = rotate('l');
                    moveForward(direction, maze,'l');
                }
                else if (noFrontWall(walls, direction)) {
                    moveForward(direction, maze,'f');
                }
                else if(noRightWall(walls,direction)){
                    direction = rotate('r');
                    moveForward(direction, maze,'r');
                }
                else{
                    direction = rotate('r');
                    direction = rotate('r');
                }
            }
            else if(type == 'r'){
                if (solver.hasMadeContact()) return;
                else if (noRightWall(walls, direction)) {
                    direction = rotate('r');
                    moveForward(direction,maze,'l');
                }
                else if (noFrontWall(walls, direction)) moveForward(direction, maze, 'f');
                else if (noLeftWall(walls,direction)){
                    direction = rotate('l');
                    moveForward(direction,maze,'l');
                }
                else{
                    direction = rotate('l');
                    direction = rotate('l');
                }
            }
        }
    }

    /**
     * rotate - helps to rotate the direction of the follower
     * @param type - type to rotate CW o ACW
     * @return - direction after rotation
     */
    public char rotate(char type){
        if(type == 'l'){
            System.out.println("Rotating left");
            /* counter-clockwise rotation */
            if(direction == 't') return 'l';
            else if(direction == 'r') return 't';
            else if(direction == 'b') return 'r';
            else if(direction == 'l') return 'b';
        }
        else if(type == 'r'){
            System.out.println("Rotatting right");
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
     * moveForward - move forward in the current direction
     * @param direction - direction of the follower
     * @param maze - maze where the follower runs
     */
    public synchronized void moveForward(char direction, Maze maze, char isTurn){
        Cell[][] grid = maze.getGrid();
        int row = current.getRow();
        int col = current.getCol();

        if(direction == 't'){
            if(!path.contains(current)) {
                path.add(current);
                if(isTurn == 'l') directions.add('L');
                else if(isTurn == 'R') directions.add('R');
                else directions.add('F');
            }
            else{
                int index = path.indexOf(current);
                directions.set(index,'B');
            }
            current = grid[row - 1][col];
            if(current.isSeen() && !path.contains(current)){
                solver.setMadeContact();
                this.isSolver = true;
            }
            else synchronized (this){current.setSeen();}
        }
        else if(direction == 'r'){
            if(!path.contains(current)) {
                path.add(current);
                if(isTurn == 'l') directions.add('L');
                else if(isTurn == 'R') directions.add('R');
                else directions.add('F');
            }else{
                int index = path.indexOf(current);
                directions.set(index,'B');
            }
            current = grid[row][col + 1];
            if(current.isSeen() && !path.contains(current)){
                solver.setMadeContact();
                this.isSolver = true;
            }
            else synchronized (this){current.setSeen();}
        }
        else if(direction == 'b'){
            if(!path.contains(current)) {
                path.add(current);
                if(isTurn == 'l') directions.add('L');
                else if(isTurn == 'R') directions.add('R');
                else directions.add('F');
            }
            else{
                int index = path.indexOf(current);
                directions.set(index,'B');
            }
            current = grid[row + 1][col];
            if(current.isSeen() && !path.contains(current)){
                solver.setMadeContact();
                this.isSolver = true;
            }
            else synchronized (this){current.setSeen();}
        }
        else if(direction == 'l'){
            if(!path.contains(current)) {
                path.add(current);
                if(isTurn == 'l') directions.add('L');
                else if(isTurn == 'R') directions.add('R');
                else directions.add('F');
            }
            else{
                int index = path.indexOf(current);
                directions.set(index,'B');
            }
            current = grid[row][col - 1];
            if(current.isSeen() && !path.contains(current)){
                solver.setMadeContact();
                this.isSolver = true;
            }
            else synchronized (this){current.setSeen();}
        }
    }


    private void printOptimizedPath(ArrayList<Character> optimizedPath){
        for(Character c: optimizedPath){
            System.out.print(c + "  -> ");
        }
        System.out.println();
    }
}
