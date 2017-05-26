package Core;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 KM
 May 02
 Handles the methods and variables concerned with individual stars, and overall star management after initial creation.

 */

//TODO: Add methods, begin filling out.

public class starClass extends starCore implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() { //used when writing to a save file

        StringBuffer data = new StringBuffer();

        data.append("/");
        data.append(this.getMapLocationX());
        data.append("-");
        data.append(this.getMapLocationY());
        data.append("-");
        data.append(this.getStarIndex());
        data.append("-");
        data.append(this.getStarRadius());
        data.append("-");
        data.append(writeHabitability());
        data.append("-");
        data.append(writeBinary());
        data.append("-");
        data.append(this.getStarSpectral());
        data.append("-");
        data.append(this.getNumOfPlanets());

        return data.toString();
    }

    private String writeHabitability() { //writes the star's habitability boolean as a single character string
        if (this.starIsHabitable) {
            return "t";
        } else {
            return "f";
        }
    }

    private String writeBinary() { //gets the isBinarySystem variable's boolean as a single character string to write to file.
        if (this.isBinarySystem) {
            return "t";
        } else {
            return "f";
        }
    }

    public ArrayList<planetClass> planetList = new ArrayList<>(); //Stores a list of the different starType blueprints.

    private int starRadius;
    private Double starMagnitude = 0.0;
    private String starSpectral;
    private boolean starIsHabitable;
    private boolean isBinarySystem;
    private int numOfPlanets;
    private int surfaceTemp;
    private int starIndex;
    private Double absLum;
    private int habitableZoneMax;
    private int habitableZoneMin;
    private int mapLocationX;
    private int mapLocationY;
    private int arrayLoc;
    private String starName;
    private boolean homeSystem = false;

    //creates a randomized star
    public starClass(int mapLocationX, int mapLocationY){
        this.starIndex = chooseStarType(); //The ID of the star type.
        this.arrayLoc = getStarFromID(this.starIndex); //Finds the position in the array of the star.
        this.surfaceTemp = determineSurfaceTemperature(arrayLoc); //The surface temperature of the star (in kelvin)
        this.starIsHabitable = determineHabitability(arrayLoc); //Whether or not the star can support habitable planets
        this.starSpectral = determineSpectralClass(this.surfaceTemp); //The spectral class of the star
        this.starRadius = determineRadius(arrayLoc); //The star's radius
        this.isBinarySystem = determineBinary(); //Whether or not the star is in a binary system
        this.numOfPlanets = determineNumOfPlanets(arrayLoc); //number of planets orbiting the star
        this.absLum = determineLuminosity(this.starSpectral, this.starMagnitude); //the absolute luminosity of the star
        this.habitableZoneMax = habitableZoneMax(this.absLum); //the upper end of the star's habitable zone
        this.habitableZoneMin = habitableZoneMin(this.absLum); //the lower end of the star's habitable zone
        this.mapLocationX = mapLocationX; //the X position of the star on the map grid
        this.mapLocationY = mapLocationY; //the Y position of the star on the map grid
        System.out.println("Star (ID" + starIndex + ") successfully generated at the coordinates " + mapLocationX + "|" + mapLocationY);
        if (this.numOfPlanets > 0) {
            constructPlanets(this.numOfPlanets);
        }


    }

    //creates a pre-defined star
    public starClass(int mapLocationX, int mapLocationY, String starName, int starIndex, boolean isHabitable, String spectralClass, Double magnitude, int radius, boolean isBinary, int numOfPlanets, boolean randomPlanets) {
        System.out.println("Added new pre-defined star system - " + starName + " System (ID" + starIndex + ")");

        this.mapLocationX = mapLocationX;
        this.mapLocationY = mapLocationY;
        this.starName = starName;
        this.starIndex = starIndex;
        this.arrayLoc = getStarFromID(this.starIndex);
        this.starIsHabitable = isHabitable;
        this.starSpectral = spectralClass;
        this.starRadius = radius;
        this.isBinarySystem = isBinary;
        this.numOfPlanets = numOfPlanets;
        this.starMagnitude = magnitude;
        this.absLum = determineLuminosity(this.starSpectral, this.starMagnitude);

        if (randomPlanets) { //if the planets aren't pre-defined, generate them randomly
            constructPlanets(this.numOfPlanets);
        }

        mapGenerator.predefinedStars.add(this); //adds the star to the map generator to be added when it is run
    }

    //generates the planets surrounding this star
    private void constructPlanets(int planetsToConstruct) {
        System.out.println("Constructing " + planetsToConstruct + " planets.");
        for (int i = 1; i <= planetsToConstruct; i++) {
            planetList.add(new planetClass(this, i));
        }

    }

    /** Setter Methods **/

    public void setHomeSystem(boolean home) {
        this.homeSystem = home;

    }

    /** Accessor Methods **/
    //For calling the variables of objects made from the starClass.

    public int getHabitableZoneMax() { return this.habitableZoneMax; }
    public int getHabitableZoneMin() { return this.habitableZoneMin; }
    public int getStarRadius() { return this.starRadius; }
    public int getNumOfPlanets() { return this.numOfPlanets; }
    public int getMapLocationX() { return this.mapLocationX; }
    public int getMapLocationY() { return this.mapLocationY; }
    public String getStarName() { return this.starName; }
    public int getStarIndex() { return this.starIndex; }
    public String getStarSpectral() { return this.starSpectral; }
    public boolean isHomeSystem() { return this.homeSystem; }

    public BufferedImage getPortraitGFX() {
        return listOfStars.get(arrayLoc).getGfxImage();
    }

    public String getStarClassName() {
        return listOfStars.get(arrayLoc).getName();
    }

    public String getStarClassDesc() { return listOfStars.get(arrayLoc).getDesc(); }

    public int getColonyCount() { //gets the number of colonies in this system
        int colonyCount = 0;

        for (int i = 0; i < this.planetList.size(); i++) {
            if (this.planetList.get(i).getPlanetColony() != null ) {
                colonyCount++;
            }
        }

        return colonyCount;
    }


}
