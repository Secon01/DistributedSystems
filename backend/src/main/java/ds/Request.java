package ds;
public class Request 
{
    private int id;                 // id of request
    private int managerID;          // instance for manager ID

    // Setters, getters
    public void setId(int id) {     
        this.id = id;
    }

    public void setManagerID(int managerID) {
        this.managerID = managerID;
    }

    public int getId() 
    {            
        return id;
    }

    public int getManagerID() {
        return managerID;
    }
}