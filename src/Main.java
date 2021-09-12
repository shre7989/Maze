/**
 * @project - Maze
 * @course - CS351
 * @group - 2
 */
package maze;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        VBox layout = new VBox(20);
        Parameters parameters = getParameters();
        List<String> args = parameters.getRaw();
        layout.setAlignment(Pos.CENTER);
        Board mazeBoard = new Board(layout,1200,1000, args.get(0));
        System.out.println("Hello");
        primaryStage.setTitle("Maze");
        primaryStage.setScene(mazeBoard);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);

    }

}
