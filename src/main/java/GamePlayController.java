import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.util.ArrayList;

public class GamePlayController {

    //Card ImageViews
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

    // Sliders
    @FXML private Slider anteBetSlider;
    @FXML private Slider pairPlusBetSlider;

    //  Buttons
    @FXML private Button dealButton;
    @FXML private Button foldButton;
    @FXML private Button playButton;
    @FXML private Button continueButton;

    // message box
    @FXML private TextArea messageBox;

    // Reference to main app
    private ProjectThreeClient mainApp;

    private int totalWinnings = 0;
    private boolean readyForResultScreen = false;

    // initialize
    public void initialize() {
        // Slider setup
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

        // Slider listeners
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

        // Initial values
        anteBetLabel.setText("$5");
        anteBetLabelBottom.setText("$5");
        pairPlusBetLabel.setText("$0");
        pairPlusBetLabelBottom.setText("$0");
        playBetLabel.setText("$0");
        totalWinningsLabel.setText("$0");

        enablePlayFoldControls(false);
        continueButton.setDisable(true);

        // Initialize message box
        showGameMessage("Welcome! Place your bets and click DEAL to start.");
    }

    // menu
    @FXML
    private void handleExit() {
        Stage stage = (Stage) messageBox.getScene().getWindow();
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

        showGameMessage("=== FRESH START ===");
        showGameMessage("Total winnings reset to $0");
    }

    @FXML
    private void handleNewLook() {
        if (mainApp != null) {
            mainApp.setCurrentTheme(ProjectThreeClient.ThemeType.BLUE);
        }
        showGameMessage("Theme changed to Blue/Purple");
    }

    // betting
    @FXML
    private void handleDealButton() {
        int anteBet = (int) anteBetSlider.getValue();
        int ppBet = (int) pairPlusBetSlider.getValue();

        PokerInfo info = new PokerInfo("PLACE_BETS");
        info.setAnteBet(anteBet);
        info.setPairPlusBet(ppBet);

        mainApp.getNetworkHandler().sendPokerInfo(info);
        showGameMessage("Bets placed: Ante $" + anteBet + ", Pair Plus $" + ppBet);

        enableBettingControls(false);
    }

    @FXML
    private void handlePlayButton() {
        int anteBet = (int) anteBetSlider.getValue();
        PokerInfo info = new PokerInfo("PLAY");
        info.setPlayBet(anteBet);

        mainApp.getNetworkHandler().sendPokerInfo(info);
        playBetLabel.setText("$" + anteBet);

        showGameMessage("You chose PLAY. Waiting for dealer...");
        enablePlayFoldControls(false);
    }

    @FXML
    private void handleFoldButton() {
        PokerInfo info = new PokerInfo("FOLD");
        mainApp.getNetworkHandler().sendPokerInfo(info);

        showGameMessage("You folded.");
        enablePlayFoldControls(false);
    }

    // continue betting logic
    @FXML
    private void handleContinueButton() {
        if (!readyForResultScreen) {
            // FIRST Continue → ask server for GAME_RESULT
            PokerInfo info = new PokerInfo("CONTINUE");
            mainApp.getNetworkHandler().sendPokerInfo(info);
            continueButton.setDisable(true);
            showGameMessage("Calculating results...");
        } else {
            // SECOND Continue → go to win/lose screen
            mainApp.switchToScene("result");
        }
    }

    // update UI from server
    public void updateGUI(PokerInfo info) {
        Platform.runLater(() -> {
            switch (info.getMessageType()) {
                case "DEAL_CARDS":
                    displayPlayerCards(info.getPlayerHand());
                    hideDealerCards();
                    showGameMessage("=== CARDS DEALT ===");
                    showGameMessage("Your hand: " + getHandEvaluation(info.getPlayerHand()));
                    showGameMessage("Choose PLAY to continue or FOLD to surrender");
                    enablePlayFoldControls(true);
                    continueButton.setDisable(true);
                    break;

                case "SHOW_DEALER":
                    displayDealerCards(info.getDealerHand());
                    showGameMessage("=== DEALER'S CARDS REVEALED ===");
                    showGameMessage("Dealer hand: " + getHandEvaluation(info.getDealerHand()));
                    showGameMessage(info.getGameMessage()); // Shows dealer qualification
                    showGameMessage("Click CONTINUE to see results");
                    continueButton.setDisable(false);
                    break;

                case "GAME_RESULT":
                    processGameResult(info);
                    // Allow second continue click
                    readyForResultScreen = true;
                    continueButton.setDisable(false);
                    continueButton.setText("FINISH");
                    break;

                case "ROUND_COMPLETE":
                    // Optional use
                    break;
            }
        });
    }

