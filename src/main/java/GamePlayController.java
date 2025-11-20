//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import java.util.ArrayList;
//
//public class GamePlayController {
//    @FXML private ImageView playerCard1;
//    @FXML private ImageView playerCard2;
//    @FXML private ImageView playerCard3;
//    @FXML private ImageView dealerCard1;
//    @FXML private ImageView dealerCard2;
//    @FXML private ImageView dealerCard3;
//
//    @FXML private Label anteBetLabel;
//    @FXML private Label pairPlusBetLabel;
//    @FXML private Label playBetLabel;
//    @FXML private Label totalWinningsLabel;
//
//    @FXML private Label anteBetLabelBottom;  // For the bottom display
//    @FXML private Label pairPlusBetLabelBottom;  // For the bottom display
//
//    @FXML private Slider anteBetSlider;
//    @FXML private Slider pairPlusBetSlider;
//
//    @FXML private Button dealButton;
//    @FXML private Button foldButton;
//    @FXML private Button playButton;
//
//    @FXML private TextArea gameInfoTextArea;
//
//    private ProjectThreeClient mainApp;
//    private int totalWinnings = 0;
//
//    public void initialize() {
//        // Setup sliders
//        anteBetSlider.setMin(5);
//        anteBetSlider.setMax(25);
//        anteBetSlider.setValue(5);
//        anteBetSlider.setShowTickLabels(true);
//        anteBetSlider.setShowTickMarks(true);
//        anteBetSlider.setMajorTickUnit(5);
//        anteBetSlider.setSnapToTicks(true);
//
//        pairPlusBetSlider.setMin(0);
//        pairPlusBetSlider.setMax(25);
//        pairPlusBetSlider.setValue(0);
//        pairPlusBetSlider.setShowTickLabels(true);
//        pairPlusBetSlider.setShowTickMarks(true);
//        pairPlusBetSlider.setMajorTickUnit(5);
//        pairPlusBetSlider.setSnapToTicks(true);
//
//        // Update labels when sliders change
//        anteBetSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
//            int value = newVal.intValue();
//            anteBetLabel.setText("Ante: $" + value);
//            anteBetLabelBottom.setText("Ante: $" + value);
//        });
//
//        pairPlusBetSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
//            int value = newVal.intValue();
//            pairPlusBetLabel.setText("Pair Plus: $" + value);
//            pairPlusBetLabelBottom.setText("Pair Plus: $" + value);
//        });
//
//        // Initial label values
//        anteBetLabel.setText("Ante: $5");
//        anteBetLabelBottom.setText("Ante: $5");
//        pairPlusBetLabel.setText("Pair Plus: $0");
//        pairPlusBetLabelBottom.setText("Pair Plus: $0");
//        playBetLabel.setText("Play: $0");
//        totalWinningsLabel.setText("Total winnings: $0");
//
//        // Initially disable play/fold buttons
//        enablePlayFoldControls(false);
//    }
//
//    @FXML
//    private void handleDealButton() {
//        int anteBet = (int) anteBetSlider.getValue();
//        int pairPlusBet = (int) pairPlusBetSlider.getValue();
//
//        // Send bet information to server
//        PokerInfo info = new PokerInfo("PLACE_BETS");
//        info.setAnteBet(anteBet);
//        info.setPairPlusBet(pairPlusBet);
//
//        mainApp.getNetworkHandler().sendPokerInfo(info);
//
//        // Update UI
//        gameInfoTextArea.appendText("Bets placed: Ante $" + anteBet + ", Pair Plus $" + pairPlusBet + "\n");
//        enableBettingControls(false);
//    }
//
//    @FXML
//    private void handleFoldButton() {
//        PokerInfo info = new PokerInfo("FOLD");
//        mainApp.getNetworkHandler().sendPokerInfo(info);
//        gameInfoTextArea.appendText("You folded.\n");
//
//        // Go to result screen
//        mainApp.getResultController().setGameResult("You folded and lost your bets.", -getTotalBetAmount());
//        mainApp.switchToScene("result");
//    }
//
//    @FXML
//    private void handlePlayButton() {
//        int anteBet = (int) anteBetSlider.getValue();
//        PokerInfo info = new PokerInfo("PLAY");
//        info.setPlayBet(anteBet); // Play wager equals ante bet
//
//        mainApp.getNetworkHandler().sendPokerInfo(info);
//        gameInfoTextArea.appendText("Play wager placed: $" + anteBet + "\n");
//
//        playBetLabel.setText("Play: $" + anteBet);
//        enablePlayFoldControls(false);
//    }
//
//    public void updateGUI(PokerInfo info) {
//        if ("DEAL_CARDS".equals(info.getMessageType())) {
//            // Display player cards
//            displayCards(info.getPlayerHand(), info.getDealerHand());
//
//            // Show dealer cards face down initially
//            dealerCard1.setImage(new Image("/card_back.png"));
//            dealerCard2.setImage(new Image("/card_back.png"));
//            dealerCard3.setImage(new Image("/card_back.png"));
//
//            gameInfoTextArea.appendText("Cards dealt. Choose to Play or Fold.\n");
//            enablePlayFoldControls(true);
//
//        } else if ("SHOW_DEALER".equals(info.getMessageType())) {
//            // Show dealer cards face up
//            displayDealerCards(info.getDealerHand());
//            gameInfoTextArea.appendText("Dealer's cards revealed.\n");
//        }
//    }
//
//    private void displayCards(ArrayList<Card> playerHand, ArrayList<Card> dealerHand) {
//        // For now, just show text representation
//        // You'll replace this with actual card images later
//        if (playerHand.size() >= 3) {
//            playerCard1.setImage(getCardImage(playerHand.get(0)));
//            playerCard2.setImage(getCardImage(playerHand.get(1)));
//            playerCard3.setImage(getCardImage(playerHand.get(2)));
//        }
//    }
//
//    private void displayDealerCards(ArrayList<Card> dealerHand) {
//        if (dealerHand.size() >= 3) {
//            dealerCard1.setImage(getCardImage(dealerHand.get(0)));
//            dealerCard2.setImage(getCardImage(dealerHand.get(1)));
//            dealerCard3.setImage(getCardImage(dealerHand.get(2)));
//        }
//    }
//
//    private Image getCardImage(Card card) {
//        // Placeholder - you'll need actual card images
//        // For now, use a generic card back image
//        try {
//            return new Image("/card_back.png");
//        } catch (Exception e) {
//            // Fallback - create a colored rectangle
//            return null;
//        }
//    }
//
//    public void resetGameUI() {
//        enableBettingControls(true);
//        enablePlayFoldControls(false);
//
//        // Reset card displays
//        playerCard1.setImage(null);
//        playerCard2.setImage(null);
//        playerCard3.setImage(null);
//        dealerCard1.setImage(null);
//        dealerCard2.setImage(null);
//        dealerCard3.setImage(null);
//
//        playBetLabel.setText("Play: $0");
//        gameInfoTextArea.clear();
//        gameInfoTextArea.appendText("Place your bets...\n");
//    }
//
//    public void enableBettingControls(boolean enable) {
//        anteBetSlider.setDisable(!enable);
//        pairPlusBetSlider.setDisable(!enable);
//        dealButton.setDisable(!enable);
//    }
//
//    public void enablePlayFoldControls(boolean enable) {
//        foldButton.setDisable(!enable);
//        playButton.setDisable(!enable);
//    }
//
//    public void setMainApp(ProjectThreeClient mainApp) {
//        this.mainApp = mainApp;
//    }
//
//    private int getTotalBetAmount() {
//        return (int) (anteBetSlider.getValue() + pairPlusBetSlider.getValue());
//    }
//
//    public void updateGameInfo(String message) {
//        gameInfoTextArea.appendText(message + "\n");
//    }
//}

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

    // ===== Game Info Text Box =====
    @FXML private TextArea gameInfoTextArea;

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

        // ---- Update labels when slider moves ----
        anteBetSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int amt = newVal.intValue();
            anteBetLabel.setText("Ante: $" + amt);
            anteBetLabelBottom.setText("Ante: $" + amt);
        });

        pairPlusBetSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int amt = newVal.intValue();
            pairPlusBetLabel.setText("Pair Plus: $" + amt);
            pairPlusBetLabelBottom.setText("Pair Plus: $" + amt);
        });

        // ---- Default text ----
        anteBetLabel.setText("Ante: $5");
        anteBetLabelBottom.setText("Ante: $5");
        pairPlusBetLabel.setText("Pair Plus: $0");
        pairPlusBetLabelBottom.setText("Pair Plus: $0");

        playBetLabel.setText("Play: $0");
        totalWinningsLabel.setText("Total winnings: $0");

        enablePlayFoldControls(false);
    }

    // ===================== MENU BAR HANDLERS (NEW) =====================

    @FXML
    private void handleExit() {
        Stage stage = (Stage) gameInfoTextArea.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleFreshStart() {
        totalWinnings = 0;
        totalWinningsLabel.setText("Total winnings: $0");
        resetGameUI();
    }

    @FXML
    private void handleNewLook() {
        gameInfoTextArea.setStyle("-fx-control-inner-background: #F0D7FF;");
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

        gameInfoTextArea.appendText("You folded.\n");

        mainApp.getResultController().setGameResult(
                "You folded and lost your bets.",
                -getTotalBetAmount()
        );

        mainApp.switchToScene("result");
    }

    // ===================== PLAY BUTTON =====================
    @FXML
    private void handlePlayButton() {
        int anteBet = (int) anteBetSlider.getValue();

        PokerInfo info = new PokerInfo("PLAY");
        info.setPlayBet(anteBet);

        mainApp.getNetworkHandler().sendPokerInfo(info);

        playBetLabel.setText("Play: $" + anteBet);
        gameInfoTextArea.appendText("Play wager placed: $" + anteBet + "\n");

        enablePlayFoldControls(false);
    }

    // ===================== UPDATE UI FROM SERVER =====================
    public void updateGUI(PokerInfo info) {

        switch (info.getMessageType()) {

            case "DEAL_CARDS":
                displayPlayerCards(info.getPlayerHand());
                hideDealerCards();
                gameInfoTextArea.appendText("Cards dealt. Choose Play or Fold.\n");
                enablePlayFoldControls(true);
                break;

            case "SHOW_DEALER":
                displayDealerCards(info.getDealerHand());
                gameInfoTextArea.appendText("Dealer reveals cards.\n");
                break;

            case "RESULT":
                totalWinnings += info.getTotalWinnings();
                totalWinningsLabel.setText("Total winnings: $" + totalWinnings);
                break;
        }
    }

    // ===================== DISPLAY FUNCTIONS =====================

    private void displayPlayerCards(ArrayList<Card> cards) {
        playerCard1.setImage(getCardImage(cards.get(0)));
        playerCard2.setImage(getCardImage(cards.get(1)));
        playerCard3.setImage(getCardImage(cards.get(2)));
    }

    private void hideDealerCards() {
        Image back = new Image("/card_back.png");
        dealerCard1.setImage(back);
        dealerCard2.setImage(back);
        dealerCard3.setImage(back);
    }

    private void displayDealerCards(ArrayList<Card> cards) {
        dealerCard1.setImage(getCardImage(cards.get(0)));
        dealerCard2.setImage(getCardImage(cards.get(1)));
        dealerCard3.setImage(getCardImage(cards.get(2)));
    }

    private Image getCardImage(Card card) {
        // Example: return new Image("/cards/" + card.toString() + ".png");
        return new Image("/card_back.png");  // placeholder
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
    }

    // ===================== RESET BETWEEN HANDS =====================
    public void resetGameUI() {

        enableBettingControls(true);
        enablePlayFoldControls(false);

        playerCard1.setImage(null);
        playerCard2.setImage(null);
        playerCard3.setImage(null);

        dealerCard1.setImage(null);
        dealerCard2.setImage(null);
        dealerCard3.setImage(null);

        playBetLabel.setText("Play: $0");
        gameInfoTextArea.clear();
        gameInfoTextArea.appendText("Place your bets...\n");
    }

    // ===================== GETTERS/SETTERS =====================
    public void setMainApp(ProjectThreeClient mainApp) {
        this.mainApp = mainApp;
    }

    private int getTotalBetAmount() {
        return (int) anteBetSlider.getValue() + (int) pairPlusBetSlider.getValue();
    }

    public void updateGameInfo(String message) {
        gameInfoTextArea.appendText(message + "\n");
    }
}
