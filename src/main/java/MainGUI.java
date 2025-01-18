import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainGUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label title = new Label("Distributed Sorting Controller");

        Button singleSortButton = new Button("Sort on Single Machine");
        Button multiSortButton = new Button("Sort on Multiple Machines");

        singleSortButton.setOnAction(e -> sortOnSingleMachine());
        multiSortButton.setOnAction(e -> sortOnMultipleMachines());

        VBox layout = new VBox(10, title, singleSortButton, multiSortButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-spacing: 10;");

        Scene scene = new Scene(layout, 400, 200);
        primaryStage.setTitle("Distributed Sorting");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sortOnSingleMachine() {
//        System.out.println("Sorting on a single machine...");
        SingleMachineSort sorter = new SingleMachineSort();
        sorter.sortAndMeasure();

    }

    private void sortOnMultipleMachines() {
//        System.out.println("Sorting on multiple machines...");
        MasterNode master = new MasterNode();
        master.startMaster();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
