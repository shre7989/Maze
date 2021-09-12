/**
 * @project - Maze
 * @course - CS351
 * @group - 2
 */
package maze;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;


public class Board extends Scene  {
    /* Our maze object and timelines for visualizing maze solving and generation */
    private Maze maze;
    private Timeline animatorG;
    private Timeline animatorS;
    private int rowNum;
    private int colNum;
    private int size;

    /**
     * Board - constructor that constructs our board
     * @param root - layout
     * @param width - width of the board
     * @param height - height of the board
     */
    public Board(Parent root, int width, int height,String fileConfig){
        super(root,width,height);
        String config = "res/" + fileConfig;
        ArrayList<String> data = decideGenAndSol(config);
        this.maze = new Maze(rowNum,colNum,size,data);
        this.animatorG = maze.getAnimationG();
        this.animatorS = maze.getAnimationS();
        VBox layout = (VBox) root;
        setup(layout);
    }

    /**
     * setup - sets up the GUI for our board
     * @param layout - layout of the main scene
     */
    public void setup(VBox layout){

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button generate = new Button("GENERATE");
        generate.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px");
        generate.setOnAction(actionEvent -> {
            animatorG.play();
        });

        Button solve = new Button("SOLVE");
        solve.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px");
        solve.setOnAction(actionEvent -> {
            animatorS.play();
        });

        Button pause = new Button("PAUSE");
        pause.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px");
        pause.setOnAction(actionEvent -> {
            animatorG.pause();
        });

        Button exit = new Button("EXIT");
        exit.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px");
        exit.setOnAction(actionEvent -> {
            Platform.exit();
        });

        buttonBox.getChildren().addAll(generate,solve,pause,exit);
        layout.getChildren().add(maze);
        layout.getChildren().add(buttonBox);
    }

    public ArrayList<String> decideGenAndSol(String path){
        System.out.println("The path is: " + path);
        Path paths = Paths.get(path);
        ArrayList<String> data = new ArrayList<>();
        int count = 0;
        try {
            Scanner scan = new Scanner(paths);
            while (scan.hasNextLine()) {
                String word;
                word = scan.nextLine();
                if(count == 0){
                    size = Integer.parseInt(word);
                }
                else if(count == 1){
                    int cellSize = Integer.parseInt(word);
                    rowNum = size/cellSize;
                    colNum = size/cellSize;
                }
                else data.add(word);
                count++;
            }
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
