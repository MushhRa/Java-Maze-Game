/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package maze;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.List;
/**
 *
 * @author Mush
 */

/*
 * Generate a maze using the Depth-First Search algorithm.
 * The maze is represented as a 2D array of integers.
 * https://en.wikipedia.org/wiki/Maze_generation_algorithm#Randomized_depth-first_search
 * 1) Choose the initial cell, mark it as visited and push it to the stack
 * 2) While the stack is not empty, do the following:
 *      1) Pop a cell from the stack and make it a current cell
 *      2) If the current cell has any neighbours which have not been visited
 *          1) Push the current cell to the stack
 *          2) Choose one of the unvisited neighbours 
 *          3) Remove the wall between the current cell and the chosen cell
 *          4) Mark the chosen cell as visited and push it to the stack
 */
public class Maze {

    /**
     * @param args the command line arguments
     */
    private class Cell {
        // Constructor
        int[] value;
        Cell next;
        Cell(int[] value, Cell next_element) {
            this.value = value;
            this.next = next_element;
        }

        // Empty Constructor
        Cell() {
            this.value = new int[2];
            this.next = null;
        }
    }

    private class Stack {
        // Constructor for the stack 
        Cell head = new Cell();
        int length;
        public Stack() {
            this.head = new Cell();
            this.length = 0;
        }

        // Method to push a value onto the stack
        public void insert(int[] data) {
            this.head = new Cell(data, this.head);
            this.length++;
        }

        // Method to return the top value of the stack
        public int[] pop() {
            if (this.length == 0) {
                return null;
            } else {
                int[] data = this.head.value;
                this.head = this.head.next;
                this.length--;
                return data;
            }
        }

        /* Method to return if stack is not empty
            Returns true if stack is empty*/ 
        public boolean isEmpty() {
            if (this.length == 0) {
                return true;
            } else {
                return false;
            }
        }

    }

