package controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import calculations.DTW;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import recognize.Recognizer;
import wavProcessing.ProcessingAudio;
import wavProcessing.Recorder;

public class RecordSamplesController {

	Integer sampleNumber, speakerNumber, numberOfSamples, numberOfSpeakers, seconds;
	boolean finished = false;
	@FXML
	Label lSpeakerNumber;
	@FXML
	Button bStart, bRecognize;
	Stage primaryStage;
	Scene parentScene;
	Recorder record = new Recorder();

	private RecordSamples recordSamples;
	List<ProcessingAudio> listOfProcessedSamples = new Vector<ProcessingAudio>();
	ProcessingAudio sampleToRecognize;

	public void setStage(Stage stage, Integer numberOfSamples, Integer numberOfSpeakers, Integer seconds)
			throws FileNotFoundException, ClassNotFoundException, IOException {
		primaryStage = stage;
		this.numberOfSamples = numberOfSamples;
		this.numberOfSpeakers = numberOfSpeakers;
		this.seconds = seconds;
	}

	public void setParentScene(Scene scene) {
		parentScene = scene;
	}

	@FXML
	public void initialize() {
		sampleNumber = 1;
		speakerNumber = 1;
		lSpeakerNumber.setText("Speaker no." + speakerNumber + " sample no." + sampleNumber);
		bStart.setText("Start");


	}

	public void Processing(int speakerNumber, int sampleNumber) {
		ProcessingAudio pAudio = new ProcessingAudio(
				"src\\samples\\Speaker no." + speakerNumber + " sample no." + sampleNumber + ".wav");
		pAudio.setSpeakerID(speakerNumber);
		pAudio.setSampleID(sampleNumber);

		listOfProcessedSamples.add(pAudio);

	}


	class RecordSamples extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			if (!finished) {
				bStart.setDisable(true);

				
				updateMessage("Recording");

				record.main("src\\samples\\Speaker no." + speakerNumber + " sample no." + sampleNumber + ".wav",
						seconds);
				updateMessage("Processing");
				Processing(speakerNumber, sampleNumber);

				/*Thread t1 = new Thread(new Runnable() {
					public void run() {
						Processing(speakerNumber, sampleNumber);

					}
				});
				t1.start();*/
 				bStart.setDisable(false);

			} else {
				updateMessage("Recording");

				Recorder record = new Recorder();
				record.main("src\\sampleToRecognize\\SampleToRecognize.wav", seconds);
				updateMessage("Processing");

				sampleToRecognize = new ProcessingAudio("src\\sampleToRecognize\\SampleToRecognize.wav");
				updateMessage("All samples recorded");
				bStart.setDisable(true);

				bRecognize.setDisable(false);
				// Recognize();

			}

			sampleNumber++;
			if (sampleNumber > numberOfSamples) {

				Thread.sleep(1000);

				sampleNumber = 1;
				speakerNumber++;

			}
			if (speakerNumber > numberOfSpeakers) {
				// lSpeakerNumber.setText("Record test sample to recognize");
				// bStart.setText("Start");
				updateMessage("Record sample to recognize");

				finished = true;

			} else {
				updateMessage("Speaker no." + speakerNumber + " sample no." + sampleNumber);

			}

			return null;
		}
		


	}

	@FXML
	public void ActionOnRecognize(ActionEvent event) throws InterruptedException, Exception {
		Recognizer recognizer = new Recognizer(listOfProcessedSamples, sampleToRecognize, numberOfSpeakers);
		recognizer.Recognize();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("DisplayResults.fxml"));
		AnchorPane stack = loader.load();
		DisplayResultsController Controller = loader.getController();

		Controller.setRecognizedSample(recognizer.getRecognizedSample());
		Controller.setSampleToRecognize(sampleToRecognize);
		Scene scena = new Scene(stack);
		primaryStage.setScene(scena);
		primaryStage.setTitle("Recording");
		primaryStage.show();
		Controller.setParentScene(((Node) event.getSource()).getScene());
		Controller.setStage(primaryStage, listOfProcessedSamples, recognizer.getRecognizedSample());

	}

	public void ActionOnRecording(ActionEvent event) throws InterruptedException, Exception {

		recordSamples = new RecordSamples();
		lSpeakerNumber.textProperty().bind(recordSamples.messageProperty());

		Thread th = new Thread(recordSamples);
		th.setDaemon(true);
		th.start();
		
	

	}

}
