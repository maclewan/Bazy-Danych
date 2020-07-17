package sample.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.Classes.SuperArrayList;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class ZwierzeController {

    private Stage stage;
    private Connection conn;
    private Statement stmt;
    private String query;
    private ResultSet res;
    private PanelController pc;
    private String idPacjenta;
    private char userTyp;

    public ZwierzeController(Stage s,Connection c, PanelController p, String idPacjenta, char userTyp){
        stage=s;
        conn=c;
        pc=p;
        this.idPacjenta=idPacjenta;
        this.userTyp= userTyp;
    }

    @FXML
    private void initialize(){
        if((userTyp=='w'||userTyp=='s')){
            btnUsunZwierze.setVisible(false);
        }
        if(userTyp=='w'){
            btnEdytuj.setVisible(false);
        }
        updateData();

    }

    private void updateData(){
        try {
            /**
             * Dane do nagłowka
             */
            clearGP(gridNotatki);

            query = "SELECT nazwa,rasa,gatunek,rok_urodzenia AS rok, umaszczenie FROM pacjenci JOIN rasy ON id_rasy=r_id WHERE p_id='" + idPacjenta + "';";
            stmt = conn.createStatement();
            res = stmt.executeQuery(query);
            res.next();
            String temp="Id: "+ idPacjenta+", imie: " +res.getString(1)+", rasa: "+res.getString(2)+", gatunek: "+res.getString(3);
            lblZwierz1.setText(temp);
            temp="rok urodzenia: "+res.getString(4)+", umaszczenie: "+res.getString(5);
            lblZwierz2.setText(temp);


            query = "SELECT n_id, godzina,dzien FROM (terminy JOIN wizyty ON g_id=id_terminu ) JOIN notatki ON id_wizyty=w_id " +
                    "WHERE notatki.id_pacjenta ='"+idPacjenta+ "' " +
                    "ORDER BY dzien, godzina, n_id;";
            stmt = conn.createStatement();
            res = stmt.executeQuery(query);


            SuperArrayList<String> listaNotId = new SuperArrayList<>();
            SuperArrayList<Label> listaLabel = new SuperArrayList<>();
            SuperArrayList<Button> listaButton = new SuperArrayList<>();

            /**
             * Dane do tabelki notatek
             */
            int counter=0;
            while (res.next()) {
                int j = gridNotatki.getRowCount();
                listaNotId.add(res.getString(1));

                temp=res.getString(2)+" "+res.getString(3);
                listaLabel.add(new Label(temp));
                gridNotatki.add(listaLabel.getLast(), 0, j);


                listaButton.add(new Button("Więcej"));
                gridNotatki.add(listaButton.getLast(), 1, j);
                int finalCounter = counter;
                listaButton.getLast().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        pokazNotatke(listaNotId.get(finalCounter));
                    }
                });
                counter++;

            }




        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void pokazNotatke(String idNotatki){
       try {
           Stage stageD = new Stage();
           DodNotatkeController dodNotatkeController = new DodNotatkeController(stageD,true,userTyp,conn);
           dodNotatkeController.setIdNotatki(idNotatki);

           FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("resources/dodajNotatke.fxml"));
           loader.setController(dodNotatkeController);

           stageD.setTitle("Notatka "+idNotatki);
           stageD.setScene(new Scene(loader.load()));
           stageD.show();
           stageD.setResizable(false);
       }
       catch (IOException e) {
           e.printStackTrace();
       }
    }




    @FXML
    private Button btnEdytuj;

    @FXML
    private Button btnUmowWizyte;

    @FXML
    private Button btnUsunZwierze;

    @FXML
    private Label lblZwierz1;

    @FXML
    private GridPane gridNotatki;

    @FXML
    private Label lblZwierz2;

    @FXML
    void btnEdytujOnAction(ActionEvent event) {
        try {
            Stage stageD = new Stage();
            DodZwierzeController dodZwierzeController = new DodZwierzeController(this,stageD,conn,idPacjenta);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("resources/dodajZwierze.fxml"));
            loader.setController(dodZwierzeController);

            stageD.setTitle("Edytuj zwierzaka");
            stageD.setScene(new Scene(loader.load()));
            stageD.show();
            stageD.setResizable(false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void btnUmowWizyteOnAction(ActionEvent event) {
        try {
            Stage stageD = new Stage();
            WybTerminController wybTerminController = new WybTerminController(idPacjenta,conn,stageD,this);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("resources/wybierzTermin.fxml"));
            loader.setController(wybTerminController);

            stageD.setTitle("Wybierz termin wizyty");
            stageD.setScene(new Scene(loader.load()));
            stageD.show();
            stageD.setResizable(false);

            stageD.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    wybTerminController.stopThreads();
                }
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnUsunZwierzeOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Uwaga!");
        alert.setHeaderText("Czy chcesz usunąć pacjenta?");
        alert.setContentText("Potwierdź lub przerwij");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            try {
                query = "DELETE from pacjenci WHERE p_id='" + idPacjenta + "';";
                stmt = conn.createStatement();
                stmt.executeUpdate(query);


                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Uwaga!");
                alert.setHeaderText(null);
                alert.setContentText("Poprawnie usunięto pacjenta");
                alert.showAndWait();
                stage.close();
                pc.refresh();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            return;
        }
    }

    public void refresh(){
        updateData();
        pc.refresh();
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

}
