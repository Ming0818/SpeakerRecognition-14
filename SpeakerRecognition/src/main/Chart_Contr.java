package main;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.commons.lang3.ArrayUtils;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.math.plot.Plot2DPanel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import calculations.DTW;
import calculations.FFT;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import recorder.Recorder;

public class Chart_Contr implements Serializable {
	// @FXML LineChart<Number,Number> lineChart;
	// @FXML final NumberAxis xAxis = new NumberAxis();
	@FXML
	Label lResult;
	@FXML
	ImageView imageView;
	@FXML
	RadioButton rb1;
	@FXML
	RadioButton rb2;
	@FXML
	Button bStart;

	XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
	Stage primaryStage;
	List<ProcessingAudio> listSpeaker1 ;
	List<ProcessingAudio> listSpeaker3 ;
	List<ProcessingAudio> listSpeaker2 ;
	String speaker="lll";

	private static final long serialVersionUID = 1L;

	public void setStage(Stage stage) throws FileNotFoundException, ClassNotFoundException, IOException {

		primaryStage = stage;
computeRecords();
		listSpeaker1 = wczytaj("src\\dane\\akumulatory\\rafal\\ak_rafal.ser");

		listSpeaker3 = wczytaj("src\\dane\\akumulatory\\mariusz\\ak_mariusz.ser");
		listSpeaker2 = wczytaj("src\\dane\\akumulatory\\pawel\\ak_pawel.ser");
		joinLists();

	}

	public void computeRecords() throws FileNotFoundException, ClassNotFoundException, IOException {

		List<ProcessingAudio> listSpeaker1 = new Vector<ProcessingAudio>();
		List<ProcessingAudio> listSpeaker2 = new Vector<ProcessingAudio>();
		List<ProcessingAudio> listSpeaker3 = new Vector<ProcessingAudio>();
		ProcessingAudio[] recordsOfSpeaker1 = new ProcessingAudio[15];
		ProcessingAudio[] recordsOfSpeaker2 = new ProcessingAudio[15];
		ProcessingAudio[] recordsOfSpeaker3 = new ProcessingAudio[15];

		int k;
		k = 0;
		listSpeaker1.clear();
		listSpeaker2.clear();
		listSpeaker3.clear();

		for (int i = 0; i < recordsOfSpeaker1.length; i++) {
			k = i;
			recordsOfSpeaker1[i] = new ProcessingAudio("src\\records\\speaker1_" + k + ".wav");
			listSpeaker1.add(recordsOfSpeaker1[i] );
			recordsOfSpeaker2[i] = new ProcessingAudio("src\\records\\speaker2_" + k + ".wav");
			listSpeaker2.add(recordsOfSpeaker2[i] );

			recordsOfSpeaker3[i] = new ProcessingAudio("src\\records\\speaker3_" + k + ".wav");
			listSpeaker3.add(recordsOfSpeaker3[i] );
		}
		

	   
		save("src\\dane\\speaker1.ser", listSpeaker3);
		save("src\\dane\\speaker2.ser", listSpeaker3);
		save("src\\dane\\speaker3.ser", listSpeaker3);
 		

	}
public void saveAsXml(){//not working correctly
	 try 
     { 
     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
     DocumentBuilder builder = factory.newDocumentBuilder();
     Document doc = builder.newDocument();
     doc.setXmlStandalone(true);
     Element rootElement = doc.createElement("arrays");
     doc.appendChild(rootElement);
      for (ProcessingAudio pAudio : listSpeaker1) {
     System.out.println("tworzymy");
          Element valueOf = doc.createElement("value");

         double [][] allArrays=pAudio.getAllArraysForXml();
         int countArray=0;
         Element arrayXml[]=new Element [allArrays.length];
         for(double [] array:allArrays)
         {
	           arrayXml[countArray] = doc.createElement("array"+countArray);
	           for(double value:array)
	           {
	        	   valueOf.appendChild(doc.createTextNode(String.valueOf(value)));
		           arrayXml[countArray].appendChild(valueOf);
	           }

	           rootElement.appendChild(arrayXml[countArray]);
	           
	           countArray++;

	           
         }

         
        
         
     }
     
     TransformerFactory transformerFactory = TransformerFactory.newInstance();
     Transformer transformer = transformerFactory.newTransformer();
     DOMSource dom = new DOMSource(doc);
     StreamResult result = new StreamResult(new File("employees.xml"));
     transformer.transform(dom, result);
     System.out.println("File employee was created.");
     }
     catch(Exception e ){ System.out.println(e.getMessage()); }
}

