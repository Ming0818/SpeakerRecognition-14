package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javafx.application.Platform;
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

public class LoadExistingSamplesController {

	@FXML
	Label lProcess, lProcess1;
	@FXML
	Button bLoad, bRecord, bRecognize, bLoadtoRecognize,bBack;
	@FXML
	ProgressBar pbProgress;
	Stage primaryStage;
	Scene parentScene;
	private LoadSample task;
	private boolean loadSampleTaskIsRunning=false;
	List<ProcessingAudio> listOfProcessedSamples = new Vector<ProcessingAudio>();
	ProcessingAudio sampleToRecognize;
	int numberOfSpeakers = 0;
	public void setParentScene(Scene scene)
	{
		parentScene=scene;
	}
	class LoadSample extends Task<Void> {
		Pattern compiledPattern = Pattern.compile("no.");
		final File folder = new File("src\\samples");
		int i = 0;
		int numberOfSpeakerHelper[] = { 0, 0, 0, 0, 0 };

		@Override
		protected Void call() throws Exception {
			loadSampleTaskIsRunning=true;

			Matcher matcher;

			for (File fileEntry : folder.listFiles()) {
				 if (isCancelled())
		            {
		                throw new InterruptedException();
		            }
				i++;
				updateProgress(i, folder.listFiles().length);
				matcher = compiledPattern.matcher(fileEntry.getPath());
				matcher.find();

				Integer speakerID = Integer.parseInt(fileEntry.getPath().substring(matcher.end(), matcher.end() + 1));
				numberOfSpeakerHelper[speakerID]++;
				String subFileEntry = fileEntry.getPath().substring(matcher.end());

				matcher.reset();

				matcher = compiledPattern.matcher(subFileEntry);
				matcher.find();

				Integer sampleID = Integer.parseInt(subFileEntry.substring(matcher.end(), matcher.end() + 1));

				ProcessingAudio pAudio = new ProcessingAudio(fileEntry.getPath());
				pAudio.setSpeakerID(speakerID);
				pAudio.setSampleID(sampleID);

				listOfProcessedSamples.add(pAudio);

			}
			for (int i = 0; i < numberOfSpeakerHelper.length; i++) {
				if (numberOfSpeakerHelper[i] > 0) {
					numberOfSpeakers++;
				}
			}
			updateProgress(folder.listFiles().length + 1, folder.listFiles().length);
			loadSampleTaskIsRunning=false;

			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean cancel(boolean mayInterrupt) {
			updateMessage("cancel");
			return super.cancel(mayInterrupt);
		}

		@Override
		protected void updateProgress(double workdone, double max) {
			if (workdone <= max) {
				updateMessage("Processing " + (int)workdone + " file out of " + (int)max);
			} else {

				updateMessage("Processing finished");
				bRecord.setDisable(false);
				bLoadtoRecognize.setDisable(false);

			}
			super.updateProgress(workdone, max);

		}

	}

	class LoadSampleToRecognize extends Task<Void> {

		@Override
		protected Void call() throws Exception {

			updateMessage("Processing");
			System.out.println("task");
			System.out.println("task");
			System.out.println("task");
			System.out.println("task");

			sampleToRecognize = new ProcessingAudio("src\\sampleToRecognize\\SampleToRecognize.wav");
			bRecognize.setDisable(false);
			updateMessage("Finished");

			return null;
		}

		@Override
		public boolean cancel(boolean mayInterrupt) {
			updateMessage("cancel");
			return super.cancel(mayInterrupt);
		}

	}

	public void setStage(Stage stage) throws FileNotFoundException, ClassNotFoundException, IOException {
		primaryStage = stage;
		pbProgress.setVisible(false);

	}

	public void ActionOnLoadSamples(ActionEvent event) throws InterruptedException, Exception {
		

		
		bLoad.setDisable(true);
		bRecord.setDisable(true);
		pbProgress.setVisible(true);

		task = new LoadSample();
		pbProgress.progressProperty().bind(task.progressProperty());
		lProcess.textProperty().bind(task.messageProperty());
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();

	}

	public void ActionOnRecord(ActionEvent event) throws InterruptedException, Exception {


		pbProgress.setVisible(false);

		bLoad.setDisable(true);
		bRecord.setDisable(true);
		bLoadtoRecognize.setDisable(true);

		

		
		    	Recorder record = new Recorder();
				record.main("src\\sampleToRecognize\\SampleToRecognize.wav",2);
		         // code goes here.
				sampleToRecognize = new ProcessingAudio("src\\sampleToRecognize\\SampleToRecognize.wav");


			bRecognize.setDisable(false);

	}
	@FXML
	public void ActionOnRecognize(ActionEvent event) throws InterruptedException, Exception {
		pbProgress.setVisible(false);

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
		primaryStage.setTitle("Results");
		primaryStage.show();
		Controller.setParentScene( ((Node) event.getSource()).getScene());
		Controller.setStage(primaryStage, listOfProcessedSamples, recognizer.getRecognizedSample());

	}

	public void ActionOnLoadToRecognize(ActionEvent event) throws InterruptedException, Exception {
		pbProgress.setVisible(false);

		bLoad.setDisable(true);
		bRecord.setDisable(true);
		bLoadtoRecognize.setDisable(true);
		LoadSampleToRecognize task = new LoadSampleToRecognize();
		lProcess.textProperty().bind(task.messageProperty());
		pbProgress.setVisible(false);
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();

	}
	public void ActionOnBack(ActionEvent event) throws InterruptedException, Exception {
		if(loadSampleTaskIsRunning)
		task.cancel();
		primaryStage.setScene(parentScene);

	}
}
