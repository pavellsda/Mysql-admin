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
import javafx.util.Pair;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;

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

        addColumnToTable(column.get(0), column.get(1), column.get(2));

    }

    public void createTable(){
        String tableName = tableNameField.getText();
        String[] columnsNames = new String[colNames.size()];
        String[] columnsTypes = new String[colTypes.size()];
        String primaryKey = this.primaryKey;

        for(int i = 0; i < colNames.size(); i++) {
            columnsNames[i] = colNames.get(i);
            columnsTypes[i] = colTypes.get(i);
        }

        createdTable = new Table(tableName, columnsNames, columnsTypes, primaryKey);

        deleteStage(this.stage);
    }

    void setStage(Stage stage){
        this.stage = stage;
    }

    private void deleteStage(Stage stage){
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
        TextField columnName = new TextField();
        ComboBox<String> typeColumn = new ComboBox<String>();
        NumTextField size = new NumTextField();
        CheckBox primary = new CheckBox();

        typeColumn.getItems().addAll(getTypes());

        columnName.setMinWidth(150);
        typeColumn.setMinWidth(150);
        size.setMinWidth(150);
        primary.setMinWidth(150);

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Column Creator");

        ButtonType acceptButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(acceptButton, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        gridPane.add(columnName, 0, 1);
        gridPane.add(new Label("Column Name:"), 0, 0);

        gridPane.add(typeColumn, 1, 1);
        gridPane.add(new Label("Type:"), 1, 0);

        gridPane.add(size, 2, 1);
        gridPane.add(new Label("Size:"), 2, 0);

        if(primaryKey==null) {
            gridPane.add(primary, 3, 1);
            gridPane.add(new Label("PrimaryKey:"), 3, 0);
        }

        primary.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                primaryKey = columnName.getText();
            }
        });

        typeColumn.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                if(Objects.equals(typeColumn.getValue(), "DATETIME") ||
                        Objects.equals(typeColumn.getValue(), "DATE"))
                   size.setEditable(false);

                else
                    size.setEditable(true);
            }
        });

        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        Vector<String> column = new Vector<>();
        column.add(columnName.getText());
        column.add(typeColumn.getValue());
        column.add(size.getText());

        return column;
    }

    public Table getCreatedTable() {
        return this.createdTable;
    }
}
