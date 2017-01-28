package com.pavellsda.mysqladmin.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Optional;
import java.util.Vector;

/**
 * Created by pavellsda on 28.01.17.
 */
public class TableConstructorController {

    @FXML
    public TextField tableNameField;
    public Button addColumnButton;
    public GridPane gridPane;
    @FXML
    private GridPane parent;

    private int rows = 1;
    private Vector<String> colNames;
    private Vector<String> colTypes;

    @FXML
    private void initialize() throws IOException {
        colNames = new Vector<>();
        colTypes = new Vector<>();
    }
    public void addColumn() {
        TextField columnName = new TextField();
        TextField typeColumn = new TextField();

        Vector<String> column = createDialog();

        columnName.setText(column.get(0));
        typeColumn.setText(column.get(1));

        colNames.add(columnName.getText());
        colTypes.add(typeColumn.getText());

        gridPane.add(columnName,0,rows);
        gridPane.add(typeColumn,1,rows);
        rows++;
    }

    private Vector<String> createDialog(){
        TextField columnName = new TextField();
        TextField typeColumn = new TextField();

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Input Data");

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


        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        Vector<String> column = new Vector<>();
        column.add(columnName.getText());
        column.add(typeColumn.getText());

        return column;
    }

}
