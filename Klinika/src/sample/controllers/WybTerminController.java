package sample.controllers;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
        import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
        import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.Classes.SuperArrayList;

import java.sql.Connection;

public class WybTerminController {

    private boolean actRequest=false;
    private String idPacjenta;
    private Stage stage;
    private Connection conn;
    private TerminRefresher refresher;

    private SuperArrayList<Label> listDataLabel;
    private SuperArrayList<Label> listGodzinaLabel;
    private SuperArrayList<Label> listLekarz;
    private SuperArrayList<Button> buttonList;

    public WybTerminController(String idPacjenta,Connection conn,Stage stage){
        this.idPacjenta=idPacjenta;
        this.conn=conn;
        this.stage=stage;
    }

    @FXML
    private void initialize(){
        updateData();
        addUpdateListeners();
        refresher = new TerminRefresher(this);
        refresher.start();

    }


    private void updateData(){
        try{
            /**
             * Czyszczenie gridPanow i pobranie filtrów
             */
            clearGP(gridWizytyWybor);
            String c1="%"+lblData.getText()+"%";
            String c2="%"+lblLekarz.getText()+"%";

            /**
             *************************************
             * Pobieram dane z bazy
             */
            String query="SELECT g_id,dzien,godzina,imie, nazwisko " +
                    "FROM terminy JOIN pracownicy ON id_lekarza=staff_id " +
                    "WHERE g_id NOT IN (SELECT id_terminu from wizyty) AND dzien like '' AND (imie LIKE '' OR nazwisko like '');"




        } catch (Exception e) {
            e.printStackTrace();
        }
    }








    @FXML
    private GridPane gridWizytyWybor;

    @FXML
    private TextField lblData;

    @FXML
    private TextField lblLekarz;

    @FXML
    private Button btnFiltruj;

    @FXML
    private Button btnUsunFiltr;

    @FXML
    void btnFiltrujOnAction(ActionEvent event) {

    }

    @FXML
    void btnUsunFiltrOnAction(ActionEvent event) {

    }


    private void addUpdateListeners(){
        SuperArrayList<TextField> filtry = new SuperArrayList<>();
        filtry.add(lblData);
        filtry.add(lblLekarz);


        for (TextField tf: filtry) {
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                actRequest=true;
            });

        }

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

    public boolean getActRequest(){
        return actRequest;
    }

    public void refresh(){
        updateData();
        actRequest = false;
    }

}

class TerminRefresher extends Thread{

    private WybTerminController wybTerminController;

    public TerminRefresher(WybTerminController wybTerminController){
        this.wybTerminController=wybTerminController;
    }

    public void run(){
        while(true) {
            try {
                Thread.sleep(200);
                if (wybTerminController.getActRequest()) {
                    Platform.runLater(() -> {
                        wybTerminController.refresh();
                    });
                }
                else
                    Thread.sleep(80);


            } catch (InterruptedException e) {
                return;
            }

        }
    }
}