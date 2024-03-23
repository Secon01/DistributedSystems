import java.rmi.Naming;

public class Master 
{
    static int y = 5;
    static int x ; 
    static void func(int y) 
    {
        x = 3;
        System.out.println(x - y);
    }
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

        func(3);
        
    }


}