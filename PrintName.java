import java.rmi.RemoteException;
//import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.UnicastRemoteObject;

public class PrintName extends UnicastRemoteObject
                       implements Print, Runnable
{
    // Default constructor to throw RemoteException
    // from its parent constructor
    PrintName() throws RemoteException
    {
        super();
    }
    // Implementing print interface 
    public String printStr(String str) throws RemoteException 
    {
        return str;
    }
    @Override
    public void run() {
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
}