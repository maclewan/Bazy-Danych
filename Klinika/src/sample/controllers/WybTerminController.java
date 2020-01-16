package sample.controllers;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
        import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.Classes.SuperArrayList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WybTerminController {

    private boolean actRequest=false;
    private ZwierzeController zc;
    private String idPacjenta;
    private Stage stage;
    private Connection conn;
    private TerminRefresher refresher;

    private SuperArrayList<String> idTerminuList;
    private SuperArrayList<Label> listDataLabel;
    private SuperArrayList<Label> listGodzinaLabel;
    private SuperArrayList<Label> listLekarz;
    private SuperArrayList<Button> buttonList;

    public WybTerminController(String idPacjenta,Connection conn,Stage stage,ZwierzeController zc){
        this.idPacjenta=idPacjenta;
        this.conn=conn;
        this.stage=stage;
        this.zc=zc;
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
                    "WHERE g_id NOT IN (SELECT id_terminu from wizyty) AND dzien like '"+c1+"' AND (imie LIKE '"+c2+"' OR nazwisko like '"+c2+"') " +
                    "ORDER BY dzien, godzina;";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);

            idTerminuList = new SuperArrayList<>();
            listDataLabel = new SuperArrayList<>();
            listGodzinaLabel = new SuperArrayList<>();
            listLekarz = new SuperArrayList<>();
            buttonList = new SuperArrayList<>();

            int counter=0;
            while (res.next()) {
                int j = gridWizytyWybor.getRowCount();
                idTerminuList.add(res.getString(1));

                listDataLabel.add(new Label(res.getString(2)));
                gridWizytyWybor.add(listDataLabel.getLast(), 0, j);

                listGodzinaLabel.add(new Label(res.getString(3)));
                gridWizytyWybor.add(listGodzinaLabel.getLast(), 1, j);

                listLekarz.add(new Label(res.getString(4)+" "+res.getString(5)));
                gridWizytyWybor.add(listLekarz.getLast(), 2, j);

                buttonList.add(new Button("Umów"));
                gridWizytyWybor.add(buttonList.getLast(), 3, j);
                int finalCounter = counter;
                buttonList.getLast().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        umowWizyte(idTerminuList.get(finalCounter));
                    }
                });
                counter++;

            }




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void umowWizyte(String idWizyty){
        try {
            String query = "call umow('"+idPacjenta+"','"+idWizyty+"');";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            zc.refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Umawianie wizyty");
            alert.setHeaderText("Poprawnie dodano wizyte");
            alert.setContentText(null);

            alert.showAndWait();
            stage.close();


        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Umawianie wizyty");
            alert.setHeaderText("Wybrany termin nie jest dostępny");
            alert.setContentText("Wybierz termin jeszcze raz");

            alert.showAndWait();

        } finally {
            refresh();
            zc.refresh();
        }
    }



    @FXML
    private GridPane gridWizytyWybor;

    @FXML
    private TextField lblData;

    @FXML
    private TextField lblLekarz;


    @FXML
    private Button btnUsunFiltr;


    @FXML
    void btnUsunFiltrOnAction(ActionEvent event) {
        lblLekarz.setText("");
        lblData.setText("");
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

    public void stopThreads(){
        Platform.runLater(() -> {
            refresher.interrupt();
        });

    }

}

class TerminRefresher extends Thread{

    private WybTerminController wybTerminController;

    public TerminRefresher(WybTerminController wybTerminController){
        this.wybTerminController=wybTerminController;
    }

    public void run(){
        int count=0;
        while(true) {
            try {
                Thread.sleep(200);
                count++;
                if (wybTerminController.getActRequest()) {
                    Platform.runLater(() -> {
                        wybTerminController.refresh();
                    });
                    count=0;
                }
                else if(count>24){
                    Platform.runLater(() -> {
                        wybTerminController.refresh();
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