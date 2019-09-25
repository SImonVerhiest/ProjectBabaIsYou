/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package babaisyou;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author simon
 */
public class BabaIsYou extends Application {
    
    Stage window;
    Scene scene_1, scene_2;
    
    private int x = 100, y=100;
    
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        window = primaryStage;
        
        // Label 1st page
        Label label_1 = new Label("BabaIsYou");
        label_1.setFont(new Font("Arial", 30));
        
        //Label 2nd page
        Label label_2 = new Label("Page 2");
        
        // Button to access the game
        Button btn = new Button();
        btn.setText("Play");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                //System.out.println("Let's play");
                window.setScene(scene_2);
            }
        });
        // Button to access the game 1st page
        Button btn_exit = new Button();
        btn_exit.setText("Exit");
        btn_exit.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                //System.out.println("You closed the tab");
                window.close();
            }
        });
        
        
        // Button to access the game 2nd page
        Button btn_exit2 = new Button();
        btn_exit2.setText("Exit");
        btn_exit2.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                //System.out.println("You closed the tab");
                window.close();
            }
        });
        
        //Indicate how to place the label and buttons 1st page
        VBox vbox_1 = new VBox(15);
        vbox_1.setAlignment(Pos.CENTER);
        vbox_1.getChildren().addAll(label_1,btn, btn_exit);
        
        //Indicate how to place the label and buttons 2nd page
        GridPane vbox_2 = new GridPane();
        
        
        vbox_2.setGridLinesVisible(true);
        
        
        
        
        GridPane.setConstraints(btn_exit, 20, 20);
        GridPane.setConstraints(label_2,0,0);
        vbox_2.getChildren().setAll(btn_exit2);
        
        final ImageView selectedImage = new ImageView();   
        Image image1 = new Image(new FileInputStream("D:\\Documents\\Cours 2017-2018\\Projet\\netbean\\BabaIsYou\\src\\babaisyou\\header.jpg"));

        selectedImage.setImage(image1);

        vbox_1.getChildren().addAll(selectedImage);

        //Create the scene
        scene_1 = new Scene(vbox_1, 500, 500, Color.BROWN);
        scene_2 = new Scene(vbox_2, 500, 500, Color.RED);
        
        //test

       
        window.setScene(scene_1);
        window.setTitle("BabaIsYou");
        window.show();
        
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
