import java.io.*;
import java.net.*;
import java.util.Arrays;

public class MasterNode {
    public void startMaster() {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Master Node is running...");
            Socket worker = serverSocket.accept();
            System.out.println("Worker connected: " + worker.getInetAddress());

            int[] arrayChunk = {5, 2, 9, 1, 6};
            ObjectOutputStream out = new ObjectOutputStream(worker.getOutputStream());
            out.writeObject(arrayChunk);

            ObjectInputStream in = new ObjectInputStream(worker.getInputStream());
            int[] sortedChunk = (int[]) in.readObject();
            System.out.println("Sorted Chunk Received: " + Arrays.toString(sortedChunk));

            worker.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
