/**
 * @author Ishan Shah
 * Represents the different terrains provided
 */
public class Terrain {
    
    String name;

    int[] color;

    double speed;

    /**
     * Constructor for a terrain
     * @param name the terrain type 
     * @param color color on the map
     * @param speed the speed you will be able to proceed at in the given terrain
     */
    public Terrain(String name, int[] color, double speed) {
        this.name = name;
        this.color = color;
        this.speed = speed;
    }  
}