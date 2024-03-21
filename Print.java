import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Print extends Remote
{
    // Declaring the method prototype
    public String printStr(String str) throws RemoteException;
}