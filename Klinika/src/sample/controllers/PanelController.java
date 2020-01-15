package sample.controllers;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.Classes.SuperArrayList;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.LongAccumulator;


public class PanelController {

    private int userId;
    private char userType;
    private boolean actRequest=true;
    private Stage stage;
    private Connection conn;
    private Refresher refresher;

    private SuperArrayList<Label> zwierzDaneWlascicieli = new SuperArrayList<>();
    private SuperArrayList<String> zwierzIdPacjenta = new SuperArrayList<>();
    private SuperArrayList<Label> zwierzNazwaLabel = new SuperArrayList<>();
    private SuperArrayList<Label> zwierzGatunekLabel = new SuperArrayList<>();
    private SuperArrayList<Button> zwierzWiecej = new SuperArrayList<>();

    private SuperArrayList<String> wizIdWizyty = new SuperArrayList<>();
    private SuperArrayList<Label> wizTermin = new SuperArrayList<>();
    private SuperArrayList<Label> wizWlasciciel = new SuperArrayList<>();
    private SuperArrayList<Label> wizZwierzak = new SuperArrayList<>();
    private SuperArrayList<Label> wizGatunek = new SuperArrayList<>();
    private SuperArrayList<Button> wizNotBtn = new SuperArrayList<>();
    private SuperArrayList<Button> wizUsunWiz = new SuperArrayList<>();

    private SuperArrayList<String> wlIdWlasciciela = new SuperArrayList<>();
    private SuperArrayList<Label> wlImie = new SuperArrayList<>();
    private SuperArrayList<Label> wlNazwisko = new SuperArrayList<>();
    private SuperArrayList<Label> wlUlica = new SuperArrayList<>();
    private SuperArrayList<Label> wlKod = new SuperArrayList<>();
    private SuperArrayList<Button> wlEdit = new SuperArrayList<>();
    private SuperArrayList<Button> wlUsun = new SuperArrayList<>();

    private SuperArrayList<Label> pracImie = new SuperArrayList<>();
    private SuperArrayList<Label> pracNazwisko = new SuperArrayList<>();
    private SuperArrayList<Label> pracZawod = new SuperArrayList<>();
    private SuperArrayList<Label> pracNumerId = new SuperArrayList<>();
    private SuperArrayList<Button> pracEdit = new SuperArrayList<>();
    private SuperArrayList<Button> pracUsun = new SuperArrayList<>();



