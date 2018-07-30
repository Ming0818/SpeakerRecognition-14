package controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ChoiceOfNumberOfSamplesController {
	@FXML
	ChoiceBox cbSpeakers, cbSamples,cbSeconds;
	@FXML
	Button bStartRecording,bBack;
	Scene parentScene;
	@FXML
	Label lCounting;
	Stage primaryStage;

	public void setStage(Stage stage) throws FileNotFoundException, ClassNotFoundException, IOException {

		primaryStage = stage;
	}

	public void setParentScene(Scene scene)
	{
		parentScene=scene;
	}
	@FXML
	public void initialize() {

		List<Integer> listOfNumberOfSamples = new ArrayList<Integer>();
		listOfNumberOfSamples.add(2);

		listOfNumberOfSamples.add(5);
		listOfNumberOfSamples.add(10);
		listOfNumberOfSamples.add(15);
		ObservableList obListOfNumberOfSamples = FXCollections.observableList(listOfNumberOfSamples);
		cbSamples.setItems(obListOfNumberOfSamples);
		cbSamples.getSelectionModel().selectFirst();

		List<Integer> listOfNumberOfSpeakers = new ArrayList<Integer>();
		listOfNumberOfSpeakers.add(2);
		listOfNumberOfSpeakers.add(3);
		listOfNumberOfSpeakers.add(4);
		ObservableList obListOfNumberOfSpeakers = FXCollections.observableList(listOfNumberOfSpeakers);
		cbSpeakers.setItems(obListOfNumberOfSpeakers);

		cbSpeakers.getSelectionModel().selectFirst();
		
		List<Integer> listOfSeconds = new ArrayList<Integer>();
		listOfSeconds.add(1);
		listOfSeconds.add(2);
		listOfSeconds.add(3);
		listOfSeconds.add(4);
		listOfSeconds.add(5);

		ObservableList obListOfSeconds = FXCollections.observableList(listOfSeconds);
		cbSeconds.setItems(obListOfSeconds);

		cbSeconds.getSelectionModel().selectFirst();

	}
	@FXML
	public void ActionOnBack(ActionEvent event) throws InterruptedException, Exception {
		primaryStage.setScene(parentScene);

	}
	public void ActionOnRecording(ActionEvent event) throws InterruptedException, Exception {

		Integer speakers = (Integer) cbSpeakers.getSelectionModel().getSelectedItem();
		Integer samples = (Integer) cbSamples.getSelectionModel().getSelectedItem();
		Integer seconds = (Integer) cbSeconds.getSelectionModel().getSelectedItem();

		 FXMLLoader loader=new FXMLLoader();
		  loader.setLocation(this.getClass().getResource("RecordSamples.fxml"));
		  AnchorPane stack=loader.load();
		  RecordSamplesController Controller = loader.getController();
		  Scene scena=new Scene(stack);
		  primaryStage.setScene(scena);
		 	primaryStage.setTitle("Recording");
		 		primaryStage.show();
		 		Controller.setStage(primaryStage,samples,speakers,seconds);
		

	}

}
