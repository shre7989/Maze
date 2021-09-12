**Instructions:**
- Replace the .txt file int res folder with your text file with the preferred generator and solver you want.
- Click on generate to generate the maze using the specified generation algorithm.
- Click on solve to solve the generated maze using the specified solving algorithm.
- Click on pause to pause the animation.
- Click on the exit button to exit the application.
- Wait until the maze generation animation finishes before you press the solve button to show the animation for solver.
 
**1. Project Description:**
   - This Project implements several maze generating and solving algorithms.
   - All the solver algorithms utilize multi-threading to solve the maze.

**2. Maze Generation algorithms**

   **1. Randomized Depth First Search**
      - This algorithm, also known as the "recursive backtracker" algorithm, is a randomized version of the depth-first search algorithm.Frequently
        implemented with a stack, this approach is one of the simplest ways to generate a maze using a computer. Consider the space for a maze 
        being a large grid of cells (like a large chess board), each cell starting with four walls. Starting from a random cell, the computer then
        selects a random neighbouring cell that has not yet been visited. The computer removes the wall between the two cells and marks the new 
        cell as visited, and adds it to the stack to facilitate backtracking. The computer continues this process, with a cell that has no
        unvisited neighbours being considered a dead-end. When at a dead-end it backtracks through the path until it reaches a cell with an
        unvisited neighbour, continuing the path generation by visiting this new, unvisited cell (creating a new junction). This process continues
        until every cell has been visited, causing the computer to backtrack all the way back to the beginning cell. We can be sure every cell is
        visited.

   **2. Randomized Kruskal's algorithm**
       - Kruskal's Maze Generator is a randomized version of Kruskal’s algorithm: a method for producing a minimal spanning tree for a weighted 
         graph. It associates each cell with a unique set ID at first. Then it randomly selects a wall, and joins the set of the connected cells if 
         they are of different set, and makes the dividing wall a passage. It does this for all walls in the grid, until it generates a minimal 
         spanning tree maze.

   **3. RandomizedPrims algorithm**
       - This maze generation algorithm is a randomized version of the Prims algorithm. It starts with a grid full of walls, and while there are 
         walls it randomly selects a wall, sees if any of the two connected cells are unvisited, marks it visited and makes the wall a passage. It 
         does for all walls in the grid until it generates a maze.

**3. Maze solving algorithms**
   
   **1. Random mouse algorithm**
      - This is a trivial method that can be implemented by a very unintelligent robot or perhaps a mouse. It is simply to proceed following the 
        current passage until a junction is reached, and then to make a random decision about the next direction to follow. Although such a method
        would always eventually find the right solution, this algorithm can be extremely slow.

   **2. Wall Follower**
      - The best-known rule for traversing mazes is the wall follower, also known as either the left-hand rule or the right-hand rule. If the maze 
        is simply connected, that is, all its walls are connected together or to the maze's outer boundary, then by keeping one hand in contact 
        with one wall of the maze the solver is guaranteed not to get lost and will reach a different exit if there is one; otherwise, the 
        algorithm will return to the entrance having traversed every corridor next to that connected section of walls at least once. The algorithm 
        is a depth-first in-order tree traversal.
        
   **3. The Pledge algorithm**
      - Designed to circumvent obstacles, requires an arbitrarily chosen direction to go toward, which will be preferential. When an obstacle is 
        met, one hand (say the right hand) is kept along the obstacle while the angles turned are counted (clockwise turn is positive, 
        counter-clockwise turn is negative). When the solver is facing the original preferential direction again, and the angular sum of the turns
        made is 0, the solver leaves the obstacle and continues moving in its original direction.


