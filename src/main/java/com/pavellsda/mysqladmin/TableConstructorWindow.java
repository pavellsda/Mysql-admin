package com.pavellsda.mysqladmin;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by pavellsda on 28.01.17.
 */
public class TableConstructorWindow {
    private static GridPane load;
    public TableConstructorWindow () throws  Exception {
        String fxmlFile = "/view/tableConstructor.fxml";
        FXMLLoader loader = new FXMLLoader();
        load = loader.load(getClass().getResourceAsStream(fxmlFile));
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(load,750,500);
        stage.setScene(scene);
        stage.show();

    }
    public static GridPane getParent(){return load;}
}