    /**
     * Method to generate and return a 2d array of integers representing the maze
     * 0 = Wall
     * 1 = Path
     * 2 = Start
     * 3 = End
     * @param rows
     * @param cols
     * @param p0 - starting position [x,y]
     * @param pf - ending position [x,y]
     */
    public int[][] generateMaze(int rows, int cols, int[] p0, int[] pf) {
        // Creates temp maze where everything is a wall (0)
        int[][] maze = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = 0;
            }
        }

        // Creates a stack to store the cells to be visited
        boolean[][] seen = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                seen[i][j] = false;
            }
        }
        int[][][] previous = new int[rows][cols][2];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < 2; k++) {
                    previous[i][j][k] = -1;
                }
            }
        }

        // Create stack and push start position (p0)
        Stack stack = new Stack();
        stack.insert(p0);

        // While stack is not empty keep going until no more paths to explore
        while (!stack.isEmpty()) {
            // Remove current position and mark it as viewed
            int[] position = stack.pop();
            int x = position[0];
            int y = position[1];
            seen[x][y] = true;

            /* Check if neighbors are valid and not visited previously 
             * Continues if:
             * Neighbor is in the maze
             * Position was already marked as a path
             * Position is the same as the one before it in the path
            */
            if (x + 1 < rows && maze[x+1][y] == 1 && !(Arrays.equals(previous[x][y], new int[] {x+1, y}))) {
                continue;
            }
            if (x > 0 && maze[x-1][y] == 1 && !(Arrays.equals(previous[x][y], new int[] {x-1, y}))) {
                continue;
            }
            if (y + 1 < cols && maze[x][y+1] == 1 && !(Arrays.equals(previous[x][y], new int[] {x, y+1}))) {
                continue;
            }
            if (y > 0 && maze[x][y-1] == 1 && !(Arrays.equals(previous[x][y], new int[] {x, y-1}))) {
                continue;
            }

            // Marks as walkable position
            maze[x][y] = 1;

            // Array to shuffle neigbors before adding to stack
            // Fake temp values that will be deleted later on
            int[][] to_stack = {{-6, -6}};

            /* Check if neighbors are valid and not visited previously 
             * Marks adjacent neighbors as visited 
             * Takes position and adds into to_stack
             * Sets previous to current position
            */
            if (x + 1 < rows && seen[x+1][y] == false) {
                seen[x+1][y] = true;
                int[] addition = {x+1, y};
                to_stack = append(to_stack, addition);
                previous[x+1][y] = new int[] {x, y};
            }
            if (x > 0 && seen[x-1][y] == false) {
                seen[x-1][y] = true;
                int[] addition = {x-1, y};
                to_stack = append(to_stack, addition);
                previous[x-1][y] = new int[] {x, y};
            }
            if (y + 1 < cols && seen[x][y+1] == false) {
                seen[x][y+1] = true;
                int[] addition = {x, y+1};
                to_stack = append(to_stack, addition);
                previous[x][y+1] = new int[] {x, y};
            }
            if (y > 0 && seen[x][y-1] == false) {
                seen[x][y-1] = true;
                int[] addition = {x, y-1};
                to_stack = append(to_stack, addition);
                previous[x][y-1] = new int[] {x, y};
            }

            // Deletes fake value from to_stack
            to_stack = remove(to_stack, 0);

            // Flag to indicate if Pf is a neighbor
            boolean pf_flag = false;

            // Adds everything from to_stack to neighbor array
            int[][] neighbor = new int[to_stack.length][2];
            for (int i = 0; i < to_stack.length; i++) {
                neighbor[i] = to_stack[i];
            }

            // While there are elements in to_stack
            while (true) {
                if (to_stack.length == 0) {
                    break;
                }

                /* Randomly removes an element from to_stack 
                 * Element that was removed becomes the neighbor
                */
                if (to_stack.length == 1) {
                    neighbor = to_stack;
                    to_stack = randRemove(to_stack);
                } else {
                    to_stack = randRemove(to_stack);
                    neighbor = getOpposite(neighbor, to_stack);

                }
                
                // Checks if neighbor is Pf
                if (Arrays.equals(neighbor[0], pf)) {
                    pf_flag = true;
                } 
                // Add to top of the stack
                else {
                    stack.insert(neighbor[0]);
                }
                
            }

            // Pf will be on top 
            if (pf_flag) {
                stack.insert(pf);
            }
        }

        // Marks initial positions
        int x0 = p0[0];
        int y0 = p0[1];
        int xf = pf[0];
        int yf = pf[1];
        maze[x0][y0] = 2;
        maze[xf][yf] = 3;
        maze[x0][y0+1] = 1;
        maze[x0+1][y0] = 1;

        // In case of bad maze generation, recursively calls generateMaze
        int top = maze[rows-2][cols-1];
        int left = maze[rows-1][cols-2];
        if (top == 0 && left == 0) {
            return generateMaze(rows, cols, p0, pf);
        } else {
            return maze;
        }
    }

    // Method to add an element to a nested array
    public static int[][] append(int[][] original, int[] addition) {
        int[][] result = new int[original.length + 1][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[0].length; j++) {
                result[i][j] = original[i][j];
            }
        }
        for (int i = 0; i < addition.length; i++) {
            result[original.length][i] = addition[i];
        }
        return result;
    }

    // Method to remove random element from a nested array
    public static int[][] randRemove(int[][] arr) {
        Random random = new Random();
        int r = random.nextInt(arr.length);
        List<List<Integer>> nestedLists =
                Arrays.stream(arr)
                .map(internalArray -> 
                Arrays.stream(internalArray)
                .boxed().
                collect(Collectors.toList())).
                collect(Collectors.toList());
        nestedLists.remove(r);

        int[][] result = new int[nestedLists.size()][];
        for (int i = 0; i < nestedLists.size(); i++) {
            int[] temp = new int[nestedLists.get(i).size()];
            for (int j = 0; j < nestedLists.get(i).size(); j++) {
                temp[j] = nestedLists.get(i).get(j);
            }
            result[i] = temp;
        }
        return result;
    }

    // Method to remove an element from a nested array given an index
    public static int[][] remove(int[][] arr, int index) {
        List<List<Integer>> nestedLists =
                Arrays.stream(arr)
                .map(internalArray -> 
                Arrays.stream(internalArray)
                .boxed().
                collect(Collectors.toList())).
                collect(Collectors.toList());
        nestedLists.remove(index);

        int[][] result = new int[nestedLists.size()][];
        for (int i = 0; i < nestedLists.size(); i++) {
            int[] temp = new int[nestedLists.get(i).size()];
            for (int j = 0; j < nestedLists.get(i).size(); j++) {
                temp[j] = nestedLists.get(i).get(j);
            }
            result[i] = temp;
        }
        return result;
    }

    // Method that finds the element that was removed from an array and returns it
    public static int[][] getOpposite(int[][] original, int[][] removed) {
        List<List<Integer>> nestedLists =
                Arrays.stream(original)
                .map(internalArray -> 
                Arrays.stream(internalArray)
                .boxed().
                collect(Collectors.toList())).
                collect(Collectors.toList());

        List<List<Integer> > nestedLists2 =
            Arrays.stream(removed)
            .map(internalArray -> 
            Arrays.stream(internalArray)
            .boxed().
            collect(Collectors.toList())).
            collect(Collectors.toList());

        nestedLists.removeAll(nestedLists2);

        int[][] result = new int[nestedLists.size()][];
        for (int i = 0; i < nestedLists.size(); i++) {
            int[] temp = new int[nestedLists.get(i).size()];
            for (int j = 0; j < nestedLists.get(i).size(); j++) {
                temp[j] = nestedLists.get(i).get(j);
            }
            result[i] = temp;
        }
        return result;
    }
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
