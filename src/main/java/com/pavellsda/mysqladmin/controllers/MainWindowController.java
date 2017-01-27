package com.pavellsda.mysqladmin.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by pavellsda on 21.01.17.
 */


public class MainWindowController {


    @FXML
    public ComboBox<String> currentDataBase;
    public ListView<String> tableList;
    public TableView table;
    public Button newDataButton;

    private String jdbcDriver = "com.mysql.jdbc.Driver";
    private String dbAddress = "jdbc:mysql://localhost:3306/";
    private String userPass = "?user=root&password=1";

    private String dbName = "test";
    private String userName = "root";
    private String password = "1";

    private String[] colsFromBase;

    private Statement statement;
    private Connection connection;

    private int currentColumnsCount = 0;

    public MainWindowController() {
    }


    @FXML
    private void initialize() throws IOException {
        connect();
        initCurrentDataBase();
        //initTableList();
        //initTable();
    }

    private void connect(){
        try {
            Class.forName(jdbcDriver);
            connection = DriverManager.getConnection(dbAddress, userName, password);
            statement = connection.createStatement();
            String[] databases;

            //ResultSetMetaData md = rs.getMetaData();

            //statement.executeUpdate("SHOW DATABASES");
        } catch (SQLException | ClassNotFoundException exception){
            exception.printStackTrace();
            print("Exception");
        }
    }

    private void initCurrentDataBase(){
        ArrayList<String> bases = getDataBases();

        currentDataBase.getItems().addAll(bases);

        currentDataBase.valueProperty().addListener((ov, t, t1) -> {
            try {
                dbName = currentDataBase.getValue();
                connection = DriverManager.getConnection(dbAddress+dbName, userName, password);
                statement = connection.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            initTableList();

        });

    }

    private void initTableList(){
        tableList.getItems().clear();
        tableList.getItems().addAll(FXCollections.observableList(getTables(currentDataBase.getValue())));
        tableList.setOnMouseClicked(event -> {
            initTable();
        });
    }

    private void initTable(){

        try {
            table.getItems().clear();
            table.getColumns().clear();
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM " + tableList.getSelectionModel().getSelectedItem());
            ResultSetMetaData md = rs.getMetaData();

            currentColumnsCount = md.getColumnCount();

            colsFromBase = new String[currentColumnsCount];
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                final int j = i;
                colsFromBase[i] = rs.getMetaData().getColumnName(i+1);
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                col.setMinWidth(100);
                table.getColumns().addAll(col);
            }
            while(rs.next()){

                ObservableList row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){

                    row.add(rs.getString(i));
                }
                data.add(row);

            }
            table.setItems((ObservableList) data);
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }


    private ArrayList<String> getDataBases(){
        ArrayList<String> bases = new ArrayList<>();
        try {
            DatabaseMetaData dbmd = connection.getMetaData();
            ResultSet rs = dbmd.getCatalogs();
            while (rs.next())
             bases.add(rs.getString(1));
            rs.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            print("Exception");
        }
        return bases;
    }

    private ArrayList<String> getTables(String dbName){
        ArrayList<String> tables = new ArrayList<>();
        try {
            DatabaseMetaData dbmd = connection.getMetaData();
            ResultSet rs = dbmd.getTables(dbName, "", "%",null);
            while (rs.next())
                tables.add(rs.getString(3));
            rs.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            print("Exception");
        }
        return tables;
    }

    private void print(String stringToPrint){
        System.out.println(stringToPrint);
    }

    private void sendNewData(String[] data){
        StringBuilder sql_request = new StringBuilder("INSERT INTO " +
                tableList.getSelectionModel().getSelectedItem() + " VALUES (");
        for(int i = 0; i < data.length; i++){
            sql_request.append("'"+data[i]);

            if(i!=data.length-1)
                sql_request.append("',");
            else
                sql_request.append("'");
        }
        sql_request.append(");");
        try {
            statement.executeUpdate(String.valueOf(sql_request));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initTable();
    }

    @FXML
    public void newData(){
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Input Data");

        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField[] colName = new TextField[table.getColumns().size()];

        for (int i = 0; i < colName.length; i++) {
            String columnName = colsFromBase[i];
            colName[i] = new TextField(columnName);
            colName[i].setPromptText(columnName);
            gridPane.add(colName[i], i, 1);
            gridPane.add(new Label(columnName), i, 0);

        }


        dialog.getDialogPane().setContent(gridPane);

        Platform.runLater(() -> {
            for (int i = 0; i < colName.length; i++) {
            colName[i].requestFocus();
            }
        });

        String[] cols = new String[colName.length];

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                for (int i = 0; i < colName.length; i++) {
                    cols[i] = colName[i].getText();
                }
                return null;
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        sendNewData(cols);


    }

}