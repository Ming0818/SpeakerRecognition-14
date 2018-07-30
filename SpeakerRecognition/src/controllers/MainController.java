package controllers;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calculations.DTW;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import wavProcessing.ProcessingAudio;

public class MainController implements Serializable {
	// @FXML LineChart<Number,Number> lineChart;
	// @FXML final NumberAxis xAxis = new NumberAxis();
	@FXML
	Label lResult, lInfo;
	@FXML
	ImageView imageView;
	@FXML
	Button bRecord, bLoad;

	@FXML
	Button bStart;

	XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
	Stage primaryStage;
	List<ProcessingAudio> listSpeaker1;
	List<ProcessingAudio> listSpeaker3;
	List<ProcessingAudio> listSpeaker2;
	String speaker = "";
	List<ProcessingAudio> listOfProcessedSamples = new Vector<ProcessingAudio>();

	private static final long serialVersionUID = 1L;

	public void setStage(Stage stage) throws FileNotFoundException, ClassNotFoundException, IOException {

		primaryStage = stage;
		
	}

	@FXML
	public void initialize() {

	}

	@FXML
	public void ActionOnButtonRecord(ActionEvent event) throws InterruptedException, Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("ChoiceOfNumberOfSamples.fxml"));
		AnchorPane stack = loader.load();
		
		ChoiceOfNumberOfSamplesController Controller = loader.getController();
		Scene scena = new Scene(stack);
		primaryStage.setScene(scena);
		primaryStage.setTitle("Recording options");
		primaryStage.show();
		Controller.setStage(primaryStage);

		Controller.setParentScene(((Node) event.getSource()).getScene());

	}

	public void ActionOnLoadSamples(ActionEvent event) throws InterruptedException, Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("LoadExistingSamples.fxml"));
		AnchorPane stack = loader.load();
		LoadExistingSamplesController Controller = loader.getController();
		Scene scena = new Scene(stack);
		primaryStage.setScene(scena);

		primaryStage.setTitle("Loading");
		primaryStage.show();
		Controller.setStage(primaryStage);
		Controller.setParentScene(((Node) event.getSource()).getScene());

	}

}
