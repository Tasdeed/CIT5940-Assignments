package homework1.src;

public class FindFirstInstance {

    public static int[] findFirstInstanceOne(int[][] grid, int target) {
        int[] result = {-1, -1};
        boolean found = false; // flag to control the outer loop


        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                
                if (grid[r][c] == target) {
                    result[0] = r;
                    result[1] = c;
                    
                    found = true;
                    break; // break inner loop
                }
            }
            
            if (found) {
                break; // break outer loop
            }
        }
        return result;
    }

    public static int[] findFirstInstanceTwo(int[][] grid, int target) {
        int[] result = {-1, -1};


        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                
                // only save location if we haven’t found yet
                if (grid[r][c] == target && result[0] == -1) {
                    result[0] = r;
                    result[1] = c;
                }
                
            }
        }
        return result;
    }


    // main method for testing purposes. Feel free to modify as you run your tests.
    public static void main(String[] args) {
        
        
        // uncomment one of the grids below to test the methods
        // int[][] grid = getGridOne();
        int[][] grid = getGridTwo();
        
        int target = 5;
        
        long start = System.nanoTime(); // start testing runtime

        // uncomment one of the methods below to test it
        // findFirstInstanceOne(grid, target);
        findFirstInstanceTwo(grid, target);

        long end = System.nanoTime(); // end testing runtime

        System.out.println("Run time was " + (end - start) / 1000000.0 + " milliseconds." ); // print runtime

    }

    public static int[][] getGridOne() {
        int[][] grid = new int[4000][4000];
        grid[0][0] = 5;
        return grid;
    }

    public static int[][] getGridTwo() {
        int[][] grid = new int[4000][4000];
        grid[3999][3999] = 5;
        return grid;
    }




}
