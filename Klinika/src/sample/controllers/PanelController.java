package sample.controllers;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import sample.Classes.SuperArrayList;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.LongAccumulator;


public class PanelController {

    private int userId;
    private char userType;
    Connection conn;

    SuperArrayList<Label> zwierzDaneWlascicieli = new SuperArrayList<>();
    SuperArrayList<String> zwierzIdPacjenta = new SuperArrayList<>();
    SuperArrayList<Label> zwierzNazwaLabel = new SuperArrayList<>();
    SuperArrayList<Label> zwierzGatunekLabel = new SuperArrayList<>();
    SuperArrayList<Button> zwierzWiecej = new SuperArrayList<>();

    SuperArrayList<String> wizIdWizyty = new SuperArrayList<>();
    SuperArrayList<Label> wizTermin = new SuperArrayList<>();
    SuperArrayList<Label> wizWlasciciel = new SuperArrayList<>();
    SuperArrayList<Label> wizZwierzak = new SuperArrayList<>();
    SuperArrayList<Label> wizGatunek = new SuperArrayList<>();
    SuperArrayList<Button> wizNotBtn = new SuperArrayList<>();
    SuperArrayList<Button> wizUsunWiz = new SuperArrayList<>();

    SuperArrayList<Button> wlUsun = new SuperArrayList<>();



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
        if(userType=='w'){
            /**
             * filtry
             */

            fldZwierzGatunek.setVisible(false);
            fldZwierzImie.setVisible(false);
            fldZwierzWlasc.setVisible(false);

            fldWlKod.setVisible(false);
            fldWlUlica.setVisible(false);
            fldWlNazwisko.setVisible(false);
            fldWlImie.setVisible(false);

            fldWizZwierz.setVisible(false);
            fldWizWlasciciel.setVisible(false);

            fldPracImie.setVisible(false);
            fldPracNazwisko.setVisible(false);
            fldPracZawod.setVisible(false);

            tabPracownicy.setDisable(true);
            tabPracownicy.setText("");

            /**
             * przyciski
             */

            btnZwierzDodajZw.setVisible(false);
            btnWizEdytuj.setVisible(false);
            btnWlDodaj.setVisible(false);

            btnUsunFiltr.setVisible(false);
            btnZwierzUsunFiltr.setVisible(false);
            btnWlUsunFiltr.setVisible(false);

            for (Button b:wizNotBtn) {
                b.setVisible(false);
            }
            for (Button b:wlUsun) {
                b.setVisible(false);
            }

            /**
             * labele
             */
            lblWizNU.setText("      Usun");

        }
    }

    private void updateData(){
        try {
            /**
             * Czyszczenie gridPanow
             */
            clearGP(gridZwierz);
            clearGP(gridPracownicy);
            clearGP(gridWlasciciel);
            clearGP(gridWizyty);


            /**
             * Pobieram filtry
             */
            String zc1="%"+fldZwierzWlasc.getText()+"%";
            String zc2="%"+fldZwierzImie.getText()+"%";
            String zc3="%"+fldZwierzGatunek.getText()+"%";

            String wic1="%"+fldWizWlasciciel.getText()+"%";
            String wic2="%"+fldWizZwierz.getText()+"%";

            String wlc1="%"+fldWlImie.getText()+"%";
            String wlc2="%"+fldWlNazwisko.getText()+"%";
            String wlc3="%"+fldWlUlica.getText()+"%";
            String wlc4="%"+fldWlKod.getText()+"%";

            String pc1="%"+fldPracImie.getText()+"%";
            String pc2="%"+fldPracNazwisko.getText()+"%";
            String pc3="%"+fldPracZawod.getText()+"%";

            /**
             *************************************
             * Pobieram dane z bazy
             */
            String query;
            /**
             * Tab zwierz
             */

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

            /**
             * Tab Umowione wizyty
             */

            if(userType=='w'){
                query = "SELECT wizyty.w_id AS idWiz, CONCAT(TIME_FORMAT(godzina,'%H:%i'),' ',dzien) AS termin, CONCAT(imie,' ',nazwisko) AS wlasciciel, nazwa, gatunek " +
                        "FROM (((wizyty JOIN terminy ON id_terminu=g_id) JOIN pacjenci ON id_pacjenta=p_id) JOIN rasy ON r_id=id_rasy) JOIN wlasciciele " +
                        "ON id_wlasciciela=wlasciciele.w_id " +
                        "WHERE wlasciciele.w_id="+userId+" " +
                        "ORDER BY dzien,godzina;";
            }
            else {
                query = "SELECT wizyty.w_id AS idWiz,CONCAT(TIME_FORMAT(godzina,'%H:%i'),' ',dzien) AS termin, CONCAT(imie,' ',nazwisko) AS wlasciciel, nazwa, gatunek " +
                        "FROM (((wizyty JOIN terminy ON id_terminu=g_id) JOIN pacjenci ON id_pacjenta=p_id) JOIN rasy ON r_id=id_rasy) JOIN wlasciciele " +
                        "ON id_wlasciciela=wlasciciele.w_id " +
                        "WHERE (nazwisko LIKE '"+wic1+"' OR imie LIKE '"+wic1+"') AND nazwa LIKE '"+wic2+"' " +
                        "ORDER BY dzien,godzina;";
            }

            stmt = conn.createStatement();
            res = stmt.executeQuery(query);

            wizIdWizyty = new SuperArrayList<>();
            wizTermin = new SuperArrayList<>();
            wizWlasciciel = new SuperArrayList<>();
            wizZwierzak = new SuperArrayList<>();
            wizGatunek = new SuperArrayList<>();
            wizNotBtn = new SuperArrayList<>();
            wizUsunWiz = new SuperArrayList<>();

            counter=0;
            while (res.next()) {
                int j = gridWizyty.getRowCount();
                wizIdWizyty.add(res.getString("idWiz"));

                wizTermin.add(new Label(res.getString("termin")));
                gridWizyty.add(wizTermin.getLast(), 0, j);

                wizWlasciciel.add(new Label(res.getString("wlasciciel")));
                gridWizyty.add(wizWlasciciel.getLast(), 1, j);

                wizZwierzak.add(new Label(res.getString("nazwa")));
                gridWizyty.add(wizZwierzak.getLast(), 2, j);

                wizGatunek.add(new Label(res.getString("gatunek")));
                gridWizyty.add(wizGatunek.getLast(), 3, j);


                wizNotBtn.add(new Button("N"));
                gridWizyty.add(wizNotBtn.getLast(), 4, j);
                wizNotBtn.getLast().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //todo:Otworz notatke korzystajac z int counter->wizIdWizyty
                    }
                });

                wizUsunWiz.add(new Button("X"));
                gridWizyty.add(wizUsunWiz.getLast(), 5, j);
                wizUsunWiz.getLast().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //todo:usun wizyte korzystajac z int counter ->wizIdWizyty
                    }
                });
                counter++;

            }



        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @FXML
    private TabPane tabPane;
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
    private GridPane gridWizyty;

    @FXML
    private TextField fldWizWlasciciel;

    @FXML
    private TextField fldWizZwierz;

    @FXML
    private Button btnUsunFiltr;


    @FXML
    private Button btnWizEdytuj;

    @FXML
    private Label lblWizNU;


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
        SuperArrayList<TextField> filtry = new SuperArrayList<>();
        filtry.add(fldZwierzWlasc);
        filtry.add(fldZwierzImie);
        filtry.add(fldZwierzGatunek);
        filtry.add(fldWlNazwisko);
        filtry.add(fldWlKod);
        filtry.add(fldWlImie);
        filtry.add(fldWlUlica);
        filtry.add(fldWizWlasciciel);
        filtry.add(fldWizZwierz);
        filtry.add(fldPracImie);
        filtry.add(fldPracNazwisko);
        filtry.add(fldPracZawod);

        for (TextField tf: filtry) {
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                updateData();
            });

        }

    }





}
