import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ResultController {

    @FXML private Label resultMessageLabel;
    @FXML private Label winningsLabel;
    @FXML private Button playAgainButton;
    @FXML private Button exitButton;

    private ProjectThreeClient mainApp;

    // apply new theme
    private void applyTheme() {
        if (resultMessageLabel == null || resultMessageLabel.getScene() == null) return;

        var root = resultMessageLabel.getScene().getRoot();

        root.getStyleClass().remove("root-green");
        root.getStyleClass().remove("root-blue");

        if (mainApp.getCurrentTheme() == ProjectThreeClient.ThemeType.BLUE) {
            if (!root.getStyleClass().contains("root-blue")) {
                root.getStyleClass().add("root-blue");
            }
        } else {
            if (!root.getStyleClass().contains("root-green")) {
                root.getStyleClass().add("root-green");
            }
        }
    }

    @FXML
    private void handlePlayAgain() {
        PokerInfo newGameInfo = new PokerInfo("NEW_GAME");
        mainApp.getNetworkHandler().sendPokerInfo(newGameInfo);

        mainApp.getGamePlayController().resetGameUI();
        mainApp.switchToScene("game");
    }

    @FXML
    private void handleExit() {
        mainApp.getNetworkHandler().disconnect();
        System.exit(0);
    }

    public void setGameResult(String message, int winnings) {
        Platform.runLater(() -> {

            applyTheme();

            resultMessageLabel.setText(message);

            if (winnings >= 0) {
                winningsLabel.setText("You won: $" + winnings);
                winningsLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                winningsLabel.setText("You lost: $" + Math.abs(winnings));
                winningsLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        });
    }

    public void setMainApp(ProjectThreeClient mainApp) {
        this.mainApp = mainApp;

        // Apply theme when controller attaches to main app
        Platform.runLater(this::applyTheme);
    }
}
