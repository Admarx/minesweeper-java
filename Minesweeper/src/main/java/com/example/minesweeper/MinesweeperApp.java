package com.example.minesweeper;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class MinesweeperApp extends Application {

    private static final int TILE_SIZE = 40; // Tile size in pixels
    private static final int W = 800; // Application width
    private static final int H = 600; // Application height

    private static final int X_TILES = W/TILE_SIZE; // Amount of tiles on the X axis
    private static final int Y_TILES = H/TILE_SIZE; // Amount of tiles on the Y axis
    private static final double BOMB_DISTRIBUTION = 0.2; // Amount of bombs in the range from 0 to 1 (0 = 0% bombs, 1 = 100% bombs)

    private Tile[][] grid = new Tile[X_TILES][Y_TILES]; // We create a field for our game
    private Scene scene;

    private Parent createContent()
    {
        Pane root = new Pane();
        root.setPrefSize(W,H);
        // This nested loop generates our minefield
            for(int y = 0; y < Y_TILES; y++) {
                for (int x = 0; x < X_TILES; x++) {
                    Tile tile = new Tile(x, y, Math.random() < BOMB_DISTRIBUTION);

                    grid[x][y] = tile;
                    root.getChildren().add(tile);
                }
            }
        // This nested loop determines how many bombs are there in the neighboring tiles and sets it as text.
            for(int y = 0; y < Y_TILES; y++) {
                for (int x = 0; x < X_TILES; x++) {
                    Tile tile = grid[x][y];

                    if(tile.hasBomb)
                        continue;

                    long bombs = getNeighbors(tile).stream().filter(t -> t.hasBomb).count();

                    if(bombs > 0)
                        tile.text.setText(String.valueOf(bombs));
                }
            }

        return root;
    }

    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();

        // Visualisation of neighboring fields - helps understand points array
        // ttt
        // tXt
        // ttt

        int[] points = new int[] {
            -1, -1,
            -1, 0,
            -1, 1,
            0, -1,
            0, 1,
            1, -1,
            1, 0,
            1, 1
        };

        // This loop counts amount of bombs in the vicinity of the neighbour
        for (int i = 0; i < points.length; i++) {
            int dx = points[i];
            int dy = points[++i];

            int newX = tile.x + dx;
            int newY = tile.y + dy;

            if (newX >= 0 && newX < X_TILES && newY >= 0 && newY < Y_TILES){
                neighbors.add(grid[newX][newY]);
            }
        }

        return neighbors;
    }

    private class Tile extends StackPane {
        private int x,y;
        private boolean hasBomb;
        private boolean isOpen = false;
        private Rectangle border = new Rectangle(TILE_SIZE -2, TILE_SIZE -2);
        private Text text = new Text();

        public Tile(int x, int y, boolean hasBomb)
        {
            this.x = x;
            this.y = y;
            this.hasBomb = hasBomb;

            border.setFill(Color.BLACK);
            border.setStroke(Color.LIGHTGRAY);

            text.setFont(Font.font(18));
            text.setText(hasBomb? "X" : "");
            text.setVisible(false);

            getChildren().addAll(border, text);

            setTranslateX(x * TILE_SIZE);
            setTranslateY(y * TILE_SIZE);

            setOnMouseClicked(e -> open());
        }

        public void open()
        {
            if(isOpen)
                return;

            if (hasBomb) {

                Alert alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle("GAME OVER");
                alert.setContentText("You have tripped a mine");
                alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                alert.showAndWait();

                System.out.println("Game Over");
                scene.setRoot(createContent());
                return;
            }

            isOpen = true;
            text.setVisible(true);
            border.setFill(null);

            if (text.getText().isEmpty()) {
                getNeighbors(this).forEach(Tile::open);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = new Scene (createContent());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }

}
