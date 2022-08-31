import java.util.LinkedList;
import java.util.Scanner;

public class Crawler {
    public static void showResult(LinkedList<URLDepthPair> resultLink) {
        for (URLDepthPair c : resultLink)
            System.out.println("Depth :" + c.getDepth()+"\tLink :"+c.getURL());
    }


    public static void main(String[] args) {
            Scanner inp = new Scanner(System.in);
            String lineUrl = "http://" + inp.nextLine() + "/";
            int numThreads = inp.nextInt();
            URLPool pool = new URLPool(inp.nextInt());
            pool.addPair(new URLDepthPair(lineUrl, 0));
            for (int i = 0; i < numThreads; i++) {
                CrawlerTask c = new CrawlerTask(pool);
                Thread t = new Thread(c);
                t.start();
            }

            while (pool.getWait() != numThreads) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Ignoring  InterruptedException");
                }
            }
            try {
                showResult(pool.getResult());;
            } catch (NullPointerException e) {
                System.out.println("Not Link");
            }
            System.exit(0);
    }
}