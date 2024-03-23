import java.rmi.Naming;

public class Master 
{
    int x ; 
    public static void main(String[] args) {
        String answer, value = "Sotiris";
        try 
        {
            Print access = (Print)Naming.lookup("rmi://localhost:1900" + "/secon");    
            answer = access.printStr(value);

            System.out.println(answer);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}