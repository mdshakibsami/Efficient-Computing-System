import java.io.*;
import java.net.*;
import java.util.Arrays;

public class WorkerNode {
    public static void main(String[] args) {
        try (Socket master = new Socket("localhost", 5000)) {
            ObjectInputStream in = new ObjectInputStream(master.getInputStream());
            int[] arrayChunk = (int[]) in.readObject();
            System.out.println("Received Chunk: " + Arrays.toString(arrayChunk));

            Arrays.sort(arrayChunk);

            ObjectOutputStream out = new ObjectOutputStream(master.getOutputStream());
            out.writeObject(arrayChunk);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
