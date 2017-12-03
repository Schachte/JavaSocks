import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Chat extends Application {

    private boolean isServer = false;
    private TextArea messages = new TextArea();
    private NetworkConnection connection = isServer ? createServer() : createClient();

    private Parent createContent() {
        messages.setPrefHeight(550);
        TextField input = new TextField();
        input.setOnAction(event -> {
            String message = isServer ? "Server: " : "Client: ";
            message += input.getText();
            input.clear();

            messages.appendText(message + "\n");
            try {
                connection.send(message);
            } catch (Exception e) {
                messages.appendText("Failed to send message!");
                e.printStackTrace();
            }
        });

        VBox root = new VBox(20, messages, input);
        root.setPrefSize(600, 600);
        return root;
    }

    public void init() throws Exception {
        connection.startConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Server createServer() {
        return new Server(5555, data -> {
            Platform.runLater(() -> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }

    private Client createClient() {
        return new Client("127.0.0.1", 5555, data -> {
            Platform.runLater(() -> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }

    @Override
    public void stop() throws Exception {
        connection.closeConnection();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }
}
