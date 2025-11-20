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

        anteBetSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int amt = newVal.intValue();
            anteBetLabel.setText("$" + amt);           // Changed to compact format
            anteBetLabelBottom.setText("$" + amt);     // Changed to compact format
        });

        pairPlusBetSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int amt = newVal.intValue();
            pairPlusBetLabel.setText("$" + amt);       // Changed to compact format
            pairPlusBetLabelBottom.setText("$" + amt); // Changed to compact format
        });

        // Default text - COMPACT VERSION
        anteBetLabel.setText("$5");
        anteBetLabelBottom.setText("$5");
        pairPlusBetLabel.setText("$0");
        pairPlusBetLabelBottom.setText("$0");
        playBetLabel.setText("$0");
        totalWinningsLabel.setText("$0");

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
        System.out.println("Received message: " + info.getMessageType());

        switch (info.getMessageType()) {
            case "DEAL_CARDS":
                displayPlayerCards(info.getPlayerHand());
                hideDealerCards();
                gameInfoTextArea.appendText("=== 🎰 CARDS DEALT ===\n");
                displayEnhancedCardInfo(info.getPlayerHand(), info.getDealerHand(), false);
                gameInfoTextArea.appendText("➡️ Choose PLAY to continue or FOLD to surrender.\n");
                enablePlayFoldControls(true);
                break;

            case "SHOW_DEALER":
                gameInfoTextArea.appendText("🃏 Revealing dealer's cards...\n");

                new Thread(() -> {
                    try {
                        Thread.sleep(1500); // 1.5 second delay for suspense
                        javafx.application.Platform.runLater(() -> {
                            if (info.getDealerHand() != null) {
                                // Ensure dealer cards are face up
                                for (Card card : info.getDealerHand()) {
                                    card.setFaceUp(true);
                                }
                                displayDealerCards(info.getDealerHand());
                                gameInfoTextArea.appendText("=== 🃏 DEALER'S CARDS REVEALED ===\n");
                                displayEnhancedCardInfo(null, info.getDealerHand(), true);

                                // Show dealer's hand strength
                                if (info.getDealerHand().size() == 3) {
                                    int dealerHandRank = ThreeCardLogic.evalHand(info.getDealerHand());
                                    String dealerHandType = getHandTypeName(dealerHandRank);
                                    boolean dealerQualifies = ThreeCardLogic.dealerQualifies(info.getDealerHand());
                                    gameInfoTextArea.appendText("📊 DEALER'S HAND: " + dealerHandType + "\n");
                                    gameInfoTextArea.appendText("🎯 Dealer qualifies: " + (dealerQualifies ? "YES ✅" : "NO ❌") + "\n");
                                }

                                // Wait before results
                                new Thread(() -> {
                                    try {
                                        Thread.sleep(2500); // 2.5 seconds to study dealer's hand
                                        javafx.application.Platform.runLater(() -> {
                                            gameInfoTextArea.appendText("⏳ Calculating results...\n");
                                        });
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }).start();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;

            case "GAME_RESULT":
                if (info.getTotalWinnings() != 0) {
                    totalWinnings += info.getTotalWinnings();
                    totalWinningsLabel.setText("Total winnings: $" + totalWinnings);

                    String resultMessage = info.getGameMessage();
                    gameInfoTextArea.appendText("=== 🏆 GAME RESULT ===\n");
                    gameInfoTextArea.appendText(resultMessage + "\n");

                    if (info.getTotalWinnings() > 0) {
                        gameInfoTextArea.appendText("💰 You won: $" + info.getTotalWinnings() + "\n");
                    } else if (info.getTotalWinnings() < 0) {
                        gameInfoTextArea.appendText("💸 You lost: $" + Math.abs(info.getTotalWinnings()) + "\n");
                    } else {
                        gameInfoTextArea.appendText("➖ Push! No money won or lost.\n");
                    }

                    gameInfoTextArea.appendText("💵 New total: $" + totalWinnings + "\n");

                    new Thread(() -> {
                        try {
                            Thread.sleep(4000); // 4 seconds to read results
                            javafx.application.Platform.runLater(() -> {
                                mainApp.getResultController().setGameResult(resultMessage, info.getTotalWinnings());
                                mainApp.switchToScene("result");
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
                break;
        }
    }

    // ===================== IMPROVED CARD DISPLAY =====================

    private void displayPlayerCards(ArrayList<Card> cards) {
        if (cards == null || cards.size() < 3) return;

        playerCard1.setImage(createCardImage(cards.get(0)));
        playerCard2.setImage(createCardImage(cards.get(1)));
        playerCard3.setImage(createCardImage(cards.get(2)));
    }

    private void displayDealerCards(ArrayList<Card> cards) {
        if (cards == null || cards.size() < 3) return;

        dealerCard1.setImage(createCardImage(cards.get(0)));
        dealerCard2.setImage(createCardImage(cards.get(1)));
        dealerCard3.setImage(createCardImage(cards.get(2)));
    }

    private void hideDealerCards() {
        dealerCard1.setImage(createCardBackImage());
        dealerCard2.setImage(createCardBackImage());
        dealerCard3.setImage(createCardBackImage());
    }

    private Image createCardImage(Card card) {
        if (card == null) {
            return createCardBackImage();
        }

        if (!card.isFaceUp()) {
            return createCardBackImage();
        }

        // Create a simple text-based card image
        try {
            // For now, we'll use a colored rectangle with text
            // In a real implementation, you'd use actual card images
            return createTextCardImage(card);
        } catch (Exception e) {
            System.err.println("Error creating card image: " + e.getMessage());
            return createCardBackImage();
        }
    }

    private Image createTextCardImage(Card card) {
        // This is a placeholder - in a real app you'd generate an actual image
        // For now, we'll use the card back but we need a better solution
        return createCardBackImage();
    }

    private Image createCardBackImage() {
        try {
            return new Image("/card_back.png");
        } catch (Exception e) {
            // Fallback: create a simple colored rectangle
            return null;
        }
    }

    // Temporary solution: Enhance the text display in game info
    private void displayEnhancedCardInfo(ArrayList<Card> playerHand, ArrayList<Card> dealerHand, boolean showDealerCards) {
        StringBuilder info = new StringBuilder();

        if (playerHand != null && !playerHand.isEmpty()) {
            info.append("🎴 YOUR CARDS: ");
            for (int i = 0; i < playerHand.size(); i++) {
                Card card = playerHand.get(i);
                info.append("\n   Card ").append(i + 1).append(": ").append(card.toString());
            }
            info.append("\n");
        }

        if (dealerHand != null && !dealerHand.isEmpty()) {
            info.append("🎴 DEALER'S CARDS: ");
            for (int i = 0; i < dealerHand.size(); i++) {
                Card card = dealerHand.get(i);
                if (showDealerCards || card.isFaceUp()) {
                    info.append("\n   Card ").append(i + 1).append(": ").append(card.toString());
                } else {
                    info.append("\n   Card ").append(i + 1).append(": [Face Down]");
                }
            }
            info.append("\n");
        }

        // Calculate and show hand strength
        if (playerHand != null && playerHand.size() == 3) {
            int handRank = ThreeCardLogic.evalHand(playerHand);
            String handType = getHandTypeName(handRank);
            info.append("📊 YOUR HAND: ").append(handType).append("\n");
        }

        gameInfoTextArea.appendText(info.toString() + "\n");
    }

    private String getHandTypeName(int handRank) {
        switch (handRank) {
            case ThreeCardLogic.STRAIGHT_FLUSH: return "STRAIGHT FLUSH 🎯";
            case ThreeCardLogic.THREE_OF_A_KIND: return "THREE OF A KIND 🔥";
            case ThreeCardLogic.STRAIGHT: return "STRAIGHT 📈";
            case ThreeCardLogic.FLUSH: return "FLUSH 💧";
            case ThreeCardLogic.PAIR: return "PAIR 👥";
            default: return "HIGH CARD 📝";
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