    // process results
    private void processGameResult(PokerInfo info) {
        int roundWinnings = info.getTotalWinnings();
        String resultMessage = info.getGameMessage();

        totalWinnings += roundWinnings;
        totalWinningsLabel.setText("$" + totalWinnings);

        showGameMessage("=== ROUND RESULT ===");

        // Display specific outcome messages as REQUIRED by the project
        if (resultMessage.contains("folded")) {
            showGameMessage("You folded and lost your Ante and Pair Plus bets");
        } else if (resultMessage.contains("does not have at least Queen high")) {
            showGameMessage("Dealer does not have at least Queen high; ante wager is pushed");
            if (resultMessage.contains("Pair Plus")) {
                showGameMessage("You win Pair Plus bet");
            } else if (resultMessage.contains("lose Pair Plus")) {
                showGameMessage("You lose Pair Plus bet");
            }
        } else if (resultMessage.contains("beat the dealer")) {
            showGameMessage("You beat the dealer!");
            if (resultMessage.contains("Pair Plus")) {
                showGameMessage("You win Pair Plus bet");
            } else if (resultMessage.contains("lose Pair Plus")) {
                showGameMessage("You lose Pair Plus bet");
            }
        } else if (resultMessage.contains("lose to dealer")) {
            showGameMessage("You lose to dealer");
            if (resultMessage.contains("Pair Plus")) {
                showGameMessage("You win Pair Plus bet");
            } else if (resultMessage.contains("lose Pair Plus")) {
                showGameMessage("You lose Pair Plus bet");
            }
        } else if (resultMessage.contains("Push")) {
            showGameMessage("Push - it's a tie");
            if (resultMessage.contains("Pair Plus")) {
                showGameMessage("You win Pair Plus bet");
            } else if (resultMessage.contains("lose Pair Plus")) {
                showGameMessage("You lose Pair Plus bet");
            }
        }

        // Show net winnings
        if (roundWinnings > 0) {
            showGameMessage("Net winnings: +$" + roundWinnings);
        } else if (roundWinnings < 0) {
            showGameMessage("Net loss: -$" + Math.abs(roundWinnings));
        } else {
            showGameMessage("No money won or lost this round");
        }

        showGameMessage("Total winnings: $" + totalWinnings);

        // Pass result data to ResultController for final screen
        mainApp.getResultController().setGameResult(resultMessage, totalWinnings);
    }

    // single message system
    private void showGameMessage(String message) {
        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        messageBox.appendText("[" + timestamp + "] " + message + "\n");
        // Auto-scroll to bottom
        messageBox.setScrollTop(Double.MAX_VALUE);
    }

    private void clearGameMessages() {
        messageBox.clear();
    }

    // card display
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

    // handle evaluation
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

    // state reset
    public void resetGameUI() {
        enableBettingControls(true);
        enablePlayFoldControls(false);

        readyForResultScreen = false;
        continueButton.setText("CONTINUE");
        clearGameMessages();

        playerCard1.setImage(null);
        playerCard2.setImage(null);
        playerCard3.setImage(null);
        dealerCard1.setImage(null);
        dealerCard2.setImage(null);
        dealerCard3.setImage(null);

        // Reset card text
        playerCard1Text.setText("");
        playerCard2Text.setText("");
        playerCard3Text.setText("");
        dealerCard1Text.setText("");
        dealerCard2Text.setText("");
        dealerCard3Text.setText("");

        playBetLabel.setText("$0");

        showGameMessage("=== NEW ROUND ===");
        showGameMessage("Current total winnings: $" + totalWinnings);
        showGameMessage("Place your ante and pair plus bets, then click DEAL");
    }

    // control helpers
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