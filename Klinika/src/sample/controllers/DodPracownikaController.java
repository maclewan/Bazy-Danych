package sample.controllers;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class DodPracownikaController {

    private ObservableList<String> zawodList = FXCollections.observableArrayList("Lekarz", "Sekretariat");

    @FXML
    private void initialize(){
        pickerZawod.setItems(zawodList);
    }

    @FXML
    private TextField txtFldImie;

    @FXML
    private TextField txtFldNazwisko;

    @FXML
    private Button btnZatwierdz;

    @FXML
    private ChoiceBox<String> pickerZawod;

    @FXML
    void btnZatwierdzOnAction(ActionEvent event) {

    }

}
