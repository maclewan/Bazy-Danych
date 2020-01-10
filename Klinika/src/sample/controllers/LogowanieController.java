package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
        import javafx.scene.control.Button;
        import javafx.scene.control.Label;
        import javafx.scene.control.PasswordField;
        import javafx.scene.control.TextField;

public class LogowanieController {

    @FXML
    private TextField fldLogin;

    @FXML
    private PasswordField fldHaslo;

    @FXML
    private Button btnZaloguj;

    @FXML
    private Label lblTerminal;

    @FXML
    void btnZalogujOnAction(ActionEvent event) {

    }

}
