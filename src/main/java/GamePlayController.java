import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GamePlayController {

    // ===== Card ImageViews =====
    @FXML private ImageView playerCard1;
    @FXML private ImageView playerCard2;
    @FXML private ImageView playerCard3;
    @FXML private ImageView dealerCard1;
    @FXML private ImageView dealerCard2;
    @FXML private ImageView dealerCard3;

    // Card Text Labels
    @FXML private Label playerCard1Text;
    @FXML private Label playerCard2Text;
    @FXML private Label playerCard3Text;
    @FXML private Label dealerCard1Text;
    @FXML private Label dealerCard2Text;
    @FXML private Label dealerCard3Text;

    // ===== Labels =====
    @FXML private Label anteBetLabel;
    @FXML private Label pairPlusBetLabel;
    @FXML private Label playBetLabel;
    @FXML private Label totalWinningsLabel;

    @FXML private Label anteBetLabelBottom;
    @FXML private Label pairPlusBetLabelBottom;

    // ===== Sliders =====
    @FXML private Slider anteBetSlider;
    @FXML private Slider pairPlusBetSlider;

    // ===== Buttons =====
    @FXML private Button dealButton;
    @FXML private Button foldButton;
    @FXML private Button playButton;
    @FXML private Button continueButton;

    // ===== Message Boxes =====
    @FXML private TextArea gameInfoTextArea;
    @FXML private TextArea messageBox;

    // Reference to main app
    private ProjectThreeClient mainApp;

    private int totalWinnings = 0;

    // ⭐ New — controls second-continue behavior
    private boolean readyForResultScreen = false;

    // ===================== INITIALIZATION =====================
    public void initialize() {

        anteBetSlider.setMin(5);
        anteBetSlider.setMax(25);
        anteBetSlider.setValue(5);
        anteBetSlider.setShowTickLabels(true);
        anteBetSlider.setShowTickMarks(true);
        anteBetSlider.setMajorTickUnit(5);

        pairPlusBetSlider.setMin(0);
        pairPlusBetSlider.setMax(25);
        pairPlusBetSlider.setValue(0);
        pairPlusBetSlider.setShowTickLabels(true);
        pairPlusBetSlider.setShowTickMarks(true);
        pairPlusBetSlider.setMajorTickUnit(5);

        anteBetSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int amt = newVal.intValue();
            anteBetLabel.setText("$" + amt);
            anteBetLabelBottom.setText("$" + amt);
        });

        pairPlusBetSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int amt = newVal.intValue();
            pairPlusBetLabel.setText("$" + amt);
            pairPlusBetLabelBottom.setText("$" + amt);
        });

        anteBetLabel.setText("$5");
        anteBetLabelBottom.setText("$5");
        pairPlusBetLabel.setText("$0");
        pairPlusBetLabelBottom.setText("$0");
        playBetLabel.setText("$0");
        totalWinningsLabel.setText("$0");

        enablePlayFoldControls(false);
        continueButton.setDisable(true);
    }

    // ===================== MENU =====================
    @FXML
    private void handleExit() {
        Stage stage = (Stage) gameInfoTextArea.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleFreshStart() {
        totalWinnings = 0;
        totalWinningsLabel.setText("$0");
        resetGameUI();

        if (mainApp != null) {
            mainApp.setCurrentTheme(ProjectThreeClient.ThemeType.GREEN);
        }

        gameInfoTextArea.appendText("=== FRESH START ===\n");
        gameInfoTextArea.appendText("Total winnings reset to $0\n");
    }

    @FXML
    private void handleNewLook() {
        if (mainApp != null) {
            mainApp.setCurrentTheme(ProjectThreeClient.ThemeType.BLUE);
        }
        gameInfoTextArea.appendText("[Theme changed to Blue/Purple]\n");
    }

    // ===================== BETTING =====================
    @FXML
    private void handleDealButton() {
        int anteBet = (int) anteBetSlider.getValue();
        int ppBet = (int) pairPlusBetSlider.getValue();

        PokerInfo info = new PokerInfo("PLACE_BETS");
        info.setAnteBet(anteBet);
        info.setPairPlusBet(ppBet);

        mainApp.getNetworkHandler().sendPokerInfo(info);
        gameInfoTextArea.appendText("Bets placed: Ante $" + anteBet + ", Pair Plus $" + ppBet + "\n");

        enableBettingControls(false);
    }

    @FXML
    private void handlePlayButton() {
        int anteBet = (int) anteBetSlider.getValue();
        PokerInfo info = new PokerInfo("PLAY");
        info.setPlayBet(anteBet);

        mainApp.getNetworkHandler().sendPokerInfo(info);
        playBetLabel.setText("$" + anteBet);

        gameInfoTextArea.appendText("You chose PLAY. Waiting for dealer...\n");
        enablePlayFoldControls(false);
    }

    @FXML
    private void handleFoldButton() {
        PokerInfo info = new PokerInfo("FOLD");
        mainApp.getNetworkHandler().sendPokerInfo(info);

        gameInfoTextArea.appendText("You folded.\n");
        enablePlayFoldControls(false);
    }

    // ===================== CONTINUE BUTTON LOGIC =====================
    @FXML
    private void handleContinueButton() {
        if (!readyForResultScreen) {
            // FIRST Continue → ask server for GAME_RESULT
            PokerInfo info = new PokerInfo("CONTINUE");
            mainApp.getNetworkHandler().sendPokerInfo(info);
            continueButton.setDisable(true);
            gameInfoTextArea.appendText("Calculating results...\n");

        } else {
            // SECOND Continue → go to win/lose screen
            mainApp.switchToScene("result");
        }
    }

    // ===================== UPDATE UI FROM SERVER =====================
    public void updateGUI(PokerInfo info) {
        Platform.runLater(() -> {

            switch (info.getMessageType()) {

                case "DEAL_CARDS":
                    displayPlayerCards(info.getPlayerHand());
                    hideDealerCards();
                    showGameMessage("CARDS DEALT - Choose PLAY or FOLD");
                    enablePlayFoldControls(true);
                    continueButton.setDisable(true);
                    break;

                case "SHOW_DEALER":
                    displayDealerCards(info.getDealerHand());
                    showGameMessage(info.getGameMessage());
                    continueButton.setDisable(false);
                    break;

                case "GAME_RESULT":
                    processGameResult(info);

                    // ⭐ Now allow second continue click
                    readyForResultScreen = true;
                    continueButton.setDisable(false);
                    continueButton.setText("FINISH");
                    break;

                case "ROUND_COMPLETE":
                    // Optional use — not required for gameplay
                    break;
            }
        });
    }

    // ===================== PROCESS RESULTS =====================
    private void processGameResult(PokerInfo info) {
        int roundWinnings = info.getTotalWinnings();
        String resultMessage = info.getGameMessage();

        totalWinnings += roundWinnings;
        totalWinningsLabel.setText("$" + totalWinnings);

        showGameMessage("=== ROUND RESULT ===");
        showGameMessage(resultMessage);

        if (roundWinnings > 0) showGameMessage("Winnings: +$" + roundWinnings);
        else if (roundWinnings < 0) showGameMessage("Loss: -$" + Math.abs(roundWinnings));
        else showGameMessage("No change this round.");

        showGameMessage("Updated total: $" + totalWinnings);

        // Pass result data to ResultController for final screen
        mainApp.getResultController().setGameResult(resultMessage, totalWinnings);
    }

    // ===================== MESSAGE BOX =====================
    private void showGameMessage(String message) {
        messageBox.setOpacity(1);
        messageBox.appendText(message + "\n");
    }

    // ===================== CARD DISPLAY =====================
    private void displayPlayerCards(ArrayList<Card> cards) {
        updateCardDisplay(playerCard1, playerCard1Text, cards.get(0), true);
        updateCardDisplay(playerCard2, playerCard2Text, cards.get(1), true);
        updateCardDisplay(playerCard3, playerCard3Text, cards.get(2), true);
    }

    private void displayDealerCards(ArrayList<Card> cards) {
        updateCardDisplay(dealerCard1, dealerCard1Text, cards.get(0), true);
        updateCardDisplay(dealerCard2, dealerCard2Text, cards.get(1), true);
        updateCardDisplay(dealerCard3, dealerCard3Text, cards.get(2), true);
    }

    private void hideDealerCards() {
        updateCardDisplay(dealerCard1, dealerCard1Text, null, false);
        updateCardDisplay(dealerCard2, dealerCard2Text, null, false);
        updateCardDisplay(dealerCard3, dealerCard3Text, null, false);
    }

    private void updateCardDisplay(ImageView view, Label label, Card card, boolean faceUp) {
        if (card == null || !faceUp) {
            view.setImage(new Image("/card_back.png"));
            label.setText("Face Down");
        } else {
            view.setImage(new Image("/card_back.png"));
            label.setText(card.toString());
        }
    }

    // ===================== STATE RESET =====================
    public void resetGameUI() {
        enableBettingControls(true);
        enablePlayFoldControls(false);

        readyForResultScreen = false;
        continueButton.setText("CONTINUE");
        messageBox.clear();

        playerCard1.setImage(null);
        playerCard2.setImage(null);
        playerCard3.setImage(null);
        dealerCard1.setImage(null);
        dealerCard2.setImage(null);
        dealerCard3.setImage(null);

        playBetLabel.setText("$0");
        messageBox.setOpacity(1);
    }

    // ===================== CONTROL HELPERS =====================
    public void enableBettingControls(boolean enable) {
        anteBetSlider.setDisable(!enable);
        pairPlusBetSlider.setDisable(!enable);
        dealButton.setDisable(!enable);
    }

    public void enablePlayFoldControls(boolean enable) {
        foldButton.setDisable(!enable);
        playButton.setDisable(!enable);
        if (!enable) continueButton.setDisable(true);
    }

    public void setMainApp(ProjectThreeClient mainApp) {
        this.mainApp = mainApp;
    }
}
