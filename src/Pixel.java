/**
 * @author Ishan Shah
 * Represents an individual pixel on the map
 */

import java.awt.*;

public class Pixel implements Comparable<Pixel> {

    Point location;
    double g_score;
    double h_score;
    double f_score;
    Pixel parent_pixel;

    /**
     * Constructor for initialising a pixel
     * @param location location of the pixel
     * @param g_score the movement cost to move from the current pixel to the next pixel
     * @param h_score the estimated movement cost to move from the current pixel to the target pixel
     * @param parent the parent pixel to keep track of the path
     */
    public Pixel(Point location, double g_score, double h_score, Pixel parent) {
        this.location = location;
        this.g_score = g_score;
        this.h_score = h_score;
        this.f_score = g_score + h_score;
        parent_pixel = parent;
    }

    /**
     * the function to compare 2 pixels based on f score
     * @param other the pixel to be compared to
     * @return -1 if smaller f_score
     */
    public int compareTo(Pixel other) {
        if(this.f_score < other.f_score){
            return -1;
        } 
        else if(this.f_score > other.f_score){
            return 1;
        } 
        else {

            if (this.location.x == other.location.x) {
                return this.location.y - other.location.y;
            }
            return this.location.x - other.location.x;
        }
    }

    /**
     * Used to compare 2 pixels
     * @param other the pixel to be compared to
     * @return true if equal else false
     */
    public boolean equals(Object other) {
        if(this == other){
            return true;
        }
        if(other == null || this.getClass() != other.getClass()){
            return false;
        }
        Pixel other_pixel = (Pixel) other;
        return this.location.equals(other_pixel.location);
    }

    /**
     * the hashcode of the current pixel to individually identify each pixel
     * @return x*5 + y*7
     */
    public int hashCode() {
        return location.x * 5 + location.y * 7;
    }

}
