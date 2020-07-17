package sample.controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.Classes.SuperArrayList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class DodTerminyController {

    private SuperArrayList<RadioButton> rBList= new SuperArrayList<>();
    private LocalDate lastSetDate =LocalDate.now();
    private Connection conn;
    private Stage stage;
    private ObservableList<String> lekarzeList;

    public DodTerminyController(Connection conn, Stage stage) {
        this.conn = conn;
        this.stage = stage;
    }

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

        /**
         * Pobierz terminy
         */
        aktWolneTerminy();
        /**
         * Pobierz lekarzy
         */
        try {
            String query = "SELECT concat(imie,' ', nazwisko),staff_id FROM pracownicy WHERE typ ='lekarz';";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);

            lekarzeList= FXCollections.observableArrayList();


            while(res.next()){
                lekarzeList.add(res.getString(1));

            }

            pickerLekarz.setItems(lekarzeList);
            pickerLekarz.setValue(lekarzeList.get(0));



        } catch (SQLException e) {
            e.printStackTrace();
        }


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
        try {
            String query = "SELECT godzina FROM terminy WHERE dzien ='" + datePicker.getValue() + "';";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);

            SuperArrayList<String> listaZajetychterminow =new SuperArrayList<>();
            while(res.next()){
                listaZajetychterminow.add(res.getString(1));
            }

            for (RadioButton rb:rBList) {
                if(listaZajetychterminow.superContains(rb.getText()+":00")){
                    rb.setDisable(true);
                }
                else
                    rb.setDisable(false);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnDodajOnAction(ActionEvent event) {

        String idLekarza;
        String data=datePicker.getValue().toString();
        try {
            String query = "SELECT staff_id FROM pracownicy WHERE typ='lekarz' AND CONCAT(imie,' ',nazwisko)='" + pickerLekarz.getValue() + "';";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            res.next();
            idLekarza=res.getString(1);

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        for (RadioButton rb: rBList) {
            try {
                if (!rb.isSelected() || rb.isDisabled()) {
                    continue;
                }
                String query = "INSERT into terminy (dzien,godzina,id_lekarza) " +
                        "VALUES ('" + data + "','" + rb.getText() + "','" + idLekarza + "');";
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(query);
                rb.setSelected(false);
                rb.setDisable(true);

            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Problem");
                alert.setHeaderText("Błąd dodawania terminu");
                alert.setContentText("Nie udało się dodać terminu: "+data+" "+rb.getText());

                alert.showAndWait();
            }

        }
        aktWolneTerminy();


    }

}
