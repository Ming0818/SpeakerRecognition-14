package main;
 import sun.audio.AudioStream;

import java.io.*;
import java.util.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
 
 
public class Main_sound extends Application {

	public static void main(String[] args) throws Exception {
		launch(args);

   
  	
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
	

		// TODO Auto-generated method stub
		//pracownicy.wczytaj();
		// TODO Auto-generated method stub
  FXMLLoader loader=new FXMLLoader();
 loader.setLocation(this.getClass().getResource("Main_fx.fxml"));
 AnchorPane stack=loader.load();
 Chart_Contr Controller = loader.getController();
 Scene scena=new Scene(stack);
 primaryStage.setScene(scena);
	primaryStage.setTitle("Lab1");
		primaryStage.show();
		Controller.setStage(primaryStage);
	}
}
