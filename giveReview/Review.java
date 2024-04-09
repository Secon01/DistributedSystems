package com.example;

import java.lang.reflect.Field;

public class Review extends Request {
    private double review;
    private String roomForReview;
  
    Review(double rev, String room){
        this.review=rev;
        this.roomForReview=room;
    }
    //setters
    public void setReview(double r){
        this.review = r;
    }
    public void setRoomForReview(String ro){
        this.roomForReview = ro ;
    }

    //getters 
    public double getReview(){
        return review ;
    }

    public String getRoomForReview(){
        return roomForReview ;
    }
    public int numNonZero()
    
    {
        int count = 0;
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(this);

                if(value != null && !value.equals(0) && !value.equals(0.0)) {
                    count++;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
    public String toString()
    {
        return "[" + this.review + ", " + this.roomForReview + "]";
    }
}

