package com.example;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;

public class Client 
{
   public Person readJsonFile(String filePath) throws FileNotFoundException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, Person.class);
        } catch (Exception e) {
            throw new FileNotFoundException("Failed to read JSON file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        Person stef;
        try {
            stef = client.readJsonFile("C:/Users/sotir/DistributedSystems/com/demo/src/main/java/com/example/Person.json");
            System.out.println(stef.name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //stef.setName("Stefanos");
        //System.out.println(stef.name);
    }
}
