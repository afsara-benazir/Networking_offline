package ServerClientWithDownload;

//import ServerClientWithDownload.WorkerThread;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {
    public static int workerThreadCount = 0;

    public static void main(String[] args) {
        int id = 1;

        try {
            ServerSocket ss = new ServerSocket(5555);//waiting for connection
            System.out.println("Server has been started successfully\n");

            while (true)
            {
                //accepting a connection
                Socket s = ss.accept();

                //creating a clientHandler object which implements runnable
                WorkerThread wt = new WorkerThread(s,id);

                //instance passed to Thread(Runnable Target)
                Thread t = new Thread(wt);

                //In order to have the HttpRequest object handle the incoming HTTP service request in a separate thread,
                // we first create a new Thread object,
                // passing to its constructor a reference to the HttpRequest object,
                // and then call the thread's start() method.

                //start method invoked on thread object t
                t.start();
                workerThreadCount++;
                System.out.println("Client ["+id+"] is now connected.# of worker threads = "+workerThreadCount);
                id++;
            }
        }
        catch (Exception e)
        {
            System.err.println("Problem in server socket connection. Exiting main\n");

        }
    }
}
