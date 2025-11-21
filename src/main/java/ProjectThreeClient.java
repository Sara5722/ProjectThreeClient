import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ProjectThreeClient extends Application {
    private Stage primaryStage;
    private WelcomeController welcomeController;
    private GamePlayController gamePlayController;
    private ResultController resultController;

    // Scenes
    private Scene welcomeScene;
    private Scene gameScene;
    private Scene resultScene;

    // Network connection
    private ClientNetworkHandler networkHandler;

    public ProjectThreeClient() throws IOException {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.networkHandler = new ClientNetworkHandler(this);

        // Load all scenes
        loadWelcomeScene();
        loadGameScene();
        loadResultScene();

        // Set larger window size for horizontal layout
        primaryStage.setTitle("3-Card Poker Client");
        primaryStage.setScene(welcomeScene);
        primaryStage.setWidth(1000);   // Wider for horizontal layout
        primaryStage.setHeight(750);   // Slightly taller
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    public enum ThemeType {
        GREEN,
        BLUE
    }

    private ThemeType currentTheme = ThemeType.GREEN;

    public ThemeType getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(ThemeType theme) {
        this.currentTheme = theme;
    }



    private void loadWelcomeScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcome.fxml"));
        Parent root = loader.load();
        welcomeController = loader.getController();
        welcomeController.setMainApp(this);
        welcomeScene = new Scene(root, 600, 400);
    }

    private void loadGameScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameplay.fxml"));
        Parent root = loader.load();
        gamePlayController = loader.getController();
        gamePlayController.setMainApp(this);
        gameScene = new Scene(root, 800, 600);
    }

    private void loadResultScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/result.fxml"));
        Parent root = loader.load();
        resultController = loader.getController();
        resultController.setMainApp(this);
        resultScene = new Scene(root, 500, 300);
    }

    // Method to switch between scenes
    public void switchToScene(String sceneName) {
        switch(sceneName) {
            case "welcome":
                primaryStage.setScene(welcomeScene);
                break;
            case "game":
                primaryStage.setScene(gameScene);
                gamePlayController.resetGameUI();
                break;
            case "result":
                primaryStage.setScene(resultScene);
                break;
        }
    }

    // Network methods
    public ClientNetworkHandler getNetworkHandler() {
        return networkHandler;
    }

    // Get controllers for network handler
    public GamePlayController getGamePlayController() {
        return gamePlayController;
    }

    public ResultController getResultController() {
        return resultController;
    }

    public static void main(String[] args) {
        // Simple test to see if it runs
        System.out.println("Application starting...");

        // Check if we have all classes
        try {
            new Card(Suit.HEARTS, 1);
            new PokerInfo();
            new WelcomeController();
            new GamePlayController();
            new ResultController();
            System.out.println("All classes loaded successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // If all classes exist, launch JavaFX
        launch(args);
    }

    public void handleServerMessage(PokerInfo info) {
        System.out.println("Processing server message: " + info.getMessageType());

        // Route message to appropriate controller
        switch(info.getMessageType()) {
            case "DEAL_CARDS":
                if (gamePlayController != null) {
                    gamePlayController.updateGUI(info);
                }
                break;
            case "SHOW_DEALER":
                if (gamePlayController != null) {
                    gamePlayController.updateGUI(info);
                }
                break;
            case "GAME_RESULT":
                if (resultController != null) {
                    resultController.setGameResult(info.getGameMessage(), info.getTotalWinnings());
                    switchToScene("result");
                }
                break;
            default:
                System.out.println("Unknown message type: " + info.getMessageType());
        }
    }

}
