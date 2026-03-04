package spot.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * A custom control for a dialog box consisting of an ImageView (avatar) and a Label (text)
 * in a rounded bubble with spacing.
 */
public class DialogBox extends HBox {

    /** Wider max width for help output so long command lines (e.g. event) don't wrap. */
    public static final double HELP_MAX_TEXT_WIDTH = 400.0;

    private static final double AVATAR_SIZE = 48.0;
    private static final double BUBBLE_PADDING = 10.0;
    private static final double BUBBLE_RADIUS = 12.0;
    /** Max width of the text label in the bubble. */
    private static final double MAX_TEXT_WIDTH = 280.0;
    /** Spacing between avatar and text in the dialog. */
    private static final double AVATAR_TEXT_SPACING = 10.0;
    private static final String USER_BUBBLE_STYLE =
            "-fx-background-color: #03a2e9; -fx-text-fill: white; -fx-background-radius: "
            + BUBBLE_RADIUS + "px; " + "-fx-padding: " + BUBBLE_PADDING + "px "
            + (BUBBLE_PADDING + 4) + "px;";
    private static final String SPOT_BUBBLE_STYLE =
            "-fx-background-color: #e5e5e5; -fx-text-fill: #333; -fx-background-radius: "
            + BUBBLE_RADIUS + "px; " + "-fx-padding: " + BUBBLE_PADDING + "px "
            + (BUBBLE_PADDING + 4) + "px;";

    private final Label text;
    private final ImageView displayPicture;

    private DialogBox(String s, Image img, boolean isUser) {
        this(s, img, isUser, MAX_TEXT_WIDTH);
    }

    private DialogBox(String s, Image img, boolean isUser, double maxTextWidth) {
        text = new Label(s);
        text.setWrapText(true);
        text.setMaxWidth(maxTextWidth);
        text.setStyle(isUser ? USER_BUBBLE_STYLE : SPOT_BUBBLE_STYLE);

        if (img != null) {
            displayPicture = new ImageView(img);
            displayPicture.setFitWidth(AVATAR_SIZE);
            displayPicture.setFitHeight(AVATAR_SIZE);
            displayPicture.setPreserveRatio(true);
            double radius = AVATAR_SIZE / 2.0;
            Circle clip = new Circle(radius, radius, radius);
            displayPicture.setClip(clip);
            this.setSpacing(AVATAR_TEXT_SPACING);
            this.getChildren().addAll(text, displayPicture);
        } else {
            displayPicture = null;
            this.getChildren().add(text);
        }
    }

    /**
     * Flips the dialog box so the ImageView is on the left and text on the right (for User on right).
     */
    private void flip() {
        this.setAlignment(Pos.TOP_RIGHT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
    }

    /** User on the right: avatar then text, right-aligned, blue bubble. */
    public static DialogBox getUserDialog(String text, Image img) {
        var db = new DialogBox(text, img, true);
        db.flip();
        return db;
    }

    /** Spot on the left: text only (no avatar; Spot is shown in header), left-aligned, grey bubble. */
    public static DialogBox getSpotDialog(String text) {
        return getSpotDialog(text, MAX_TEXT_WIDTH);
    }

    /**
     * Spot dialog with custom max width (e.g. wider for help output).
     *
     * @param text      the message to display
     * @param maxWidth  max width of the bubble in pixels
     */
    public static DialogBox getSpotDialog(String text, double maxWidth) {
        var db = new DialogBox(text, null, false, maxWidth);
        db.setAlignment(Pos.TOP_LEFT);
        return db;
    }
}
