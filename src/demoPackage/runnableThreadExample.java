package demoPackage;

public class runnableThreadExample implements Runnable{

    @Override
    public void run() {
        System.out.println("inside "+ Thread.currentThread().getName());

    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("inside "+ Thread.currentThread().getName());
        System.out.println();

        System.out.println("creating new thread\n");
        Runnable runnable = new runnableThreadExample();

        System.out.println("creating new thread\n");
        Thread t = new Thread(runnable);
        t.start();
        t.sleep(5000);

    }
}
