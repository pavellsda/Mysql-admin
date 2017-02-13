package com.pavellsda.mysqladmin.controllers;

import com.pavellsda.mysqladmin.utils.NumTextField;
import com.pavellsda.mysqladmin.utils.Table;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Vector;

import static com.pavellsda.mysqladmin.utils.Utils.alert;
import static com.pavellsda.mysqladmin.utils.Utils.getTypes;

/**
 * Created by pavellsda on 28.01.17.
 */
public class TableConstructorController {

    @FXML
    public TextField tableNameField;
    public Button addColumnButton;
    public Button createTableButton;
    public TableView<ObservableList> tableView;

    private Vector<String> colNames;
    private Vector<String> colTypes;
    private Table createdTable;
    private Stage stage;
    private MainWindowController mainController;
    private String primaryKey;

    @FXML
    private void initialize() throws IOException {
        colNames = new Vector<>();
        colTypes = new Vector<>();

        initTable();
    }

    private void initTable() {

    }

    public void addColumn() {
        Vector<String> column = createDialog();
        if(column!=null)
            addColumnToTable(column.get(0), column.get(1), column.get(2));

    }

    public void createTable() throws SQLException {
        String tableName = tableNameField.getText();
        String[] columnsNames = new String[colNames.size()];
        String[] columnsTypes = new String[colTypes.size()];
        String primaryKey = this.primaryKey;

        for(int i = 0; i < colNames.size(); i++) {
            columnsNames[i] = colNames.get(i);
            columnsTypes[i] = colTypes.get(i);
        }

        if(tableName.length()>=1) {
            createdTable = new Table(tableName, columnsNames, columnsTypes, primaryKey);
            deleteStage(this.stage);
        }
        else {
            alert("Table name is empty!");
        }
    }

    void setStage(Stage stage){
        this.stage = stage;
    }

    private void deleteStage(Stage stage) throws SQLException {
        stage.hide();
        stage.close();
        mainController.sendTableToDataBase(createdTable);
    }

    void setMainController(MainWindowController mainController) {
        this.mainController = mainController;
    }

    private void addColumnToTable(String columnName, String columnType, String size) {

        tableView.getColumns().clear();

        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        ObservableList<String> row = FXCollections.observableArrayList();

        if(!Objects.equals(size, ""))
            columnType = columnType + "(" + size + ")";

        colNames.add(columnName);
        colTypes.add(columnType);

        for(int i = 0; i < colTypes.size(); i++){
            TableColumn column = new TableColumn(colNames.get(i));
            int finalI = i;
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(finalI).toString());
                }
            });

            column.prefWidthProperty().bind(tableView.widthProperty().divide(4));
            tableView.getColumns().addAll(column);
            row.add(colTypes.get(i));
        }
        data.add(row);
        tableView.setItems(data);
    }

    private Vector<String> createDialog(){
        TextField columnNameTextField = new TextField();
        ComboBox<String> typeColumnComboBox = new ComboBox<String>();
        NumTextField sizeField = new NumTextField();
        CheckBox primaryKeyCheckBox = new CheckBox();
        Vector<String> columnData = new Vector<>();

        typeColumnComboBox.getItems().addAll(getTypes());

        columnNameTextField.setMinWidth(150);
        typeColumnComboBox.setMinWidth(150);
        sizeField.setMinWidth(150);
        primaryKeyCheckBox.setMinWidth(150);

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Column Creator");

        ButtonType acceptButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(acceptButton, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        gridPane.add(columnNameTextField, 0, 1);
        gridPane.add(new Label("Column Name:"), 0, 0);

        gridPane.add(typeColumnComboBox, 1, 1);
        gridPane.add(new Label("Type:"), 1, 0);

        gridPane.add(sizeField, 2, 1);
        gridPane.add(new Label("Size:"), 2, 0);

        if(primaryKey==null) {
            gridPane.add(primaryKeyCheckBox, 3, 1);
            gridPane.add(new Label("PrimaryKey:"), 3, 0);
        }

        primaryKeyCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                primaryKey = columnNameTextField.getText();
            }
        });

        typeColumnComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                if(Objects.equals(typeColumnComboBox.getValue(), "DATETIME") ||
                        Objects.equals(typeColumnComboBox.getValue(), "DATE") ||
                        Objects.equals(typeColumnComboBox.getValue(), "CHAR"))
                   sizeField.setEditable(false);

                else
                    sizeField.setEditable(true);
            }
        });

        dialog.getDialogPane().setContent(gridPane);

        dialog.showAndWait();

        if(!columnNameTextField.getText().isEmpty() ||
                (typeColumnComboBox.getValue()!=null)){

            columnData.add(columnNameTextField.getText());
            columnData.add(typeColumnComboBox.getValue());
            columnData.add(sizeField.getText());

            return columnData;

        } else{
            alert("Fields empty");
            return null;
        }

    }

    Table getCreatedTable() {
        return this.createdTable;
    }
}
