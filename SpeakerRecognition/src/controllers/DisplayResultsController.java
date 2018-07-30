package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import wavProcessing.ProcessingAudio;

public class DisplayResultsController {

	@FXML
	private Label lResult;
	@FXML
	Button bPlayTest,bPlaySample,bBack;
	@FXML
	private RadioButton rbEnergy, rbCentroid, rbZCR,rbRollOff;
	@FXML
	private RadioButton rbMFCC1,rbMFCC2,rbMFCC3,rbMFCC4,rbMFCC5,rbMFCC6,rbMFCC7,rbMFCC8,rbMFCC9,rbMFCC10,rbMFCC11,rbMFCC12,rbMFCC13;
	ToggleGroup toggleGroup;
	@FXML
	ChoiceBox cbShowAnotherSample;
	private Stage primaryStage;
	Scene parentScene;

	private int recognizedSpeakerID;
	private int recognizedSampleID;
	private ProcessingAudio sampleToRecognize;
	private ProcessingAudio recognizedSample;
	private ProcessingAudio selectedSample;
	private List<ProcessingAudio> listOfProcessedSamples;
	@FXML
	LineChart<Number, Number> testChart, recognizedChart;

	public void setParentScene(Scene scene)
	{
		parentScene=scene;
	}
	public ProcessingAudio getSampleToRecognize() {
		return sampleToRecognize;
	}

	public void setSampleToRecognize(ProcessingAudio sampleToRecognize) {
		this.sampleToRecognize = sampleToRecognize;
	}

	public ProcessingAudio getRecognizedSample() {
		return recognizedSample;
	}

	public void setRecognizedSample(ProcessingAudio recognizedSample) {
		this.recognizedSample = recognizedSample;
		this.recognizedSpeakerID = recognizedSample.getSpeakerID();
		this.recognizedSampleID = recognizedSample.getSampleID();
	}

	public int getRecognizedSpeakerID() {
		return recognizedSpeakerID;
	}

	public int getRecognizedSampleID() {
		return recognizedSampleID;
	}

