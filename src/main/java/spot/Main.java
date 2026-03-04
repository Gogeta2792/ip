package spot;

import java.io.InputStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import spot.ui.DialogBox;

/**
 * Main window for the Spot chatbot GUI (JavaFX).
 */
public class Main extends Application {

    private static final double HEADER_AVATAR_SIZE = 56.0;

    private VBox headerPane;
    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;
    private boolean firstMessageSent;

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

    /**
     * Builds the fixed top header with circular Spot avatar and name "Spot the Dog".
     */
    private VBox createHeader() {
        ImageView spotAvatar = new ImageView(spotImage);
        spotAvatar.setFitWidth(HEADER_AVATAR_SIZE);
        spotAvatar.setFitHeight(HEADER_AVATAR_SIZE);
        spotAvatar.setPreserveRatio(true);
        double radius = HEADER_AVATAR_SIZE / 2.0;
        Circle clip = new Circle(radius, radius, radius);
        spotAvatar.setClip(clip);

        Label nameLabel = new Label("Spot the Dog");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        VBox header = new VBox(4);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: white; -fx-padding: 8px;");
        header.getChildren().addAll(spotAvatar, nameLabel);
        return header;
    }

    @Override
    public void start(Stage stage) {
        headerPane = createHeader();
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);
        userInput = new TextField();
        userInput.setPromptText("Type a command or say help");
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.setStyle("-fx-background-color: #f0f0f0;");
        mainLayout.getChildren().addAll(headerPane, scrollPane, userInput, sendButton);

        scene = new Scene(mainLayout);

        stage.setTitle("Spot");
        stage.getIcons().add(spotImage);
        stage.setResizable(true);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);
        double headerHeight = 90.0;
        double bottomHeight = 32.0;

        headerPane.setPrefHeight(headerHeight);
        headerPane.setMinHeight(headerHeight);
        AnchorPane.setTopAnchor(headerPane, 0.0);
        AnchorPane.setLeftAnchor(headerPane, 0.0);
        AnchorPane.setRightAnchor(headerPane, 0.0);

        scrollPane.setPrefSize(400.0, 600.0 - headerHeight - bottomHeight);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        AnchorPane.setTopAnchor(scrollPane, headerHeight);
        AnchorPane.setBottomAnchor(scrollPane, bottomHeight);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);

        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        dialogContainer.setSpacing(12);
        dialogContainer.setPadding(new Insets(10));
        sendButton.setPrefWidth(55.0);
        userInput.setStyle("-fx-background-color: white; -fx-border-color: #ccc; "
                + "-fx-border-width: 1px 0 0 0; -fx-border-radius: 0;");

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);
        AnchorPane.setLeftAnchor(userInput, 1.0);
        AnchorPane.setRightAnchor(userInput, 56.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        sendButton.setOnMouseClicked((event) -> handleUserInput());
        userInput.setOnAction((event) -> handleUserInput());

        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        String welcome = spot.getWelcomeMessage();
        if (!welcome.isEmpty()) {
            dialogContainer.getChildren().addAll(
                    DialogBox.getSpotDialog(welcome),
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
        if (!firstMessageSent) {
            firstMessageSent = true;
            userInput.setPromptText("");
        }
        String spotText = spot.getResponse(userText);
        dialogContainer.getChildren().addAll(
                new Separator(),
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getSpotDialog(spotText),
                new Separator()
        );
        userInput.clear();

        if (spotText.contains("Bye. Hope to see you again soon!")) {
            Platform.exit();
        }
    }
}
