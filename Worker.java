import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Worker
{
    public static void main(String[] args) {
        try 
        {
            PrintName obj = new PrintName();
            LocateRegistry.createRegistry(1900);
            Naming.rebind("rmi://localhost:1900" + "/secon", obj);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