    public PanelController(int c,char t,Stage stage){
        userId=c;
        userType=t;
        this.stage=stage;
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

            updateData();

            addUpdateListeners();
            refresher = new Refresher(this);
            refresher.start();

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
            fldWizDzien.setVisible(false);

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

            btnZwierzDodajRase.setVisible(false);

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
        else if(userType=='l'){
            /**
             * taby
             */

            tabPracownicy.setDisable(true);
            tabPracownicy.setText("");

            /**
             * przyciski
             */

            btnWlDodaj.setVisible(false);


            for (Button b:wlUsun) {
                b.setVisible(false);
            }

            for (Button b:wizUsunWiz) {
                b.setVisible(false);
            }
            /**
             * labele
             */
            lblWizNU.setText("Notatka");
        }
        else if(userType=='s'){

            /**
             * przyciski
             */

            for (Button b:pracUsun) {
                b.setVisible(false);
            }
            for (Button b:pracEdit) {
                b.setVisible(false);
            }
            btnZwierzDodajRase.setVisible(false);
            btnPracDodajPrac.setVisible(false);
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

            String wic0="%"+fldWizDzien.getText()+"%";
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
                        "JOIN wlasciciele ON pacjenci.id_wlasciciela=w_id " +
                        "WHERE (nazwisko LIKE '"+zc1+"' OR imie LIKE '"+zc1+"') AND nazwa LIKE '"+zc2+"' AND gatunek LIKE '"+zc3+"' " +
                        "ORDER BY nazwisko,imie;";
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
                int finalCounter = counter;
                zwierzWiecej.getLast().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        openZwierzDetails(finalCounter);
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
                        "WHERE (nazwisko LIKE '"+wic1+"' OR imie LIKE '"+wic1+"') AND dzien LIKE '"+wic0+"'AND nazwa LIKE '"+wic2+"' " +
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
                int finalCounter1 = counter;
                wizNotBtn.getLast().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        openNewNotatka(wizIdWizyty.get(finalCounter1));
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

            /**
             * Tab Wlasciciele
             */

            if(userType=='w'){
                query="SELECT w_id,imie,nazwisko,ulica,kod_pocztowy AS kod FROM wlasciciele " +
                        "WHERE w_id = "+userId+";";
            }
            else{
                query="SELECT w_id,imie,nazwisko,ulica,kod_pocztowy AS kod FROM wlasciciele " +
                        "WHERE imie LIKE '"+wlc1+"' AND nazwisko LIKE '"+wlc2+"' AND ulica LIKE '"+wlc3+"' AND kod_pocztowy LIKE '"+wlc4+"' " +
                        "ORDER BY nazwisko, imie;";
            }

            stmt = conn.createStatement();
            res = stmt.executeQuery(query);

            wlIdWlasciciela = new SuperArrayList<>();
            wlImie = new SuperArrayList<>();
            wlNazwisko = new SuperArrayList<>();
            wlUlica = new SuperArrayList<>();
            wlKod = new SuperArrayList<>();
            wlEdit = new SuperArrayList<>();
            wlUsun = new SuperArrayList<>();

            counter=0;
            while (res.next()) {
                int j = gridWlasciciel.getRowCount();

                wlIdWlasciciela.add(res.getString("w_id"));

                wlImie.add(new Label(res.getString("imie")));
                gridWlasciciel.add(wlImie.getLast(), 0, j);

                wlNazwisko.add(new Label(res.getString("nazwisko")));
                gridWlasciciel.add(wlNazwisko.getLast(), 1, j);

                wlUlica.add(new Label(res.getString("ulica")));
                gridWlasciciel.add(wlUlica.getLast(), 2, j);

                wlKod.add(new Label(res.getString("kod")));
                gridWlasciciel.add(wlKod.getLast(), 3, j);


                wlEdit.add(new Button("Edit"));
                gridWlasciciel.add(wlEdit.getLast(), 4, j);
                wlEdit.getLast().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //todo:Edytuj dane wlasciciela korzystajac z int counter->wizIdWlasciciela
                    }
                });

