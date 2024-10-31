package com.example.comp3160project;


public class Restaurant {

    private String imageUrl;
    private String name;
    private String street;
    private int distance;
    private double rating;

    public Restaurant()
    {

    }

    public Restaurant() {

    }

    public Restaurant(String imageUrl, String name, String street, int distance, double rating) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.street = street;
        this.distance = distance;
        this.rating = rating;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getDistance() {
        if (distance < 1000)
           return distance + "m";
        else
            return distance/1000 + "km"; //todo: make it so it also includes a decimal point
    }

    public double getRating() {
        return rating;
    }


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    /* Stuff to implement later:
    //Information
    private static final int TAKEOUT = 485;
    private static final int TAKEOUT_ONLY = 636;
    private static final int DELIVERY = 835;
    private static final int NO_DELIVERY = 442;
    private static final int DINE_IN = 928;
    private static final int DINE_IN_ONLY = 103;
    private static final int DRIVE_THROUGH = 887;



    private String name;
    private String imgURL;

    //Todo: Decide on a better way to store the resturaunt hours (I think there is a class for it)
    private int[][] hours;
    //Columns = days, 0 = Sunday 6 = Saturday:
    //Rows = hours open {700,1630} = Open 7:00am-4:30pm {700,1330, 1430,1900} = 7am-1:30pm and 2:30pm-7:00pm (1h break)
    private double[] coords;
    private int[] options;


    public Restaurant(String name, String imgURL, int[][] hours, double[] coords, int[] options) {
        this.name = name;
        this.imgURL = imgURL;
        this.hours = hours;
        this.coords = coords;
        this.options = options;
    }

    */

}
