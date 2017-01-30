package com.pavellsda.mysqladmin.controllers;

import com.pavellsda.mysqladmin.utils.NumTextField;
import com.pavellsda.mysqladmin.utils.Table;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Vector;

import static com.pavellsda.mysqladmin.utils.Utils.*;
import static jdk.nashorn.internal.objects.Global.print;

/**
 * Created by pavellsda on 21.01.17.
 */


public class MainWindowController {


    @FXML
    public ComboBox<String> currentDataBase;
    public ListView<String> tableList;
    public TableView table;
    public Button newDataButton;
    private ContextMenu popupMenu = new ContextMenu();
    private ContextMenu listPopupMenu = new ContextMenu();
    public Button createTable;

    private String jdbcDriver = "com.mysql.jdbc.Driver";
    private String dbAddress = "jdbc:mysql://localhost:3306/";
    private String dbName;
    private String userName = "root";
    private String password = "1";
    private String newTable;

    private String[] colsFromBase;
    private String[] colsTypeFromBase;

    private Statement statement;
    private Connection connection;

    public MainWindowController() {
    }


    @FXML
    private void initialize() throws IOException {
        initPopupMenu();
        connect();
        initCurrentDataBase();
    }

    private void initPopupMenu(){
        ArrayList<String> menuItems = new ArrayList<>();
        menuItems.add("Delete");
        menuItems.add("Close");

        listPopupMenu = createPopupMenu(menuItems);
        listPopupMenu.getItems().get(0).setOnAction((event) -> listMenuDelete());
        listPopupMenu.getItems().get(1).setOnAction((event) -> menuClose());

        popupMenu = createPopupMenu(menuItems);
        popupMenu.getItems().get(0).setOnAction((event) -> menuDelete());
        popupMenu.getItems().get(1).setOnAction((event) -> menuClose());

    }

    private void connect(){
        try {
            Class.forName(jdbcDriver);
            connection = DriverManager.getConnection(dbAddress, userName, password);
            statement = connection.createStatement();

        } catch (SQLException | ClassNotFoundException exception){
            exception.printStackTrace();
            print("Exception");
        }
    }

    /*
    Function for initialize JavaFX elements
     */

    private void initCurrentDataBase(){
        currentDataBase.getItems().clear();

        ArrayList<String> bases = getDataBases();

        currentDataBase.getItems().addAll(bases);

        currentDataBase.valueProperty().addListener((ov, t, t1) -> {
            try {
                dbName = getCurrentDataBase();

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
            if (event.getButton() == MouseButton.PRIMARY) {
                initTable();
            } else {
                listPopupMenu.show(tableList, event.getScreenX(), event.getScreenY());
            }

        });

    }

    private void initTable(){
        try {
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            Statement stmt = connection.createStatement();
            StringBuilder sql_request = new StringBuilder("SELECT * FROM ");
            if(tableList.getSelectionModel().getSelectedItems()!=null)
                sql_request.append(tableList.getSelectionModel().getSelectedItem());
            else
                sql_request.append(newTable);
            ResultSet rs = stmt.executeQuery(sql_request.toString());
            ResultSetMetaData md = rs.getMetaData();

            int currentColumnsCount = md.getColumnCount();
            colsFromBase = new String[currentColumnsCount];
            colsTypeFromBase = new String[currentColumnsCount];

            table.getItems().clear();
            table.getColumns().clear();

            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                colsTypeFromBase[i] = String.valueOf(rs.getMetaData().getColumnTypeName(i+1));
                final int j = i;
                colsFromBase[i] = rs.getMetaData().getColumnName(i+1);
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                col.prefWidthProperty().bind(table.widthProperty().divide(4));
                table.getColumns().addAll(col);
            }
            while(rs.next()){

                ObservableList row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){

                    row.add(rs.getString(i));
                }
                data.add(row);

            }
            table.setItems(data);
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }

