public class Worker extends Thread
{
    public void run()
    {
        System.out.println("Hello I am a worker");
    }

    Worker(String name)
    {
        this.setName(name);
    }
}