	public void setStage(Stage stage,List<ProcessingAudio> listOfProcessedSamples,ProcessingAudio recognizedSample) throws FileNotFoundException, ClassNotFoundException, IOException {
		primaryStage = stage;
		this.listOfProcessedSamples= listOfProcessedSamples;
		this.recognizedSample = recognizedSample;
		this.selectedSample=recognizedSample;
		List<String> list = new ArrayList<String>();
		list.add("Recognized Sample ");

		for(int i=0;i<listOfProcessedSamples.size();i++)
		{
			
			if(listOfProcessedSamples.get(i).getSpeakerID()!=this.recognizedSample.getSpeakerID() || listOfProcessedSamples.get(i).getSampleID()!=this.recognizedSample.getSampleID() )
			{
				list.add("Speaker "+listOfProcessedSamples.get(i).getSpeakerID()+" sample "+listOfProcessedSamples.get(i).getSampleID());

			}
		}

		ObservableList obList = FXCollections.observableList(list);
		cbShowAnotherSample.setItems(obList);

		cbShowAnotherSample.getSelectionModel().selectFirst();
		cbShowAnotherSample.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
		      @Override
		      public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
		        System.out.println(cbShowAnotherSample.getItems().get((Integer) number2));
		        System.out.println(cbShowAnotherSample.getSelectionModel().getSelectedIndex());

		        if(cbShowAnotherSample.getSelectionModel().getSelectedIndex()>0)
		        {
		        selectedSample=listOfProcessedSamples.get(cbShowAnotherSample.getSelectionModel().getSelectedIndex()-1);
		        }
		        else{
		        	selectedSample=recognizedSample;
		        }
		  
		        //RadioButton rbTemp=(RadioButton)toggleGroup.getSelectedToggle();
		        rbEnergy.fire();
		      }

		    });
		lResult.setText("Recognized Speaker "+selectedSample.getSpeakerID()+", the most appropriate sample is "+selectedSample.getSampleID());

	}

	@FXML
	public void initialize() {

		toggleGroup = new ToggleGroup();
		rbEnergy.setToggleGroup(toggleGroup);
		rbCentroid.setToggleGroup(toggleGroup);
		rbZCR.setToggleGroup(toggleGroup);
		rbRollOff.setToggleGroup(toggleGroup);
		rbMFCC1.setToggleGroup(toggleGroup);
		rbMFCC2.setToggleGroup(toggleGroup);
		rbMFCC3.setToggleGroup(toggleGroup);
		rbMFCC4.setToggleGroup(toggleGroup);
		rbMFCC5.setToggleGroup(toggleGroup);
		rbMFCC6.setToggleGroup(toggleGroup);
		rbMFCC7.setToggleGroup(toggleGroup);
		rbMFCC8.setToggleGroup(toggleGroup);
		rbMFCC9.setToggleGroup(toggleGroup);
		rbMFCC10.setToggleGroup(toggleGroup);
		rbMFCC11.setToggleGroup(toggleGroup);
		rbMFCC12.setToggleGroup(toggleGroup);
		rbMFCC13.setToggleGroup(toggleGroup);

		testChart.setLegendVisible(false);
		recognizedChart.setLegendVisible(false);

		
		testChart.setCreateSymbols(false); // hide dots
		recognizedChart.setCreateSymbols(false); // hide dots
		
		

	}

	public void ActionOnRadioButtonEnergy(ActionEvent event) throws InterruptedException, Exception {


		updateChart("Energy",sampleToRecognize.getEnergy(),selectedSample.getEnergy());


	}

	public void ActionOnRadioButtonCentroid(ActionEvent event) throws InterruptedException, Exception {

		
		updateChart("Centroid",sampleToRecognize.getCentroid(),selectedSample.getCentroid());

	}
	public void ActionOnRadioButtonZCR(ActionEvent event) throws InterruptedException, Exception {

		updateChart("Zero-Crossing Rate",sampleToRecognize.getZcr(),selectedSample.getZcr());
	}
	
	

	public void ActionOnRadioButtonRollOff(ActionEvent event) throws InterruptedException, Exception {

		updateChart("Roll Off ",sampleToRecognize.getRollOff(),selectedSample.getRollOff());
	}
	public void ActionOnRadioButtonMFCC1(ActionEvent event) throws InterruptedException, Exception {

		System.out.println(sampleToRecognize.getWspMFCC().length);
		updateChart("MFCC1 ",sampleToRecognize.getWspMFCC()[0],selectedSample.getWspMFCC()[0]);
	}
	
	public void ActionOnRadioButtonMFCC2(ActionEvent event) throws InterruptedException, Exception {

		updateChart("MFCC2 ",sampleToRecognize.getWspMFCC()[1],selectedSample.getWspMFCC()[1]);
	}
	
	public void ActionOnRadioButtonMFCC3(ActionEvent event) throws InterruptedException, Exception {

		updateChart("MFCC3 ",sampleToRecognize.getWspMFCC()[2],selectedSample.getWspMFCC()[2]);
	}
	public void ActionOnRadioButtonMFCC4(ActionEvent event) throws InterruptedException, Exception {

		updateChart("MFCC4 ",sampleToRecognize.getWspMFCC()[3],selectedSample.getWspMFCC()[3]);
	}
	public void ActionOnRadioButtonMFCC5(ActionEvent event) throws InterruptedException, Exception {

		updateChart("MFCC5 ",sampleToRecognize.getWspMFCC()[4],selectedSample.getWspMFCC()[4]);
	}
	public void ActionOnRadioButtonMFCC6(ActionEvent event) throws InterruptedException, Exception {

		updateChart("MFCC6 ",sampleToRecognize.getWspMFCC()[5],selectedSample.getWspMFCC()[5]);
	}
	public void ActionOnRadioButtonMFCC7(ActionEvent event) throws InterruptedException, Exception {

		updateChart("MFCC7 ",sampleToRecognize.getWspMFCC()[6],selectedSample.getWspMFCC()[6]);
	}
	public void ActionOnRadioButtonMFCC8(ActionEvent event) throws InterruptedException, Exception {

		updateChart("MFCC8 ",sampleToRecognize.getWspMFCC()[7],selectedSample.getWspMFCC()[7]);
	}
	public void ActionOnRadioButtonMFCC9(ActionEvent event) throws InterruptedException, Exception {

		updateChart("MFCC9 ",sampleToRecognize.getWspMFCC()[8],selectedSample.getWspMFCC()[8]);
	}
	public void ActionOnRadioButtonMFCC10(ActionEvent event) throws InterruptedException, Exception {

		updateChart("MFCC10 ",sampleToRecognize.getWspMFCC()[9],selectedSample.getWspMFCC()[9]);
	}
	public void ActionOnRadioButtonMFCC11(ActionEvent event) throws InterruptedException, Exception {

		updateChart("MFCC11 ",sampleToRecognize.getWspMFCC()[10],selectedSample.getWspMFCC()[10]);
	}
	public void ActionOnRadioButtonMFCC12(ActionEvent event) throws InterruptedException, Exception {

		updateChart("MFCC12 ",sampleToRecognize.getWspMFCC()[11],selectedSample.getWspMFCC()[11]);
	}
	public void ActionOnRadioButtonMFCC13(ActionEvent event) throws InterruptedException, Exception {

		updateChart("MFCC13 ",sampleToRecognize.getWspMFCC()[12],selectedSample.getWspMFCC()[12]);
	}

	public void updateChart(String label,double [] testFeature,double [] recognizedFeature)
	{
		XYChart.Series<Number, Number> seriesTestedSample= new XYChart.Series<Number, Number>();
		XYChart.Series<Number, Number> seriesRecognizedSample= new XYChart.Series<Number, Number>();
		
		testChart.getData().clear();
		recognizedChart.getData().clear();
		recognizedChart.setTitle("Speaker "+selectedSample.getSpeakerID()+" sample "+selectedSample.getSampleID());
		testChart.getXAxis().setLabel(label);
		recognizedChart.getXAxis().setLabel(label);
		
		for (int i = 0; i < testFeature.length; i++) {
			seriesTestedSample.getData().add(new XYChart.Data<Number, Number>(i, testFeature[i]));

		}
		testChart.getData().add(seriesTestedSample);
		for (int i = 0; i < recognizedFeature.length; i++) {
			seriesRecognizedSample.getData().add(new XYChart.Data<Number, Number>(i, recognizedFeature[i]));

		}
		recognizedChart.getData().add(seriesRecognizedSample);
	}
	
	public void ActionOnPlayTest(ActionEvent event) throws InterruptedException, Exception {
		try
	    {
	        Clip clip = AudioSystem.getClip();
	        clip.open(AudioSystem.getAudioInputStream(new File(sampleToRecognize.getFilePath())));
	        clip.start();
	        
	    }
	    catch (Exception exc)
	    {
	        exc.printStackTrace(System.out);
	    }
	}
	
	public void ActionOnPlaySample(ActionEvent event) throws InterruptedException, Exception {
		try
	    {
	        Clip clip = AudioSystem.getClip();
	        clip.open(AudioSystem.getAudioInputStream(new File(selectedSample.getFilePath())));
	        clip.start();
	        
	    }
	    catch (Exception exc)
	    {
	        exc.printStackTrace(System.out);
	    }
	}
	public void ActionOnBack(ActionEvent event) throws InterruptedException, Exception {
		primaryStage.setScene(parentScene);

	}


}
