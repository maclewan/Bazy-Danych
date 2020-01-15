package sample.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import sample.Classes.SuperArrayList;

import java.time.LocalDate;
import java.util.ArrayList;

public class DodTerminyController {

    private SuperArrayList<RadioButton> rBList= new SuperArrayList<>();
    private LocalDate lastSetDate =LocalDate.now();

    @FXML
    private void initialize(){
        /**
         * tworzenie tabelki z przyciskami
         */
        int h=8;
        int m=0;
        for(int i=0;i<16;i++){
            String temp = new String();
            if(m==0) {
                temp = h + ":00";
                m++;
            }
            else{
                temp = h + ":30";
                m--;
                h++;
            }

            rBList.add(new RadioButton());
            rBList.getLast().setText(temp);
            if(i<8) {
                gridTerminy.add(rBList.getLast(), 0,i);
            }
            else{
                gridTerminy.add(rBList.getLast(), 1,i-8);
            }
        }

        /**
         * ustaw date
         */
        datePicker.setValue(LocalDate.now());
        datePicker.setEditable(false);
        aktWolneTerminy();
    }

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button btnWszystkie;

    @FXML
    private Button btnWszystkieDo12;

    @FXML
    private Button btnWszystkiePo12;

    @FXML
    private Button btnDodaj;

    @FXML
    private GridPane gridTerminy;

    @FXML
    private ChoiceBox<String> pickerLekarz;

    @FXML
    void btnDodajOnAction(ActionEvent event) {

    }

    @FXML
    void btnWszystkieDo12OnAction(ActionEvent event) {
        aktWolneTerminy();
        for (int i=0; i<8;i++){
            if(!rBList.get(i).isDisabled())
                rBList.get(i).setSelected(!rBList.get(i).isSelected());
        }

    }

    @FXML
    void btnWszystkieOnAction(ActionEvent event) {
        aktWolneTerminy();
        for (RadioButton radioButton: rBList) {
            if(!radioButton.isDisabled())
                radioButton.setSelected(!radioButton.isSelected());
        }
    }

    @FXML
    void btnWszystkiePo12OnAction(ActionEvent event) {
        aktWolneTerminy();
        for (int i=8; i<16;i++){
            if(!rBList.get(i).isDisabled())
                rBList.get(i).setSelected(!rBList.get(i).isSelected());

        }
    }

    @FXML
    void datePickerOnAction(ActionEvent event) {
        aktWolneTerminy();
        if(datePicker.getValue().compareTo(LocalDate.now())<0){
            datePicker.setValue(lastSetDate);
        }
        lastSetDate=datePicker.getValue();
    }

    public void aktWolneTerminy(){
        //todo: połącz z bazą i pobierz wolne terminy na ten dzień
    }

}
