package sample.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
        import javafx.scene.control.Button;
        import javafx.scene.control.ChoiceBox;
        import javafx.scene.control.TextField;

public class DodZwierzeController {

    @FXML
    private TextField fldNazwa;

    @FXML
    private TextField fldUmaszczenie;

    @FXML
    private TextField fldRok;

    @FXML
    private ChoiceBox<?> pickerGatunek;

    @FXML
    private ChoiceBox<?> pickerRasa;

    @FXML
    private Button btnZatwierdz;

    @FXML
    void btnZatwierdzOnAction(ActionEvent event) {

    }

    @FXML
    void fldRokOnAction(ActionEvent event) {

    }

}
