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
 * A custom control for a dialog box consisting of an ImageView (avatar) and a Label (text).
 */
public class DialogBox extends HBox {

    private static final double AVATAR_SIZE = 48.0;

    private final Label text;
    private final ImageView displayPicture;

    private DialogBox(String s, Image img) {
        text = new Label(s);
        displayPicture = new ImageView(img);

        text.setWrapText(true);
        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setFitHeight(AVATAR_SIZE);
        displayPicture.setPreserveRatio(true);

        double radius = AVATAR_SIZE / 2.0;
        Circle clip = new Circle(radius, radius, radius);
        displayPicture.setClip(clip);

        this.getChildren().addAll(text, displayPicture);
    }

    /**
     * Flips the dialog box so the ImageView is on the left and text on the right (for User on left).
     */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
    }

    /** User on the left: avatar then text, left-aligned. */
    public static DialogBox getUserDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /** Spot on the right: text then avatar, right-aligned. */
    public static DialogBox getSpotDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.setAlignment(Pos.TOP_RIGHT);
        return db;
    }
}
