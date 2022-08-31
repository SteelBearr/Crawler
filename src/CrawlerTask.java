import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class CrawlerTask implements Runnable {
    URLPool urlPool;
    public static final String URL_PREFIX = "http:";

    public CrawlerTask(URLPool pool) {
        this.urlPool = pool;
    }
    public static void request(PrintWriter out,URLDepthPair pair) {
        out.println("GET " + pair.getPath() + " HTTP/1.1");
        out.println("Host: " + pair.getHost());
        out.println("Connection: close");
        out.println();
        out.flush();
    }
    public static void buildNewUrl(String str,int depth,URLPool pool) {
        try {
            int eOL = str.indexOf("\"", str.indexOf(URL_PREFIX));
            if (eOL == -1 || (str.indexOf("'", str.indexOf(URL_PREFIX)) != -1 && str.indexOf("'", str.indexOf(URL_PREFIX)) < eOL)) {
                eOL = str.indexOf("'", str.indexOf(URL_PREFIX));
            }
            if (eOL == -1 || (str.indexOf("<", str.indexOf(URL_PREFIX)) - 1 != -1 && str.indexOf("<", str.indexOf(URL_PREFIX)) - 1 < eOL)) {
                eOL = str.indexOf("<", str.indexOf(URL_PREFIX)) - 1;
            }
            String currentLink = str.substring(str.indexOf(URL_PREFIX), eOL);
            pool.addPair(new URLDepthPair(currentLink, depth + 1));
        } catch (StringIndexOutOfBoundsException e) {
        }
    }
    @Override
    public void run() {
        while (true) {
            URLDepthPair currentPair = urlPool.getPair();
            try {
                Socket socket = new Socket(currentPair.getHost(), 80);
                socket.setSoTimeout(1000);
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    request(out,currentPair);
                    String line;
                    while ((line = in.readLine()) != null){
                        if (line.indexOf(currentPair.URL_PREFIX)!=-1) {
                            buildNewUrl(line,currentPair.getDepth(),urlPool);
                        }
                    }
                    socket.close();
                } catch (SocketTimeoutException e) {
                    socket.close();
                }
            }
            catch (IOException e) {}
        }
    }
}