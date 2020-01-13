package sample.controllers;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import sample.Classes.SuperArrayList;

import java.sql.*;
import java.util.ArrayList;


public class PanelController {

    private int userId;
    private char userType;
    Connection conn;

    SuperArrayList<Label> zwierzDaneWlascicieli = new SuperArrayList<>();
    SuperArrayList<String> zwierzIdPacjenta = new SuperArrayList<>();
    SuperArrayList<Label> zwierzNazwaLabel = new SuperArrayList<>();
    SuperArrayList<Label> zwierzGatunekLabel = new SuperArrayList<>();
    SuperArrayList<Button> zwierzWiecej = new SuperArrayList<>();



    public PanelController(int c,char t){
        userId=c;
        userType=t;
    }

    @FXML
    private void initialize(){
        /**
         * logowanie do bazy
         */
        try {
            String login,haslo;
            if(userType=='l'){
                login="lek@localhost";
                haslo="l3efj29chj";
            }
            else if(userType=='w'){
                login="wlasc@localhost";
                haslo="w3pa2kvi3";
            }
            else if(userType=='s'){
                login="sek@localhost";
                haslo="s4f52vserg";
            }
            else{
                login="adm@localhost";
                haslo="a4vdmq9diw";
            }
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/klinika", login, haslo);

            /**
             * pobieranie danych
             */
            updateData();

            addUpdateListeners();
            hidePossibilities();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hidePossibilities() {
    }

    private void updateData(){
        try {
            /**
             * Czyszczenie gridPanow
             */
            clearGP(gridZwierz);
            //todo: wyczyc grid pany w  zakładkach


            /**
             * Pobieram filtry
             */
            String zc1="%"+fldZwierzWlasc.getText()+"%";
            String zc2="%"+fldZwierzImie.getText()+"%";
            String zc3="%"+fldZwierzGatunek.getText()+"%";
            //todo: pobrać filtry z innych zakladek

            /**
             *************************************
             * Pobieram dane z bazy
             */
            /**
             * Tab zwierz
             */

            String query;
            if(userType=='w'){
                query = "SELECT p_id, imie, nazwisko, nazwa, gatunek FROM (pacjenci JOIN rasy ON pacjenci.id_rasy=rasy.r_id) JOIN wlasciciele ON pacjenci.id_wlasciciela=w_id WHERE w_id="+userId+";";
            }
            else {
                query = "SELECT p_id, imie, nazwisko, nazwa, gatunek FROM (pacjenci JOIN rasy ON pacjenci.id_rasy=rasy.r_id) " +
                        "JOIN wlasciciele ON pacjenci.id_wlasciciela=w_id WHERE (nazwisko LIKE '"+zc1+"' OR imie LIKE '"+zc1+"') AND nazwa LIKE '"+zc2+"' AND gatunek LIKE '"+zc3+"';";
            }

            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);

            zwierzIdPacjenta = new SuperArrayList<>();
            zwierzDaneWlascicieli = new SuperArrayList<>();
            zwierzNazwaLabel = new SuperArrayList<>();
            zwierzGatunekLabel = new SuperArrayList<>();
            zwierzWiecej = new SuperArrayList<>();

            int counter=0;
            while (res.next()) {
                int j = gridZwierz.getRowCount();
                zwierzIdPacjenta.add(res.getString("p_id"));

                zwierzDaneWlascicieli.add(new Label(res.getString("imie") + " " + res.getString("nazwisko")));
                gridZwierz.add(zwierzDaneWlascicieli.getLast(), 0, j);

                zwierzNazwaLabel.add(new Label(res.getString("nazwa")));
                gridZwierz.add(zwierzNazwaLabel.getLast(), 1, j);

                zwierzGatunekLabel.add(new Label(res.getString("gatunek")));
                gridZwierz.add(zwierzGatunekLabel.getLast(), 2, j);

                zwierzWiecej.add(new Button("W"));
                gridZwierz.add(zwierzWiecej.getLast(), 3, j);
                zwierzWiecej.getLast().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //todo:Otworz szczegóły korzystajac z int counter
                    }
                });
                counter++;

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    /**
     * Tab Zwierzęta
     */
    @FXML
    private Button btnZwierzDodajZw;

    @FXML
    private GridPane gridZwierz;

    @FXML
    private TextField fldZwierzWlasc;

    @FXML
    private TextField fldZwierzImie;

    @FXML
    private TextField fldZwierzGatunek;

    @FXML
    private Button btnZwierzUsunFiltr;

    /**
     * Tab Umówione Wizyty
     */

    @FXML
    private GridPane girdWizyty;

    @FXML
    private TextField fldWizWlasciciel;

    @FXML
    private TextField fldWizZwierz;

    @FXML
    private Button btnUsunFiltr;


    @FXML
    private Button btnWizEdytuj;


    /**
     * Tab Właściciele
     */

    @FXML
    private Button btnWlDodaj;

    @FXML
    private GridPane gridWlasciciel;

    @FXML
    private Button btnWlUsunFiltr;

    @FXML
    private TextField fldWlImie;

    @FXML
    private TextField fldWlNazwisko;

    @FXML
    private TextField fldWlUlica;

    @FXML
    private TextField fldWlKod;

    /**
     * Tab Pracownicy
     */

    @FXML
    private Tab tabPracownicy;

    @FXML
    private Button btnPracDodajPrac;

    @FXML
    private GridPane gridPracownicy;

    @FXML
    private Button btnPracUsunFIltr;

    @FXML
    private Button btnPracFiltr;

    @FXML
    private TextField fldPracImie;

    @FXML
    private TextField fldPracNazwisko;

    @FXML
    private TextField fldPracZawod;

    /**
     * Tab Pracownicy
     */

    @FXML
    void btnPracDodajPracOnAction(ActionEvent event) {

    }

    @FXML
    void btnPracUsunFIltrOnAction(ActionEvent event) {
        fldPracImie.setText("");
        fldPracNazwisko.setText("");
        fldPracZawod.setText("");
    }


    /**
     * Tab Umówione Wizyty
     */

    @FXML
    void btnUsunFiltrOnAction(ActionEvent event) {
        fldWizWlasciciel.setText("");
        fldWizZwierz.setText("");
    }

    @FXML
    void btnWizEdytujOnAction(ActionEvent event) {

    }




    /**
     * Tab Właściciele
     */

    @FXML
    void btnWlDodajOnAction(ActionEvent event) {

    }

    @FXML
    void btnWlUsunFiltrOnAction(ActionEvent event) {
        fldWlImie.setText("");
        fldWlKod.setText("");
        fldWlUlica.setText("");
        fldWlNazwisko.setText("");

    }

    /**
     * Tab Zwierzęta
     */

    @FXML
    void btnZwierzDodajZwOnAction(ActionEvent event) {

    }


    @FXML
    void btnZwierzUsunFiltrOnAction(ActionEvent event) {
        fldZwierzGatunek.setText("");
        fldZwierzImie.setText("");
        fldZwierzWlasc.setText("");
    }


    private void clearGP(GridPane pane){

        int rowCount=pane.getRowCount();
        int columnCount=pane.getColumnCount();

        for(int d=0;d<rowCount*columnCount;d++){
            pane.getChildren().remove(pane.getChildren().get(0));
        }

        while(pane.getRowConstraints().size()>0){
            pane.getRowConstraints().remove(0);
        }
    }

    private void addUpdateListeners(){
        fldZwierzWlasc.textProperty().addListener((observable, oldValue, newValue) -> {
            updateData();
        });
        fldZwierzImie.textProperty().addListener((observable, oldValue, newValue) -> {
            updateData();
        });
        fldZwierzGatunek.textProperty().addListener((observable, oldValue, newValue) -> {
            updateData();
        });
        //todo: reszta fieldów

    }





}
