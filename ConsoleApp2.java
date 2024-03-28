package com.example;

import java.util.Scanner;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.FileNotFoundException;

public class ConsoleApp2 {

    boolean done;
    Scanner inp;
    Info information = new Info();

    public void welcome() {
        System.out.println("+---------------------------------+");
        System.out.println("|  You've logged in as a Manager  |");
        System.out.println("+---------------------------------+");

    }

    // Print Menu

    public void menu() {
        System.out.println("Select an action");
        System.out.println(" 1) Insert House Information ");
        System.out.println(" 2) Insert Date / Availability");
        System.out.println(" 3) Continue ");
        System.out.println(" 0) Exit");
    }

    public void runMenu() {

        welcome();
        while (!done) {
            menu();
            int choice = getInput();
            performAction(choice);
        }

    }

    public int getInput() {
        inp = new Scanner(System.in); // initialize scanner
        int choice = -1;
        while (choice < 0 || choice > 3) { // user input number out of bounds
            try {
                System.out.println("Enter your selection: ");
                choice = Integer.parseInt(inp.nextLine()); // user input
            } catch (NumberFormatException e) {
                System.out.println("Invalid selection! Please try again.");
            }
        }
        return choice;
    }

    // Perform action according to choice
    public void performAction(int choice) {
        switch (choice) {
            case 0:
                System.out.println(" Your room is now available for booking");
                done = true;
                break;
            case 1:
                System.out.println("Enter the path of your JSON file: ");
                String filePath = inp.nextLine();
                try {
                    Room room = readJsonFile(filePath);
                    information.setRoom(room);
                    System.out.println("JSON file parsed successfully.");
                } catch (FileNotFoundException e) {
                    System.out.println("File not found. Please try again.");
                }
                break;
            case 2:
                System.out.println("Enter you start date");
                information.setAvailabilityDateStart(inp.nextLine());
                System.out.println("Enter you end date");
                information.setAvailabilityDateEnd(inp.nextLine());

                break;
            case 3:
                System.out.println("These are your room's information ");
                // System.out.println(information.path);
                System.out.println(information.availableDateS);
                System.out.println(information.availableDateE);
                break;
            default:
                System.out.println("An unknown error has occured!");
                break;
        }
    }

    // Info chunck

    public class Info {

        private Room a;
        private String availableDateS;
        private String availableDateE;

        public void setRoom(Room room) {
            this.a = room;
            System.out.println(a);
        }

        public void setAvailabilityDateStart(String b) {
            this.availableDateS = b;
        }

        public void setAvailabilityDateEnd(String c) {
            this.availableDateE = c;
        }

    }

    public class Room {
        private String roomName;
        private String date;
        private int guestNum;
        private double price;
        private int stars;
        private String area;
        private int reviews;
        private String roomImage;

        // Getters
        public String getRoomName() {
            return roomName;
        }

        public String getDate() {
            return date;
        }

        public int getGuestNum() {
            return guestNum;
        }

        public double getPrice() {
            return price;
        }

        public int getStars() {
            return stars;
        }

        public String getArea() {
            return area;
        }

        public int getReviews() {
            return reviews;
        }

        public String getRoomImage() {
            return roomImage;
        }

        // Setters
        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setGuestNum(int guestNum) {
            this.guestNum = guestNum;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void setStars(int stars) {
            this.stars = stars;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public void setReviews(int reviews) {
            this.reviews = reviews;
        }

        public void setRoomImage(String roomImage) {
            this.roomImage = roomImage;
        }
    }

    public Room readJsonFile(String filePath) throws FileNotFoundException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, Room.class);
        } catch (Exception e) {
            throw new FileNotFoundException("Failed to read JSON file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ConsoleApp2 insertion = new ConsoleApp2();
        insertion.runMenu();

    }
}