                wlUsun.add(new Button("X"));
                gridWlasciciel.add(wlUsun.getLast(), 5, j);
                wlUsun.getLast().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //todo:usun wlasciciela korzystajac z int counter ->wizIdWlasciciela
                    }
                });
                counter++;
            }

            /**
             * Tab Pracownicy
             */


            query="SELECT imie,nazwisko,typ,staff_id " +
                    "FROM pracownicy " +
                    "WHERE imie LIKE '"+pc1+"' AND nazwisko LIKE '"+pc2+"' AND typ LIKE '"+pc3+"';";

            stmt = conn.createStatement();
            res = stmt.executeQuery(query);

            pracImie = new SuperArrayList<>();
            pracNazwisko = new SuperArrayList<>();
            pracZawod = new SuperArrayList<>();
            pracNumerId = new SuperArrayList<>();
            pracEdit = new SuperArrayList<>();
            pracUsun = new SuperArrayList<>();

            counter=0;
            while (res.next()) {
                int j = gridPracownicy.getRowCount();

                pracImie.add(new Label(res.getString("imie")));
                gridPracownicy.add(pracImie.getLast(), 0, j);

                pracNazwisko.add(new Label(res.getString("nazwisko")));
                gridPracownicy.add(pracNazwisko.getLast(), 1, j);

                pracZawod.add(new Label(res.getString("typ")));
                gridPracownicy.add(pracZawod.getLast(), 2, j);

                pracNumerId.add(new Label(res.getString("staff_id")));
                gridPracownicy.add(pracNumerId.getLast(), 3, j);


                pracEdit.add(new Button("Edit"));
                gridPracownicy.add(pracEdit.getLast(), 4, j);
                pracEdit.getLast().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //todo:Edytuj dane pracownika korzystajac z int counter->pracNumerId
                    }
                });

                pracUsun.add(new Button("X"));
                gridPracownicy.add(pracUsun.getLast(), 5, j);
                pracUsun.getLast().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //todo:usun pracownika korzystajac z int counter ->pracNumerId
                    }
                });
                counter++;
            }

            /**
             * Nadaj uprawnienia tylko tym którzy je mają mieć
             */
            hidePossibilities();




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
    private Button btnZwierzDodajRase;

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
    private TextField fldWizDzien;

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
        fldWizDzien.setText("");
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
        try {
            Stage stageD = new Stage();
            DodZwierzeController dodZwierzeController = new DodZwierzeController(this,stageD,conn);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("resources/dodajZwierze.fxml"));
            loader.setController(dodZwierzeController);

            stageD.setTitle("Dodaj Pupila");
            stageD.setScene(new Scene(loader.load()));
            stageD.show();
            stageD.setResizable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnZwierzDodajRaseOnAction(ActionEvent event){
        try {
            Stage stageD = new Stage();
            DodRaseController dodRaseController = new DodRaseController(conn,stageD,this);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("resources/dodajRase.fxml"));
            loader.setController(dodRaseController);

            stageD.setTitle("Dodaj Rase");
            stageD.setScene(new Scene(loader.load()));
            stageD.show();
            stageD.setResizable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        filtry.add(fldWizDzien);
        filtry.add(fldPracImie);
        filtry.add(fldPracNazwisko);
        filtry.add(fldPracZawod);

        for (TextField tf: filtry) {
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                actRequest=true;
            });

        }

    }

    public boolean getActRequest(){
        return actRequest;
    }

    public void refresh(){
        updateData();
        actRequest = false;
    }


    public void stopThreads(){
        Platform.runLater(() -> {
            refresher.interrupt();
        });

    }

    private void openZwierzDetails(int index){
        try {
            Stage stageD = new Stage();
            String idPacjenta=zwierzIdPacjenta.get(index);
            ZwierzeController zwierzeController = new ZwierzeController(stageD,conn,this,idPacjenta,userType);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("resources/zwierze.fxml"));
            loader.setController(zwierzeController);

            stageD.setTitle("Szczegóły");
            stageD.setScene(new Scene(loader.load()));
            stageD.show();
            stageD.setResizable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openNewNotatka(String idWizyty){
        try {
            Stage stageD = new Stage();

            DodNotatkeController dodNotatkeController = new DodNotatkeController(stageD,false,userType,conn);
            dodNotatkeController.setIdWizyty(idWizyty);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("resources/dodajNotatke.fxml"));
            loader.setController(dodNotatkeController);

            stageD.setTitle("Nowa Notatka");
            stageD.setScene(new Scene(loader.load()));
            stageD.show();
            stageD.setResizable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

class Refresher extends Thread{

    private PanelController panelController;

    public Refresher(PanelController panelController){
        this.panelController=panelController;
    }

    public void run(){
        int count=0;
        while(true) {
            try {
                Thread.sleep(200);
                count++;
                if (panelController.getActRequest()) {
                    Platform.runLater(() -> {
                        panelController.refresh();
                    });
                    count=0;
                }
                else if(count>24){
                    Platform.runLater(() -> {
                        panelController.refresh();
                    });
                    count=0;
                }
                else
                    Thread.sleep(80);


            } catch (InterruptedException e) {
                return;
            }

        }
    }
}