package com.pavellsda.mysqladmin.utils;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by pavellsda on 28.01.17.
 */
public class TableConstructor {

    private String tableName;
    private HashMap columns;
    private String primaryKey;

    public TableConstructor(){
        columns = new HashMap();
        createDialog();
    }

    public void setTableName(String tableName){this.tableName = tableName;}
    public String getTableName(){return this.tableName;}

    public void addColumns(HashMap columns){this.columns.putAll(columns);}
    public HashMap getColumns(){return this.columns;}

    public void setPrimaryKey(String primaryKey){this.primaryKey = primaryKey;}
    public String getPrimaryKey(){return this.primaryKey;}

    private void createDialog(){
        Dialog dialog = new Dialog();
        dialog.setTitle("New Table");

        ButtonType acceptButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(acceptButton, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField textField = new TextField();

        gridPane.add(textField, 0, 1);
        gridPane.add(new Label("Table name:"), 0, 0);

        dialog.getDialogPane().setContent(gridPane);

        Optional result = dialog.showAndWait();

        setTableName(textField.getText());
        System.out.println(getTableName());
    }


}
