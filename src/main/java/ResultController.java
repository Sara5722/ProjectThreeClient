import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ResultController {
    @FXML private Label resultMessageLabel;
    @FXML private Label winningsLabel;
    @FXML private Button playAgainButton;
    @FXML private Button exitButton;

    private ProjectThreeClient mainApp;

    @FXML
    private void handlePlayAgain() {
        // Notify server we're starting a new game
        PokerInfo newGameInfo = new PokerInfo("NEW_GAME");
        mainApp.getNetworkHandler().sendPokerInfo(newGameInfo);

        // Reset UI and go back to gameplay
        mainApp.getGamePlayController().resetGameUI();
        mainApp.switchToScene("game");
    }

    @FXML
    private void handleExit() {
        mainApp.getNetworkHandler().disconnect();
        System.exit(0);
    }

    public void setGameResult(String message, int winnings) {
        resultMessageLabel.setText(message);

        if (winnings >= 0) {
            winningsLabel.setText("You won: $" + winnings);
            winningsLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            winningsLabel.setText("You lost: $" + Math.abs(winnings));
            winningsLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
    }

    public void setMainApp(ProjectThreeClient mainApp) {
        this.mainApp = mainApp;
    }
}