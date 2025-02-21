import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class NodeManagerGUI extends Application {
    private final MasterNode masterNode = new MasterNode();
    private final ListView<String> workerListView = new ListView<>();
    private final TextArea statusTextArea = new TextArea();
    private List<Integer> data;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Distributed Sorting System");

        new Thread(masterNode).start();

        Button uploadButton = new Button("Upload File");
        Button sortDistributedButton = new Button("Sort with Worker Nodes");
        Button refreshButton = new Button("Refresh Workers");

        uploadButton.setOnAction(e -> uploadFile(primaryStage));
        sortDistributedButton.setOnAction(e -> triggerSortDistributed());
        refreshButton.setOnAction(e -> refreshWorkerList());

        VBox centerBox = new VBox(10, new Label("Connected Workers:"), workerListView, new Label("Status:"), statusTextArea);
        HBox buttonBox = new HBox(10, uploadButton, sortDistributedButton, refreshButton);

        BorderPane root = new BorderPane();
        root.setTop(buttonBox);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshWorkerList();
    }

    private void uploadFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                data = reader.lines()
                        .flatMap(line -> Arrays.stream(line.split("\\s+")))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                statusTextArea.appendText("File uploaded: " + file.getName() + "\n");
            } catch (IOException e) {
                statusTextArea.appendText("Error reading file: " + e.getMessage() + "\n");
            }
        } else {
            statusTextArea.appendText("No file selected.\n");
        }
    }

    private void triggerSortDistributed() {
        if (data == null || data.isEmpty()) {
            statusTextArea.appendText("No data to sort. Please upload a file first.\n");
            return;
        }

        List<Integer> dataToSort = new ArrayList<>(data);

        long startTime = System.currentTimeMillis();
        try {
            List<Integer> sortedData = masterNode.sortAndMergeData(dataToSort);
            long endTime = System.currentTimeMillis();

            statusTextArea.appendText("Sorted Data (Distributed): " + sortedData + "\n");
            statusTextArea.appendText("Time taken: " + (endTime - startTime) + " ms\n");

            File outputFile = new File("distributed_sorted_result.txt");
            try (PrintWriter writer = new PrintWriter(outputFile)) {
                for (int num : sortedData) {
                    writer.println(num);
                }
                statusTextArea.appendText("Result written to: " + outputFile.getAbsolutePath() + "\n");
            } catch (FileNotFoundException e) {
                statusTextArea.appendText("Error writing to file: " + e.getMessage() + "\n");
            }
        } catch (Exception e) {
            statusTextArea.appendText("Error during distributed sorting: " + e.getMessage() + "\n");
        }
    }

    private void refreshWorkerList() {
        data = null;

        List<String> workerIPs = masterNode.getWorkerIPs();

        statusTextArea.clear();
        statusTextArea.appendText("Refreshing worker list...\n");

        for (String workerIP : workerIPs) {
            new Thread(() -> {
                boolean isOnline = checkWorkerHeartbeat(workerIP);
                Platform.runLater(() -> {
                    if (isOnline) {
                        workerListView.getItems().add(workerIP + " (Self)");
                    } else {
                        workerListView.getItems().add(workerIP);
                    }
                });
            }).start();
        }

        statusTextArea.appendText("Worker list refreshed.\n");
    }

    private boolean checkWorkerHeartbeat(String workerIP) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(workerIP, 5000), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
