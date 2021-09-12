/**
 * @project - Maze
 * @course - CS351
 * @author - Mausam Shrestha
 */
package maze;

import javafx.animation.Timeline;

/**
 * Interface for our maze generation algorithms
 */
public interface MazeGeneratorAlgorithm {
    void setup(Maze maze);
    void generateMaze(Maze maze, Timeline animation);
    Timeline getAnimation();
}
