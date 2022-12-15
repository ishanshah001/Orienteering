/**
 * @author Ishan Shah
 * It takes 4 arguments, in order: terrain-image, elevation-file, path-file, output-image-filename.
 * Finds the most optimal path based on the terrain
 * The program outputs an image of the input map with the optimal path drawn on top of it
 * and also the total path length in meters to the terminal.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.*;
import javax.imageio.ImageIO;

public class lab1 {

    /**
     * A list to store all the types of terrains
     */
    private static List<Terrain> terrain_types = new ArrayList<>();

    public static void main(String[] args){
        
        if (args.length != 4){
            System.err.println("Usage: java lab1 terrain.png mpp.txt red.txt redOut.png");
        }
        else{
            long start = System.nanoTime();
            /**
             * Locations to be traversed by the path
             */
            List<Point> locations = new ArrayList<>();

            /**
             * The optimal path
             */
            List<Pixel> path = new ArrayList<>();

            init_terrain();

            BufferedImage map = null;
            String elevation_file = null;
            String path_file = null;
            try {
                // read files
                map = ImageIO.read(new File(args[0]));
                elevation_file = Files.readString(Paths.get(args[1]));
                path_file = Files.readString(Paths.get(args[2]));
            } catch (IOException e){
                System.err.println(e);
            }
            File output_file = new File(args[3]);

            String[] split_elevation_file = elevation_file.strip().split("\\s+");

            // Removes the scientific notation
            for(int i = 0; i < split_elevation_file.length; i++){
                if(split_elevation_file[i].length() > 0){
                    split_elevation_file[i] = split_elevation_file[i].substring(0, split_elevation_file[i].length() - 5);
                }
            }

            // reference: https://stackoverflow.com/questions/9101301/how-to-convert-string-array-to-double-array-in-one-line
            double[] one_d_elevation = Arrays.stream(split_elevation_file).mapToDouble(Double::parseDouble).toArray();

            // Converts to 2D array
            double[][] elevation = new double[400][500];
            for(int row = 0; row < 400; row++){
                for (int col = 0; col < 500; col++){
                    elevation[row][col] = one_d_elevation[row + (col * 400)];
                }
            }

            String[] co_ordinates = path_file.split("\\s+");

            for(int i = 0; i < co_ordinates.length; i+=2){
                int x = Integer.parseInt(co_ordinates[i]);
                int y = Integer.parseInt(co_ordinates[i + 1]);
                locations.add(new Point(x, y));
            }

            for(int i = 0; i < locations.size() - 1; i++){
                aStarSearch search = new aStarSearch(map, terrain_types, elevation,
                        locations.get(i), locations.get(i + 1));
                path.addAll(search.solve());
            }

            /**
             * Calculates total distance
             */
            double total_distance = 0;
            for(int i = 0; i < path.size() - 1; i++){
                Point current = path.get(i).location;
                Point next = path.get(i + 1).location;

                if(current.y == next.y){
                    total_distance += aStarSearch.LONGITUDE;
                }
                else if(current.x == next.x){
                    total_distance += aStarSearch.LATITUDE;
                }
            }

            System.out.println("Total Distance: " + total_distance+ " m");

            /**
             * Calculates total time
             */
            long totalTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start);
            System.out.println("Time Taken: " + totalTime + " seconds");

            /**
             * Edits input map to add optimal path
             */
            for(Pixel pixel: path){
                map.setRGB(pixel.location.x, pixel.location.y, Color.red.getRGB());
            }
            try {
                ImageIO.write(map, "PNG", output_file);
            } catch(IOException e){
                System.err.println(e);
            }
        }

    }


    /**
     * Initialises all the terrain types    
     */
    static void init_terrain(){
        terrain_types.add(new Terrain("Open land", new int[] {248, 148, 18}, 1.0));
        terrain_types.add(new Terrain("Rough meadow", new int[]{255,192,0}, 0.4));
        terrain_types.add(new Terrain("Easy movement forest", new int[]{255, 255, 255}, 0.75));
        terrain_types.add(new Terrain("Slow run forest", new int[]{2, 208, 60}, 0.6));
        terrain_types.add(new Terrain("Walk forest", new int[]{2, 136, 40}, 0.5));
        terrain_types.add(new Terrain("Impassible vegetation", new int[]{5, 73, 24}, 0.1));
        terrain_types.add(new Terrain("Lake/Swamp/Marsh", new int[]{0, 0, 255}, 0.01));
        terrain_types.add(new Terrain("Paved road", new int[]{71, 51, 3}, 0.95));
        terrain_types.add(new Terrain("Footpath", new int[]{0, 0, 0}, 0.8));
        terrain_types.add(new Terrain("Out of bounds", new int[]{205, 0, 101}, 0.00001));
    }
}
