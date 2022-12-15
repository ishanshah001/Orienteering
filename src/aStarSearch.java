/**
 * @author Ishan Shah
 * A * Search is used to find the optimal path between the start point and target point.
 * At each step it picks the pixel according to the f_score
 * which is equal to the sum of g_score and h_score.
 * At each step it picks the pixel having the lowest f_score, and processes that pixel.
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class aStarSearch {

    /**
     * The real-world pixel size
     */
    static final double LONGITUDE = 10.29;
    static final double LATITUDE = 7.55;

    /**
     * Boundaries of the map
     */
    private static final int NORTH = 498;
    private static final int SOUTH = 1;
    private static final int EAST = 393;
    private static final int WEST = 1;

    private final BufferedImage map;
    private final List<Terrain> terrains;
    private final double[][] elevation;
    private final Point start_point;
    private final Point target_point;

    /**
     * a priority queue based of pixels based on f scores
     */
    PriorityQueue<Pixel> exploration_queue = new PriorityQueue<>();

    /**
     * a set to keep track of visited pixels
     */
    Set<Pixel> visited = new HashSet<>();

    /**
     * Constructor to initialise the A * Search class
     * @param map represents the map in rgb
     * @param terrain_types list of all the terrains
     * @param elevation text representation of the elevation
     * @param start_point starting pixel
     * @param target_point goal pixel
     */
    public aStarSearch(BufferedImage map, List<Terrain> terrain_types, double[][] elevation,
                       Point start_point, Point target_point) {
        this.map = map;
        this.terrains = terrain_types;
        this.elevation = elevation;
        this.start_point = start_point;
        this.target_point = target_point;
    }

    /**
     * finds the optimal path between the start pixel and the goal pixel
     * @return the optimal path
     */
    public ArrayList<Pixel> solve() {

        Pixel start = new Pixel(start_point, 0, 0, null);
        exploration_queue.add(start);

        Pixel goal = null;
        while(!exploration_queue.isEmpty()) {
            
            Pixel current = exploration_queue.remove();
            
            if(visited.contains(current)) {
                continue;
            }

            // 4 neighbors

            // North
            if(current.location.y <= NORTH) {
                Pixel neighbor = get_neighbor(current, new Point(current.location.x, current.location.y + 1));
                if(neighbor.location.equals(target_point)) {
                    goal = neighbor;
                    break;
                }
                explore(neighbor);
            }

            // South
            if(current.location.y >= SOUTH) {
                Pixel neighbor = get_neighbor(current, new Point(current.location.x, current.location.y - 1));
                if(neighbor.location.equals(target_point)) {
                    goal = neighbor;
                    break;
                }
                explore(neighbor);
            }

            // East
            if(current.location.x <= EAST) {
                Pixel neighbor = get_neighbor(current, new Point(current.location.x + 1, current.location.y));
                if(neighbor.location.equals(target_point)) {
                    goal = neighbor;
                    break;
                }
                explore(neighbor);
            }

            // West
            if(current.location.x >= WEST) {
                Pixel neighbor = get_neighbor(current, new Point(current.location.x - 1, current.location.y));
                if(neighbor.location.equals(target_point)) {
                    goal = neighbor;
                    break;
                }
                explore(neighbor);
            }
            visited.add(current);
        }

        return construct_path(goal);
    }

    /**
     * Checks if given pixel has been visited or not
     * Adds the pixel to the exploration queue if not visited
     * @param neighbor the pixel to be checked
     */
    private void explore(Pixel neighbor){
        if(!visited.contains(neighbor)){
            exploration_queue.add(neighbor);
        }
    }

    /**
     * Calculates the euclidean distance between current and target pixel
     * @param current the "from" pixel
     * @param target the "to" pixel
     * @return the euclidean distance between the 2 pixels
     */
    private double euclidean_distance(Point current, Point target) {

        double dx = LONGITUDE * (current.x - target.x);
        double dy = LATITUDE * (current.y - target.y);
        double dz = elevation[current.x][current.y]
                - elevation[target.x][target.y];
        
        double distX = Math.pow(Math.abs(dx), 2);
        double distY = Math.pow(Math.abs(dy), 2);
        double distZ = Math.pow(Math.abs(dz), 2);

        return Math.sqrt(distX + distY + distZ);
    }

    /**
     * Calculates the cost of the path between current and next pixel
     * @param current the "from" pixel
     * @param next the "to" pixel
     * @return the cost of the path
     */
    private double cost_function(Point current, Point next) {
        double dist = euclidean_distance(current, next);
        double current_pixel_speed = get_speed(current);
        double next_pixel_speed = get_speed(next);

        return (dist / 2) / current_pixel_speed + (dist / 2) / next_pixel_speed;
    }

    /**
     * Calculates the h_score between current pixel and target pixel
     * @param current the "from" pixel
     * @param target the "to" pixel
     * @return the euclidean pixel
     */
    private double heuristic_function(Point current, Point target) {
        return  euclidean_distance(current, target);
    }

    /**
     * Returns the neighboring pixel
     * @param curr the pixel whose neighbor needs to be found
     * @param nextPoint the next co-ordinate
     * @return the pixel form of the neighboring pixel
     */
    private Pixel get_neighbor(Pixel curr, Point nextPoint) {
        double g_score = cost_function(curr.location, nextPoint);
        double h_score = heuristic_function(nextPoint, target_point);
        return new Pixel(nextPoint, curr.g_score + g_score, h_score, curr);
    }

    /**
     * returns the speed based on the terrain of the given point
     * @param point the point whose speed needs to be calculated
     * @return the speed
     */
    private double get_speed(Point point) {
        Color color = new Color(map.getRGB(point.x, point.y));
        int[] color_values = new int[]{color.getRed(), color.getGreen(), color.getBlue()};

        for(Terrain t : terrains){
            if(Arrays.equals(t.color, color_values)){
                return t.speed;
            }
        }   

        return 0.000001;
    }

    /**
     * Constructs the path from goal to the first pixel
     * @param goal the target pixel
     * @return path
     */
    private ArrayList<Pixel> construct_path(Pixel goal){
        ArrayList<Pixel> path = new ArrayList<>();
        while(goal != null) {
            path.add(0, goal);
            goal = goal.parent_pixel;
        }
        return path;
    }

}
