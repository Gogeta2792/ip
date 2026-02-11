package spot;

import java.io.InputStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import spot.ui.DialogBox;

/**
 * Main window for the Spot chatbot GUI (JavaFX).
 */
public class Main extends Application {

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;

    private final Image userImage = loadImage("/images/DaUser.png");
    private final Image spotImage = loadImage("/images/DaSpot.png");
    private final Spot spot = new Spot("data/spot.txt");

    private static Image loadImage(String resourcePath) {
        InputStream stream = Main.class.getResourceAsStream(resourcePath);
        if (stream != null) {
            return new Image(stream);
        }
        return createPlaceholderImage();
    }

    private static Image createPlaceholderImage() {
        javafx.scene.image.WritableImage img = new javafx.scene.image.WritableImage(100, 100);
        return img;
    }

    @Override
    public void start(Stage stage) {
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);
        userInput = new TextField();
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        scene = new Scene(mainLayout);

        stage.setTitle("Spot");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);
        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        dialogContainer.setSpacing(12);
        dialogContainer.setPadding(new Insets(10));
        userInput.setPrefWidth(325.0);
        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);
        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);
        AnchorPane.setLeftAnchor(userInput, 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        sendButton.setOnMouseClicked((event) -> handleUserInput());
        userInput.setOnAction((event) -> handleUserInput());

        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        String welcome = spot.getWelcomeMessage();
        if (!welcome.isEmpty()) {
            dialogContainer.getChildren().addAll(
                    DialogBox.getSpotDialog(welcome, spotImage),
                    new Separator()
            );
        }

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles user input: adds user and Spot dialog boxes and clears the input.
     */
    private void handleUserInput() {
        String userText = userInput.getText();
        if (userText.isBlank()) {
            return;
        }
        String spotText = spot.getResponse(userText);
        dialogContainer.getChildren().addAll(
                new Separator(),
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getSpotDialog(spotText, spotImage),
                new Separator()
        );
        userInput.clear();

        if (spotText.contains("Bye. Hope to see you again soon!")) {
            Platform.exit();
        }
    }
}
