//package com.tezkit.wallet.view;
//
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.VBox;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class Main extends AnchorPane {
////<com.tezkit.wallet.view.Main prefHeight="191.0" prefWidth="442.0" xmlns="http://javafx.com/javafx/8.0.999-ea" xmlns:fx="http://javafx.com/fxml/1" fx:controller="com.tezkit.wallet.view.Empty">
//
////    @Autowired
////    ViewLoader viewLoader;
//
//    @FXML
//    ScrollPane contentArea;
//
//    public Main () throws IOException {
//        try {
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("/views/MainFrame.fxml"));
//            loader.setController("com.tezkit.wallet.view.Main");
//            loader.setRoot(this);
//            this.getStylesheets().add(getClass().getResource("/tezwallet_styling.css").toExternalForm());
//
//            AnchorPane mainFrame = loader.load();
//
//            this.setPrefHeight(612);
//            this.setPrefWidth(931);
//
////            contentArea.
////            contentArea.setContent(viewLoader.loadView("/views/Dashboard.fxml"));
////            this.getChildren().add(hbox);
////            var pane = new Pane();
////            pane.setMaxHeight(100);
////            pane.setMaxWidth(100);
////            for (int i = 0; i < hbox.getChildren().size(); i++) {
////            System.out.print(mainFrame.getChildren().size());
//
////            pane.getChildren().addAll(mainFrame.getChildren());
////            }
//
////            contentArea.setContent(pane);
//        } catch (IOException exc) {
//            // handle exception
//            throw exc;
//        }
//    }
//
//    @Override
//    public ObservableList<Node> getChildren() {
//        return super.getChildren();
//    }
//
//    public void handleButtonClick() {}
//
//}
