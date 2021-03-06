package com.pavellsda.mysqladmin.utils;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by pavellsda on 28.01.17.
 */
public class Utils {

    public static ContextMenu createPopupMenu(ArrayList<String> items){
        final ContextMenu popupMenu = new ContextMenu();
        for(String anItem:items){
            popupMenu.getItems().add(new MenuItem(anItem));
        }
        return popupMenu;
    }

    public static String subStr(String toSub){
        if (toSub == null || toSub.length() == 0) {
            return toSub;
        }
        return toSub.substring(1, toSub.length()-1);
    }

    public static void alert(String text) {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle("alert");
        Group group = new Group(new Text(25,25,text));
        stage.setScene(new Scene(group, 350, 100));
        stage.show();

    }

    public static Control createField(String colType){

        switch (colType) {

            case "INT":
                return new NumTextField(colType);

            case "INT UNSIGNED":
                return new NumTextField(colType);

            case "BIGINT":
                return new NumTextField(colType);

            case "BIGINT UNSIGNED":
                return new NumTextField(colType);

            case "SMALLINT":
                return new NumTextField(colType);

            case "SMALLINT UNSIGNED":
                return new NumTextField(colType);

            case "TYNYINT":
                return new NumTextField(colType);

            case "TYNYINT UNSIGNED":
                return new NumTextField(colType);

            case "DATETIME":
                return new TextField();

            case "DATE":
                return new DatePicker();

            case "CHAR":
                return new TextField();

            case "VARCHAR":
                return new TextField();

            default:
                return new TextField();
        }
    }

    public static StringBuilder createSqlRequestToDelete(String colFromBase, String colTypeFromBase,
                                                         String dataToDelete, int count, int size){
        StringBuilder sqlRequest = new StringBuilder();

        sqlRequest.append(colFromBase+"=");
        if(!Objects.equals(colTypeFromBase, "INT")||
                !Objects.equals(colTypeFromBase, "INT UNSIGNED")||
                !Objects.equals(colTypeFromBase, "MEDIUMINT")||
                !Objects.equals(colTypeFromBase, "MEDIUMINT UNSIGNED")||
                !Objects.equals(colTypeFromBase, "BIGINT")||
                !Objects.equals(colTypeFromBase, "BIGINT UNSIGNED")||
                !Objects.equals(colTypeFromBase, "SMALLINT")||
                !Objects.equals(colTypeFromBase, "SMALLINT UNSIGNED")||
                !Objects.equals(colTypeFromBase, "TYNYINT")||
                !Objects.equals(colTypeFromBase, "TYNYINT UNSIGNED")
                ) {

            sqlRequest.append("\"");
            sqlRequest.append(dataToDelete);
            sqlRequest.append("\"");

        } else{

            sqlRequest.append(dataToDelete);
        }
        if(count!=size-1)
            sqlRequest.append(" AND ");

        return sqlRequest;
    }

    public static ArrayList<String> getTypes(){
        ArrayList<String> types = new ArrayList<String>();

        types.add("INT");
        types.add("TINYINT");
        types.add("SMALLINT");
        types.add("MEDIUMINT");
        types.add("BIGINT");
        types.add("DATETIME");
        types.add("DATE");
        types.add("VARCHAR");
        types.add("CHAR");

        return types;
    }

    private void print(String stringToPrint){
        System.out.println(stringToPrint);
    }


}
