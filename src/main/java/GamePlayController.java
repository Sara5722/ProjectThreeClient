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

    // Card Text Label Fields
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

    // ===== Game Info Text Box =====
    @FXML private TextArea gameInfoTextArea;
    @FXML private TextArea messageBox;

    // ===== Reference to main application =====
    private ProjectThreeClient mainApp;

    private int totalWinnings = 0;

    // ===================== INITIALIZATION =====================
    public void initialize() {

        // ---- Setup sliders ----
        anteBetSlider.setMin(5);
        anteBetSlider.setMax(25);
        anteBetSlider.setValue(5);
        anteBetSlider.setShowTickLabels(true);
        anteBetSlider.setShowTickMarks(true);
        anteBetSlider.setMajorTickUnit(5);
        anteBetSlider.setSnapToTicks(true);

        pairPlusBetSlider.setMin(0);
        pairPlusBetSlider.setMax(25);
        pairPlusBetSlider.setValue(0);
        pairPlusBetSlider.setShowTickLabels(true);
        pairPlusBetSlider.setShowTickMarks(true);
        pairPlusBetSlider.setMajorTickUnit(5);
        pairPlusBetSlider.setSnapToTicks(true);

        // ---- Update labels when slider moves (compact format: "$X") ----
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

        // Default text - COMPACT VERSION
        anteBetLabel.setText("$5");
        anteBetLabelBottom.setText("$5");
        pairPlusBetLabel.setText("$0");
        pairPlusBetLabelBottom.setText("$0");
        playBetLabel.setText("$0");
        totalWinningsLabel.setText("$0");

        enablePlayFoldControls(false);
        continueButton.setDisable(true);
    }

    // ===================== MENU BAR HANDLERS =====================

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

        gameInfoTextArea.appendText("[Theme changed to Blue/Purple Retro]\n");
    }

    // ===================== DEAL BUTTON =====================
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

    // ===================== FOLD BUTTON =====================
    @FXML
    private void handleFoldButton() {
        PokerInfo info = new PokerInfo("FOLD");
        mainApp.getNetworkHandler().sendPokerInfo(info);

        gameInfoTextArea.appendText("You chose to FOLD.\n");
        enablePlayFoldControls(false);
        // Wait for server response
    }

    // ===================== PLAY BUTTON =====================
    @FXML
    private void handlePlayButton() {
        int anteBet = (int) anteBetSlider.getValue();
        PokerInfo info = new PokerInfo("PLAY");
        info.setPlayBet(anteBet);

        mainApp.getNetworkHandler().sendPokerInfo(info);

        playBetLabel.setText("$" + anteBet);
        gameInfoTextArea.appendText("You chose to PLAY. Waiting for dealer...\n");
        enablePlayFoldControls(false);
        // Wait for server response
    }

    // ===================== CONTINUE BUTTON =====================
    @FXML
    private void handleContinueButton() {
        PokerInfo info = new PokerInfo("CONTINUE");
        mainApp.getNetworkHandler().sendPokerInfo(info);
        continueButton.setDisable(true);
        gameInfoTextArea.appendText("Calculating results...\n");
    }

    // ===================== UPDATE UI FROM SERVER =====================
    public void updateGUI(PokerInfo info) {
        // This might be called from a non-JavaFX thread, so wrap the logic.
        Platform.runLater(() -> {
            if (info == null) return;

            System.out.println("Received message: " + info.getMessageType());

            switch (info.getMessageType()) {
                case "DEAL_CARDS":
                    displayPlayerCards(info.getPlayerHand());
                    hideDealerCards();
                    showGameMessage("CARDS DEALT - Choose PLAY or FOLD");
                    showInfoMessage("Your hand: " + getHandEvaluation(info.getPlayerHand()));
                    enablePlayFoldControls(true);
                    continueButton.setDisable(true);
                    break;

                case "SHOW_DEALER":
                    if (info.getDealerHand() != null) {
                        for (Card card : info.getDealerHand()) {
                            card.setFaceUp(true);
                        }
                        displayDealerCards(info.getDealerHand());
                        showGameMessage("DEALER'S CARDS REVEALED - Click CONTINUE for results");
                        showInfoMessage("Dealer hand: " + getHandEvaluation(info.getDealerHand()));
                        showInfoMessage(info.getGameMessage()); // e.g., dealer qualification
                        continueButton.setDisable(false);
                    }
                    break;

                case "GAME_RESULT":
                    processGameResult(info);
                    continueButton.setDisable(true);
                    break;

                case "ROUND_COMPLETE":
                    if (info.getTotalWinnings() != 0 && mainApp != null) {
                        mainApp.getResultController().setGameResult(info.getGameMessage(), info.getTotalWinnings());
                        mainApp.switchToScene("result");
                    }
                    break;
            }
        });
    }

    private String getHandEvaluation(ArrayList<Card> hand) {
        if (hand == null || hand.size() != 3) return "Unknown";

        int handRank = ThreeCardLogic.evalHand(hand);
        switch (handRank) {
            case ThreeCardLogic.STRAIGHT_FLUSH: return "STRAIGHT FLUSH";
            case ThreeCardLogic.THREE_OF_A_KIND: return "THREE OF A KIND";
            case ThreeCardLogic.STRAIGHT: return "STRAIGHT";
            case ThreeCardLogic.FLUSH: return "FLUSH";
            case ThreeCardLogic.PAIR: return "PAIR";
            default: return "HIGH CARD";
        }
    }

    // New method to process game results without switching scenes directly
    private void processGameResult(PokerInfo info) {
        int roundWinnings = info.getTotalWinnings();
        String resultMessage = info.getGameMessage();

        totalWinnings += roundWinnings;
        totalWinningsLabel.setText("$" + totalWinnings);

        gameInfoTextArea.appendText("=== GAME RESULT ===\n");
        gameInfoTextArea.appendText(resultMessage + "\n");

        if (resultMessage != null) {
            if (resultMessage.contains("won") && resultMessage.contains("Pair Plus")) {
                gameInfoTextArea.appendText("🎉 You won both main game and Pair Plus!\n");
            } else if (resultMessage.contains("won") && !resultMessage.contains("Pair Plus")) {
                gameInfoTextArea.appendText("✅ You won the main game!\n");
            } else if (resultMessage.contains("lost") && resultMessage.contains("Pair Plus")) {
                gameInfoTextArea.appendText("❌ You lost both main game and Pair Plus\n");
            } else if (resultMessage.contains("lost") && !resultMessage.contains("Pair Plus")) {
                gameInfoTextArea.appendText("⚠️ You lost the main game but Pair Plus may have won\n");
            } else if (resultMessage.toLowerCase().contains("push")) {
                gameInfoTextArea.appendText("➖ Push - no money won or lost\n");
            }
        }

        if (roundWinnings > 0) {
            gameInfoTextArea.appendText("💰 Net winnings: +$" + roundWinnings + "\n");
        } else if (roundWinnings < 0) {
            gameInfoTextArea.appendText("💸 Net loss: -$" + Math.abs(roundWinnings) + "\n");
        } else {
            gameInfoTextArea.appendText("No change in chips this round.\n");
        }

        gameInfoTextArea.appendText("💵 Updated total: $" + totalWinnings + "\n");
    }

    // ===================== CARD DISPLAY METHODS =====================

    private void displayPlayerCards(ArrayList<Card> cards) {
        if (cards != null && cards.size() >= 3) {
            updateCardDisplay(playerCard1, playerCard1Text, cards.get(0), true);
            updateCardDisplay(playerCard2, playerCard2Text, cards.get(1), true);
            updateCardDisplay(playerCard3, playerCard3Text, cards.get(2), true);
        }
    }

    private void displayDealerCards(ArrayList<Card> cards) {
        if (cards != null && cards.size() >= 3) {
            updateCardDisplay(dealerCard1, dealerCard1Text, cards.get(0), true);
            updateCardDisplay(dealerCard2, dealerCard2Text, cards.get(1), true);
            updateCardDisplay(dealerCard3, dealerCard3Text, cards.get(2), true);
        }
    }

    private void hideDealerCards() {
        updateCardDisplay(dealerCard1, dealerCard1Text, null, false);
        updateCardDisplay(dealerCard2, dealerCard2Text, null, false);
        updateCardDisplay(dealerCard3, dealerCard3Text, null, false);
    }

    private void updateCardDisplay(ImageView cardImage, Label cardText, Card card, boolean faceUp) {
        try {
            if (card == null || !faceUp) {
                // Show card back
                cardImage.setImage(new Image("/card_back.png"));
                cardText.setText("Face Down");
                cardText.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
            } else {
                // For now, use card_back.png for all faces and show text label
                cardImage.setImage(new Image("/card_back.png"));
                cardText.setText(card.toString());
                cardText.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            }
        } catch (Exception e) {
            System.err.println("Error loading card image: " + e.getMessage());
            cardImage.setImage(null);
            cardText.setText("Error");
        }
    }

    // ===================== CONTROL TOGGLERS =====================

    public void enableBettingControls(boolean enable) {
        anteBetSlider.setDisable(!enable);
        pairPlusBetSlider.setDisable(!enable);
        dealButton.setDisable(!enable);
    }

    public void enablePlayFoldControls(boolean enable) {
        foldButton.setDisable(!enable);
        playButton.setDisable(!enable);
        // Continue is only enabled when dealer has been shown
        if (!enable) {
            continueButton.setDisable(true);
        }
    }

    // ===================== MESSAGE DISPLAY METHODS =====================

    private void showGameMessage(String message) {
        messageBox.setText(message);
        // Text color styling for TextArea content is done via CSS; this sets the control's style
        messageBox.setStyle("-fx-text-fill: #FFD700;");
    }

    private void showInfoMessage(String message) {
        gameInfoTextArea.appendText(message + "\n");
    }

    // ===================== RESET BETWEEN HANDS =====================
    public void resetGameUI() {
        enableBettingControls(true);
        enablePlayFoldControls(false);

        // Reset card displays but keep total winnings
        playerCard1.setImage(null);
        playerCard2.setImage(null);
        playerCard3.setImage(null);
        dealerCard1.setImage(null);
        dealerCard2.setImage(null);
        dealerCard3.setImage(null);

        playerCard1Text.setText("");
        playerCard2Text.setText("");
        playerCard3Text.setText("");
        dealerCard1Text.setText("");
        dealerCard2Text.setText("");
        dealerCard3Text.setText("");

        // Reset current bets but keep total winnings
        playBetLabel.setText("$0");
        anteBetLabel.setText("$5");
        anteBetLabelBottom.setText("$5");
        pairPlusBetLabel.setText("$0");
        pairPlusBetLabelBottom.setText("$0");

        // Clear game info but show current total
        gameInfoTextArea.clear();
        gameInfoTextArea.appendText("=== NEW ROUND ===\n");
        gameInfoTextArea.appendText("Current total winnings: $" + totalWinnings + "\n");
        gameInfoTextArea.appendText("Place your bets...\n");

        // Reset sliders to default
        anteBetSlider.setValue(5);
        pairPlusBetSlider.setValue(0);

        showGameMessage("Place your bets and click DEAL.");
    }

    // ===================== GETTERS/SETTERS =====================

    public void setMainApp(ProjectThreeClient mainApp) {
        this.mainApp = mainApp;
    }

    @SuppressWarnings("unused")
    private int getTotalBetAmount() {
        return (int) anteBetSlider.getValue() + (int) pairPlusBetSlider.getValue();
    }

    public void updateGameInfo(String message) {
        gameInfoTextArea.appendText(message + "\n");
    }
}
