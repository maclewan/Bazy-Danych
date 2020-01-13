package sample.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
        import javafx.scene.control.Button;
        import javafx.scene.control.ChoiceBox;
        import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DodZwierzeController {

    private int rok;

    @FXML
    private void initialize(){
        btnZatwierdz.setDisable(true);

        fldRok.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                btnZatwierdz.setDisable(false);

                if(newValue.length()>4)
                    throw new Exception();
                if(newValue.length()!=0)
                    rok=Integer.parseInt(newValue);

                if(rok> LocalDate.now().getYear())
                    btnZatwierdz.setDisable(true);

            } catch (Exception e) {
                fldRok.setText(oldValue);
            }
        });
    }
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
