package game;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Minesweeperapp extends Application {

    private Game game;
    private User user;
    private Button[][] buttons;
    private Label flagLabel;
    private Label timerLabel;
    private Label statusLabel;
    private Timeline timer;
    private long secondsElapsed;

    private static final String BG_DARK      = "#0d0d0d";
    private static final String BG_PANEL     = "#1a1a1a";
    private static final String CELL_HIDDEN  = "#2a2a2a";
    private static final String CELL_HOVER   = "#3a3a3a";
    private static final String CELL_CLICKED = "#111111";
    private static final String CELL_BOMB    = "#8b0000";
    private static final String BORDER_COLOR = "#444444";
    private static final String ACCENT       = "#c8a400";
    private static final String TEXT_MAIN    = "#e8e8e8";
    private static final String TEXT_DIM     = "#888888";
    private static final String[] NUM_COLORS = {
        "", "#5b9bd5", "#4caf50", "#e74c3c",
        "#9b59b6", "#e67e22", "#1abc9c", "#e8e8e8", "#888888"
    };

    @Override
    public void start(Stage primaryStage) {
        user = new User(1, "Player");
        showUsernameDialog(primaryStage);
    }

    private void showUsernameDialog(Stage primaryStage) {
        Stage dialog = new Stage();
        dialog.setTitle("Minesweeper");
        dialog.setResizable(false);

        VBox root = new VBox(18);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

        Label title = new Label("MINESWEEPER");
        title.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + ACCENT + ";");

        Label sub = new Label("Enter your name to begin");
        sub.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px; -fx-text-fill: " + TEXT_DIM + ";");

        TextField nameField = new TextField("Player");
        nameField.setMaxWidth(200);
        nameField.setStyle(
            "-fx-background-color: " + BG_PANEL + ";" +
            "-fx-text-fill: " + TEXT_MAIN + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-radius: 4;" +
            "-fx-background-radius: 4;" +
            "-fx-font-family: 'Courier New';" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 8 12;"
        );

        Button startBtn = styledButton("START GAME", ACCENT, BG_DARK);
        startBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) user = new User(1, name);
            dialog.close();
            showGame(primaryStage);
        });

        nameField.setOnAction(e -> startBtn.fire());
        root.getChildren().addAll(title, sub, nameField, startBtn);
        Scene scene = new Scene(root, 380, 260);
        dialog.setScene(scene);
        dialog.show();
    }

    private void showGame(Stage stage) {
        game = new Game();
        int size = game.getBoardSize();
        buttons = new Button[size][size];
        secondsElapsed = 0;

        HBox hud = new HBox(30);
        hud.setAlignment(Pos.CENTER);
        hud.setPadding(new Insets(14, 20, 14, 20));
        hud.setStyle("-fx-background-color: " + BG_PANEL + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 0 1 0;");

        flagLabel  = hudLabel("Flags: " + game.getFlagLimit());
        timerLabel = hudLabel("Time: 0s");
        statusLabel = new Label(user.getUsername());
        statusLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14px; -fx-text-fill: " + ACCENT + "; -fx-font-weight: bold;");

        Button newGameBtn = styledButton("NEW GAME", ACCENT, BG_DARK);
        newGameBtn.setOnAction(e -> restartGame(stage));

        hud.getChildren().addAll(flagLabel, statusLabel, timerLabel, newGameBtn);

        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: " + BG_DARK + ";");
        grid.setAlignment(Pos.CENTER);

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Button btn = createCellButton(r, c);
                buttons[r][c] = btn;
                grid.add(btn, c, r);
            }
        }

        HBox statsBar = new HBox(30);
        statsBar.setAlignment(Pos.CENTER);
        statsBar.setPadding(new Insets(10));
        statsBar.setStyle("-fx-background-color: " + BG_PANEL + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1 0 0 0;");

        Label statsLabel = new Label(getStatsText());
        statsLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; -fx-text-fill: " + TEXT_DIM + ";");
        statsBar.getChildren().add(statsLabel);

        BorderPane root = new BorderPane();
        root.setTop(hud);
        root.setCenter(grid);
        root.setBottom(statsBar);
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

        Scene scene = new Scene(root);
        scene.setFill(Color.web(BG_DARK));
        stage.setTitle("Minesweeper");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        startTimer(statsLabel);
    }

    private Button createCellButton(int row, int col) {
        Button btn = new Button();
        btn.setPrefSize(38, 38);
        btn.setMinSize(38, 38);
        btn.setMaxSize(38, 38);
        btn.setStyle(cellStyle(false, false, false, false));
        btn.setFont(Font.font("Courier New", FontWeight.BOLD, 13));

        btn.setOnMouseClicked(e -> {
            if (game.getGameOver()) return;
            if (e.getButton() == MouseButton.PRIMARY) {
                handleLeftClick(row, col);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                handleRightClick(row, col);
            }
        });

        btn.setOnMouseEntered(e -> {
            if (!game.isCellClicked(row, col) && !game.getGameOver())
                btn.setStyle(cellStyle(false, true, false, false));
        });
        btn.setOnMouseExited(e -> {
            if (!game.isCellClicked(row, col))
                btn.setStyle(cellStyle(game.isCellFlagged(row, col), false, false, false));
        });

        return btn;
    }

    private void handleLeftClick(int row, int col) {
        boolean hitBomb = game.clickCell(row, col);
        updateAllCells();
        if (hitBomb) {
            stopTimer();
            user.recordGame(false, secondsElapsed);
            showOverlay("BOOM! Game Over", false);
        } else if (game.getWon()) {
            stopTimer();
            user.recordGame(true, secondsElapsed);
            showOverlay("You Win! Time: " + secondsElapsed + "s", true);
        }
        updateHUD();
    }

    private void handleRightClick(int row, int col) {
        game.toggleFlag(row, col);
        updateCell(row, col);
        updateHUD();
    }

    private void updateAllCells() {
        int size = game.getBoardSize();
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                updateCell(r, c);
    }

    private void updateCell(int row, int col) {
        Button btn = buttons[row][col];
        boolean clicked = game.isCellClicked(row, col);
        boolean flagged = game.isCellFlagged(row, col);
        int val = game.getCellValue(row, col);

        if (clicked) {
            if (val == 9) {
                btn.setText("*");
                btn.setStyle(cellStyle(false, false, true, false));
            } else if (val == 0) {
                btn.setText("");
                btn.setStyle(cellStyle(false, false, false, true));
            } else {
                btn.setText(String.valueOf(val));
                btn.setStyle(cellStyle(false, false, false, true) +
                    "-fx-text-fill: " + NUM_COLORS[val] + ";");
            }
        } else if (flagged) {
            btn.setText("F");
            btn.setStyle(cellStyle(true, false, false, false));
        } else {
            btn.setText("");
            btn.setStyle(cellStyle(false, false, false, false));
        }
    }

    private void updateHUD() {
        flagLabel.setText("Flags: " + game.getFlagLimit());
    }

    private void startTimer(Label statsLabel) {
        if (timer != null) timer.stop();
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            timerLabel.setText("Time: " + secondsElapsed + "s");
            statsLabel.setText(getStatsText());
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void stopTimer() {
        if (timer != null) timer.stop();
    }

    private void showOverlay(String message, boolean won) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(won ? "You Won!" : "Game Over");
        alert.setHeaderText(message);
        alert.setContentText(
            "Games played: " + user.getGamesPlayed() + "\n" +
            "Games won: " + user.getGamesWon() + "\n" +
            "Win rate: " + user.getWinRate() + "\n" +
            "Total score: " + user.getTotalScore() + "\n" +
            "Best time: " + user.getBestTimeString()
        );
        alert.showAndWait();
    }

    private void restartGame(Stage stage) {
        stopTimer();
        showGame(stage);
    }

    private String getStatsText() {
        return String.format(
            "%s  |  Played: %d  |  Won: %d  |  Win Rate: %s  |  Best: %s  |  Score: %d",
            user.getUsername(), user.getGamesPlayed(), user.getGamesWon(),
            user.getWinRate(), user.getBestTimeString(), user.getTotalScore()
        );
    }

    private String cellStyle(boolean flagged, boolean hovered, boolean bomb, boolean revealed) {
        String bg = revealed ? CELL_CLICKED :
                    bomb     ? CELL_BOMB    :
                    hovered  ? CELL_HOVER   :
                    flagged  ? "#1e2a1e"    :
                               CELL_HIDDEN;
        String border = flagged ? "#4caf50" : bomb ? "#ff4444" : BORDER_COLOR;
        return  "-fx-background-color: " + bg + ";" +
                "-fx-border-color: " + border + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 3;" +
                "-fx-background-radius: 3;" +
                "-fx-cursor: hand;" +
                "-fx-text-fill: " + TEXT_MAIN + ";";
    }

    private Label hudLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MAIN + ";");
        return l;
    }

    private Button styledButton(String text, String fg, String bg) {
        Button b = new Button(text);
        String baseStyle =
            "-fx-background-color: " + fg + ";" +
            "-fx-text-fill: " + bg + ";" +
            "-fx-font-family: 'Courier New';" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 7 16;" +
            "-fx-background-radius: 4;" +
            "-fx-cursor: hand;";
        b.setStyle(baseStyle);
        b.setOnMouseEntered(e -> b.setStyle(baseStyle.replace(fg, "derive(" + fg + ", -15%)")));
        b.setOnMouseExited(e -> b.setStyle(baseStyle));
        return b;
    }

    public static void main(String[] args) {
        launch(args);
    }
}