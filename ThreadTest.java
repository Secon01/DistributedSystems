public class ThreadTest implements Runnable {
    public static void main(String[] args) {
      ThreadTest obj = new ThreadTest();
      Thread thread = new Thread(obj);
      thread.start();
      System.out.println("This code is outside of the thread");
    }
    public void run() {
      int i = 0;
      while (i < 10) {
        i++;
      }
      System.out.println("i = " + i);
    }
  }
  