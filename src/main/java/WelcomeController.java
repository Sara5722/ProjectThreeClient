import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
        statusLabel.setText("");
    }

    @FXML
    private void handleConnectButton() {
        if (mainApp == null) {
            statusLabel.setText("Internal error: main app not set.");
            return;
        }

        if (validateInput()) {
            String ip = ipAddressField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());

            statusLabel.setText("Connecting...");
            connectButton.setDisable(true);

            // Connect on a separate thread to avoid blocking UI
            new Thread(() -> {
                boolean success = mainApp.getNetworkHandler().connectToServer(ip, port);

                Platform.runLater(() -> {
                    if (success) {
                        statusLabel.setText("Connected successfully!");
                        mainApp.switchToScene("game");
                    } else {
                        statusLabel.setText("Connection failed! Check IP/port and server.");
                        connectButton.setDisable(false);
                    }
                });
            }).start();
        }
    }

    private boolean validateInput() {
        String ip = ipAddressField.getText();
        String portText = portField.getText();

        if (ip == null || ip.trim().isEmpty()) {
            statusLabel.setText("Please enter IP address");
            return false;
        }

        try {
            int port = Integer.parseInt(portText.trim());
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