	public void btn(ActionEvent event) throws InterruptedException, Exception {

		if (rb1.isSelected()) {

			Recorder record = new Recorder("test.wav");

			System.out.println("3");
			bStart.setText("3");
			lResult.setText("3");

			Thread.sleep(1000);
			System.out.println("2");

			bStart.setText("2");
			lResult.setText("2");

			Thread.sleep(1000);
			System.out.println("1");

			bStart.setText("1");
			lResult.setText("1");

			Thread.sleep(1000);
			bStart.setText("Recording");

			record.main(1, "test.wav");

		}

		/*	
		
		*/

		bStart.setText("Computing");

		int k;
		k = 0;
	    
		Task task = new Task<Void>() {
			  @Override
			  public Void call() throws Exception {
			    int i = 0;
			    
			      final int finalI = i;
			      Platform.runLater(new Runnable() {
			        @Override
			        public void run() {
			        	compute();
			        	lResult.setText(speaker);

 			        }
			      });
			      i++;

 			      return null;
			  }
			};
			Thread th = new Thread(task);
			th.setDaemon(true);
			th.start();
 		

	}

	public void joinLists() {
		for (int i = 0; i < listSpeaker3.size(); i++) {
			listSpeaker1.add(listSpeaker3.get(i));
		}
		for (int i = 0; i < listSpeaker2.size(); i++) {
			listSpeaker1.add(listSpeaker2.get(i));
		}
	}

	public void compute(){
		ProcessingAudio test = new ProcessingAudio("test.wav");

		DTW dtw = new DTW();
		double[][] cost = new double[listSpeaker1.size()][19];
		double[] min = new double[19];
		for (int i = 0; i < cost.length; i++) {
			cost[i][0] = dtw.calcDTW(test.getEnergy(), listSpeaker1.get(i).getEnergy());
			cost[i][1] = dtw.calcDTW(test.getZcr(), listSpeaker1.get(i).getZcr());
			cost[i][2] = dtw.calcDTW(test.getCentroid(), listSpeaker1.get(i).getCentroid());
			cost[i][3] = dtw.calcDTW(test.getRollOff(), listSpeaker1.get(i).getRollOff());
			cost[i][4] = dtw.calcDTW(test.getAverageCentroid(), listSpeaker1.get(i).getAverageCentroid());
			cost[i][5] = dtw.calcDTW(test.getMedianCentroid(), listSpeaker1.get(i).getMedianCentroid());
			for (int j = 0; j < test.getWspMFCC().length; j++) {
				cost[i][6 + j] = dtw.calcDTW(test.getWspMFCC()[j], listSpeaker1.get(i).getWspMFCC()[j]);
			}

		}

		double[] temp_min = new double[19];
		for (int i = 0; i < temp_min.length; i++) {
			temp_min[i] = Double.POSITIVE_INFINITY;
		}
		for (int i = 0; i < cost.length; i++) {
			for (int j = 0; j < temp_min.length; j++) {
				if (cost[i][j] < temp_min[j]) {
					temp_min[j] = cost[i][j];
					min[j] = i;

				}
			}

		}

		int r_pkt = 0;
		int m_pkt = 0;
		int p_pkt = 0;
		double pkt = 1;
		for (int i = 0; i < min.length; i++) {

			pkt = 1;
			if (min[i] < 15)
				r_pkt += pkt;
			if ((min[i] > 14) && (min[i] < 30))
				m_pkt += pkt;
			if (min[i] > 29)
				p_pkt += pkt;
		}
		if ((r_pkt > m_pkt) && (r_pkt > p_pkt)) {
			speaker="Speaker1";
  		} else if ((m_pkt > r_pkt) && (m_pkt > p_pkt))
		{

 			speaker="Speaker2";
		} else if ((p_pkt > m_pkt) && (p_pkt > r_pkt)) {
 			speaker="Speaker3";
 
		} else {
			System.out.println("not recognized " + r_pkt + " " + m_pkt + " " + p_pkt);
 			speaker="not recognized " + r_pkt + " " + m_pkt + " " + p_pkt;

 
		}
		System.out.println("Speaker1 " + r_pkt + " Speaker2 " + p_pkt + " Speaker3 " + p_pkt);
	}
	public List<ProcessingAudio> wczytaj(String path)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		List<ProcessingAudio> lista = new Vector<ProcessingAudio>();
		ObjectInputStream pl2 = null;
		try {
			pl2 = new ObjectInputStream(new FileInputStream(path));

			lista = (Vector<ProcessingAudio>) pl2.readObject();

		} catch (EOFException ex) {
			System.out.println("EndOfFile");
		}

		finally {
			if (pl2 != null)
				pl2.close();
		}
		return lista;
	}

	public void save(String path, List<ProcessingAudio> lista)
			throws FileNotFoundException, IOException, ClassNotFoundException

	{
		try {

			ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(path));
			o.writeObject(lista); // zapisanie obiektu
			o.close(); // zamkniêcie strumienia

			

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	 
}
