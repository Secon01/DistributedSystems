package ds;
public class Request 
{
    private int id;                 // id of request
    private static int requestID = 0;               // id for request

    public void setId(int id) {     // set request's id
        this.id = id;
    }

    public int getId() {            // get request's id
        return id;
    }

    // Give unique number in order in the next request
    public int generateUniqueNumber() {
        return requestID++;               
    }
}