        table.setOnMouseClicked(event -> {
            if (event.isControlDown())
                table.getSelectionModel().setSelectionMode(
                        SelectionMode.MULTIPLE);
            else
                table.getSelectionModel().setSelectionMode(
                        SelectionMode.SINGLE);

            if (event.getButton() == MouseButton.SECONDARY)
                popupMenu.show(table, event.getScreenX(), event.getScreenY());
        });

    }

    /*
    Buttons handlers
     */
    @FXML
    public void createTable(){

        if(currentDataBase.getValue() != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/tableConstructor.fxml"));
                Parent root = fxmlLoader.load();
                TableConstructorController controller=fxmlLoader.getController();

                Stage stage = new Stage();
                stage.setTitle("Table Creator");
                stage.setScene(new Scene(root));
                stage.show();

                controller.setStage(stage);
                controller.setMainController(this);

                if(!stage.isShowing())
                    sendTableToDataBase(controller.getCreatedTable());

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        else
            alert("\n" +
                    "First select the database");
    }

    @FXML
    public void newData(){
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Input Data");

        ButtonType acceptButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(acceptButton, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        Vector fields = new Vector<>();

        for (int i = 0; i < table.getColumns().size(); i++) {
            String columnName = colsFromBase[i];
            String columnType = colsTypeFromBase[i];

            fields.add(createField(columnType));

            gridPane.add((Node)fields.get(i), i, 1);
            gridPane.add(new Label(columnName), i, 0);

        }

        dialog.getDialogPane().setContent(gridPane);

        String[] cols = new String[fields.size()];

        dialog.setResultConverter(dialogButton -> {
            for (int i = 0; i < fields.size(); i++){
                Object field = fields.get(i);
                String className = field.getClass().getSimpleName();
                switch (className){
                    case "NumTextField":
                        cols[i] = NumTextField.class.cast(field).getText();
                        break;
                    case "TextField":
                        cols[i] =  TextField.class.cast(field).getText();
                        break;
                    case "DatePicker":
                        cols[i] =  DatePicker.class.cast(field).getValue().toString();
                        break;
                }
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        sendNewData(cols);
    }

    /*
    Popup menu handlers
     */

    private void menuDelete(){
        if(table.getSelectionModel().getSelectedItems().size()>0) {
            ObservableList itemsToDelete = table.getSelectionModel().getSelectedItems();
            StringBuilder strToDelete;
            for (int i = 0; i < itemsToDelete.size(); i++){
                strToDelete = new StringBuilder("");
                String anItemsToDelete = itemsToDelete.get(i).toString();
                anItemsToDelete = subStr(anItemsToDelete);
                String[] dataToDelete = anItemsToDelete.split(",");

                for(int j = 0; j < dataToDelete.length; j++){
                    if(j!=0)
                        dataToDelete[j] = dataToDelete[j].substring(1,dataToDelete[j].length());

                    strToDelete.append(createSqlRequestToDelete(colsFromBase[j],
                            colsTypeFromBase[j],dataToDelete[j], j, dataToDelete.length));
                }
                deleteData(strToDelete, i, itemsToDelete.size());
            }
        }
    }

    private void listMenuDelete() {
        if (tableList.getSelectionModel().getSelectedItems().size() > 0) {
            ObservableList itemsToDelete = tableList.getSelectionModel().getSelectedItems();
            deleteTable(subStr(itemsToDelete.toString()));
        }
    }

    private void menuClose(){
        popupMenu.hide();
    }

    /*
    Functions for working with database
     */

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


    void sendTableToDataBase(Table table) throws SQLException {
        StringBuilder sql_request = new StringBuilder("CREATE TABLE " +
                table.getTableName()+ " (");

        for(int i = 0; i < table.getColumnsNames().length; i++){
            sql_request.append(table.getColumnsNames()[i]).append(" ").append(table.getColumnsTypes()[i]);

            if(i!=table.getColumnsNames().length-1)
                sql_request.append(",");
            else
                sql_request.append(");");
        }

        statement.executeUpdate(String.valueOf(sql_request));

        newTable = table.getTableName();
        updateDataBase();
    }

    private void deleteData(StringBuilder toDelete, int count, int size) {
        StringBuilder sql_request = new StringBuilder("DELETE FROM " +
                tableList.getSelectionModel().getSelectedItem() + " WHERE ");
        sql_request.append(toDelete).append(";");

        try {
            statement.executeUpdate(String.valueOf(sql_request));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(count==size-1)
            initTable();
    }

    private void deleteTable(String toDelete){
        StringBuilder sql_request = new StringBuilder("DROP TABLE " +toDelete +";");
        print(sql_request.toString());
        try {
            statement.executeUpdate(String.valueOf(sql_request));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateDataBase();
    }

    private void updateDataBase(){
        try {
            dbName = getCurrentDataBase();

            connection = DriverManager.getConnection(dbAddress+dbName, userName, password);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initTableList();
    }

    private String getCurrentDataBase() { return currentDataBase.getValue(); }



}