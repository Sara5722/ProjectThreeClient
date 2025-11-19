import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;


public class WelcomeController {
    @FXML private TextField ipAddressField;
    @FXML private TextField portField;
    @FXML private Button connectButton;
    @FXML private Label statusLabel;

    private ProjectThreeClient mainApp;

    public void initialize() {
        // Set default values
        ipAddressField.setText("localhost");
        portField.setText("5555");
    }

    @FXML
    private void handleConnectButton() {
        if (validateInput()) {
            String ip = ipAddressField.getText();
            int port = Integer.parseInt(portField.getText());

            statusLabel.setText("Connecting...");
            connectButton.setDisable(true);

            // Connect on a separate thread to avoid blocking UI
            new Thread(() -> {
                boolean success = mainApp.getNetworkHandler().connectToServer(ip, port);

                javafx.application.Platform.runLater(() -> {
                    if (success) {
                        statusLabel.setText("Connected successfully!");
                        mainApp.switchToScene("game");
                    } else {
                        statusLabel.setText("Connection failed!");
                        connectButton.setDisable(false);
                    }
                });
            }).start();
        }
    }

    private boolean validateInput() {
        String ip = ipAddressField.getText();
        String portText = portField.getText();

        if (ip.isEmpty()) {
            statusLabel.setText("Please enter IP address");
            return false;
        }

        try {
            int port = Integer.parseInt(portText);
            if (port < 1 || port > 65535) {
                statusLabel.setText("Port must be between 1-65535");
                return false;
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter a valid port number");
            return false;
        }

        return true;
    }



    public void setMainApp(ProjectThreeClient mainApp) {
        this.mainApp = mainApp;
    }

    public void updateStatus(String message) {
        statusLabel.setText(message);
    }
}


