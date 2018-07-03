package main;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Arrays;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

import calculations.DTW;
import calculations.FFT;

public class ProcessingAudio implements Serializable {

	private int numChannels;
	private long NumFrames;
	private int framesInt;
	private double[] buffer;
	private double[] buffer1;
	private int framesRead;
	private int SampleRate;
	private double energy[];
	private double Samplefor50ms;
	private double[][] bufferFrames;
	private double[][] bufferCalcFFT;
	private double[] rollOff;
	private double[] centroid;
	private double[] averageCentroid;
	private double[] medianCentroid;
	 
	private double[][] magnitude;
	private double[] zcr;
	 
	private double[][] wspMFCC;

 
	private double[][] MFCC;
	private MFCC mfcc;

 	protected final static int numMelFilters = 23;
	protected final static double lowerFilterFreq = 133.3334;

	private static final long serialVersionUID = 1L;

	public ProcessingAudio(String a) {
		try {
			// Open the wav file specified as the first argument
			WavFile wavFile = WavFile.openWavFile(new File(a));

			// Display information about the wav file
			wavFile.display();

			// Get the number of audio channels in the wav file
			numChannels = wavFile.getNumChannels();
			NumFrames = wavFile.getNumFrames();

			// Create a buffer of 100 frames
			buffer = new double[(int) NumFrames * numChannels];

			int x = 0;

			int timeFrame = 20; // 50ms
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			double average = 0.0;
			SampleRate = (int) wavFile.getSampleRate();
			Samplefor50ms = SampleRate / timeFrame;
			
			// Read frames into buffer
			framesRead = wavFile.readFrames(buffer, (int) NumFrames * numChannels);
			
			long newRate = preProcessing(buffer, (int) NumFrames * numChannels, SampleRate);
			
 			if(newRate==0)
				newRate = buffer.length;
			
			framesRead = (int) newRate;
			NumFrames = newRate;
 			buffer1 = new double[(int) newRate];
			for (int i = 0; i < newRate; i++) {
				buffer1[i] = buffer[i];
			}

			// Loop through frames and look for minimum and maximum value
			for (int s = 0; s < (int) newRate; s++) {
				x++;

				average += buffer1[s];
			}
			average /= x;

			x = 0;
 
 			// remove DC
			double norm;

			for (int s = 0; s < (int) newRate; s++) {

				buffer1[s] -= average;
				if (buffer1[s] > max)
					max = buffer1[s];
				if (buffer1[s] < min)
					min = buffer1[s];

			}
			// normalization
			if (Math.abs(min) > max)
				norm = Math.abs(min);
			else
				norm = max;
			for (int s = 0; s < (int) newRate; s++) {
				buffer1[s] /= norm;
			}
 
			// counting number of frames
			double frames = framesRead * numChannels / (Samplefor50ms);
			 framesInt = (int) frames;
		
			framesInt = framesInt * 2;
			bufferFrames = new double[framesInt][(int) (Samplefor50ms) + 1];

		 
			// pre-emphasis

			for (int s = framesRead * numChannels - 1; s > 0; s--) {

				buffer1[s] = buffer1[s] - 0.97 * buffer1[s - 1];
			}
 			
			
			
			divisionIntoFrames();
			
		 

			int newFramesInt = framesInt;
			
			energy = new double[newFramesInt];
			rollOff = new double[newFramesInt];
			centroid = new double[newFramesInt];
			double[] copyCentroid = new double[newFramesInt];

			averageCentroid = new double[1];
			medianCentroid = new double[1];
			 

			zcr = new double[newFramesInt];
 			double[] copyAvrMFCC = new double[newFramesInt];
 
			 
			MFCC = new double[newFramesInt][];

 			bufferCalcFFT = new double[newFramesInt][(int) (Samplefor50ms) + 1];
			magnitude = new double[newFramesInt][];
			//  FFT
			FFT fft = new FFT();
			try {
				for (int i = 0; i < newFramesInt; i++) {

					bufferCalcFFT[i] = fft.calculateFramesFFT(bufferFrames[i]);
					magnitude[i] = fft.getMagnitudeSpectrum();
				}

			} catch (Exception e) {
				System.err.println(e + " pusty wskaznik");
				e.printStackTrace();
			}
 
			for (int i = 0; i < newFramesInt; i++) {
				rollOff[i] = calcRollOff(bufferCalcFFT[i]);
			}

 
			energy = calculateEnergy(bufferFrames, newFramesInt, Samplefor50ms);

			// centroid
			for (int i = 0; i < newFramesInt; i++) {
				centroid[i] = 0.0;
				centroid[i] = calculateCentroid(bufferCalcFFT[i]);
				averageCentroid[0] += centroid[i];

 
			}
			averageCentroid[0] /= newFramesInt;
			System.out.println("Srednia Centroid " + averageCentroid[0]);
			for (int i = 0; i < centroid.length; i++) {
				copyCentroid[i] = centroid[i];
			}
			Arrays.sort(copyCentroid);

 			if (copyCentroid.length % 2 == 0) {
				medianCentroid[0] = (copyCentroid[copyCentroid.length / 2] + copyCentroid[copyCentroid.length / 2 - 1])
						/ 2;
			} else {
				medianCentroid[0] = copyCentroid[copyCentroid.length / 2];
			}
 			 	// audio();
			zcr = calculateZCR(bufferFrames, newFramesInt, Samplefor50ms);
			
			// MFCC
			 mfcc = new MFCC();
			for (int i = 0; i < newFramesInt; i++) {
				MFCC[i] = mfcc.getMFCC(Samplefor50ms, bufferCalcFFT[i]);

			}
 				wspMFCC = new double[MFCC[0].length][framesInt];
			 
 				mfccToWspMfcc();
	 
			
			// Close the wavFile
			wavFile.close();
		 

			// Output the minimum and maximum value
			// System.out.printf("Min: %f, Max: %f\n", min, max);

		} catch (Exception e) {
			System.err.println(e);
		}

	}

 

	 
	 

	 
	 

	public double[] getAverageCentroid() {
		return averageCentroid;
	}

	public void setAverageCentroid(double[] averageCentroid) {
		this.averageCentroid = averageCentroid;
	}

	 
	public double[][] getWspMFCC() {
		return wspMFCC;
	}

	public void setWspMFCC(double[][] wspMFCC) {
		this.wspMFCC = wspMFCC;
	}
	public double[] getMedianCentroid() {
		return medianCentroid;
	}

	public void setMedianCentroid(double[] medianCentroid) {
		this.medianCentroid = medianCentroid;
	}

	 
	
	public int getNumChannels() {
		return numChannels;
	}

	public void setNumChannels(int numChannels) {
		this.numChannels = numChannels;
	}

	public long getNumFrames() {
		return NumFrames;
	}

	public void setNumFrames(long numFrames) {
		NumFrames = numFrames;
	}

	public double[] getBuffer() {
		return buffer;
	}

	public void setBuffer(double[] buffer) {
		this.buffer = buffer;
	}

	public int getFramesRead() {
		return framesRead;
	}

	public void setFramesRead(int framesRead) {
		this.framesRead = framesRead;
	}

	public double[][]getAllArraysForXml()
	{
		double tab [][]=new double [19][];
		tab[0]=energy;
		tab[1]=zcr;
		tab[2]=centroid;
		tab[3]=rollOff;
		tab[4]=averageCentroid;
		tab[5]=medianCentroid;
		for(int i=0;i<MFCC.length;i++)
		{
			tab[i+6]=MFCC[i];
		}
		return tab;
		
	}
	public int getSampleRate() {
		return SampleRate;
	}

	public void setSampleRate(int sampleRate) {
		SampleRate = sampleRate;
	}

	public double[] getEnergy() {
		return energy;
	}

	public void setEnergy(double[] energy) {
		this.energy = energy;
	}

	public double getSamplefor50ms() {
		return Samplefor50ms;
	}

	public void setSamplefor50ms(double samplefor50ms) {
		Samplefor50ms = samplefor50ms;
	}

	public double[][] getBufferFrames() {
		return bufferFrames;
	}

	public void setBufferFrames(double[][] bufferFrames) {
		this.bufferFrames = bufferFrames;
	}

	public double[][] getBufferCalcFFT() {
		return bufferCalcFFT;
	}

	public void setBufferCalcFFT(double[][] bufferCalcFFT) {
		this.bufferCalcFFT = bufferCalcFFT;
	}

	public double[] getRollOff() {
		return rollOff;
	}

	public void setRollOff(double[] rollOff) {
		this.rollOff = rollOff;
	}

	public double[] getCentroid() {
		return centroid;
	}

	public void setCentroid(double[] centroid) {
		this.centroid = centroid;
	}

	public double[] getZcr() {
		return zcr;
	}

	public void setZcr(double[] zcr) {
		this.zcr = zcr;
	}

	public double[] calculateZCR(double[][] frame, int frames, double Samplefor50ms) {
		double[] zcr = new double[frames];

		for (int i = 0; i < frames; i++) {
			zcr[i] = 0.0;
			for (int j = 1; j < Samplefor50ms; j++) {
				if ((frame[i][j] * frame[i][j - 1]) < 0)
					zcr[i]++;
			}

		}
		return zcr;

	}

	public double[] calculateEnergy(double[][] frame, int frames, double Samplefor50ms) {
		double[] energy = new double[frames];
		// obliczanie energii
		for (int i = 0; i < frames; i++) {
			energy[i] = 0.0;
			for (int j = 0; j < Samplefor50ms; j++) {
				energy[i] += frame[i][j] * frame[i][j];

			}
			// series.getData().add(new
			// XYChart.Data<Number,Number>(i,energy[i]));

			// System.out.println("energia "+i+" ramki: "+ energy[i]);

		}
		return energy;
	}

	public double calculateCentroid(double[] frame) {

		double total = 0.0;
		double weighted_total = 0.0;
		for (int bin = 0; bin < frame.length; bin++) {
			weighted_total += bin * frame[bin];
			total += frame[bin];
		}

		double result = 0.0;
		if (total != 0.0) {
			result = weighted_total / total;
		} else {
			result = 0.0;
		}
		// System.out.println("result "+result);

		return result;
	}

	 

	public double calcRollOff(double[] frame) throws Exception {
		double[] pow_spectrum = frame;
		double cutoff = 0.85;
		double total = 0.0;
		for (int bin = 0; bin < pow_spectrum.length; bin++)
			total += pow_spectrum[bin];
		double threshold = total * cutoff;

		total = 0.0;
		int point = 0;
		for (int bin = 0; bin < pow_spectrum.length; bin++) {
			total += pow_spectrum[bin];
			if (total >= threshold) {
				point = bin;
				bin = pow_spectrum.length;
			}
		}

		double result;
		result = ((double) point) / ((double) pow_spectrum.length);
		return result;
	}

 
public void  mfccToWspMfcc()
{
	for (int i = 0; i < wspMFCC.length; i++) {
		for (int b = 0; b < framesInt; b++) {
			wspMFCC[i][b] = MFCC[b][i];

		}
	}
}
	public long preProcessing(double buffer[], long frames, long sampleRate) throws FileNotFoundException  
	{
		double cisza = 0.033;
		int i;
		int left = 1;
		int right = 0;
		int ms10 = (int) sampleRate / 100; // 
		double over002;
		double temp;
		double max = 0;
		double sum = 0;
		double average;
		long newFrames = -1;
		PrintWriter zapis = new PrintWriter("ex.txt");

		for (i = 0; i < frames - ms10; i += ms10) // checking if silence is not greater than 40% of frame
		{
			over002 = 0;
			for (int j = 0; j < ms10; j++) {
				if (buffer[i + j] > cisza)
					over002 += 1; 
			}
			if (over002 / ms10 > 0.4) // 
			{
				left = i; // 
				break;
			}
		}

		for (i = (int) (frames - 1); i >= ms10; i -= ms10) {
			over002 = 0;
			for (int j = ms10; j >= 0; j--) {
				if (buffer[i - j] > cisza)
					over002 += 1;
			}

			if (over002 / ms10 > 0.3) 
			{
				right = i;
				break;
			}
			// else right = 0;
		}

		if (left > right) // if whole record is silence
		{
 			return 0;
		}

		newFrames = right - left + 1; // computing new size of frame

		for (i = 0; i < newFrames; i++) 
		{
			temp = buffer[left + i];
			sum = sum + temp; 
			buffer[i] = temp;
		}

		average = sum / newFrames;

		for (i = 0; i < newFrames; i++) {
			buffer[i] = buffer[i] - average; 
			if (Math.abs(buffer[i]) > max)
				max = Math.abs(buffer[i]);
		}

		for (i = 0; i < newFrames; i++) {
			buffer[i] = buffer[i] / max; 
			zapis.println(buffer[i]);
		}

		zapis.close();
		return newFrames; // funkcja zwraca now¹ iloœæ próbek nagrania
	}
	
	public void divisionIntoFrames()
	{
		try {
			for (int i = 0; i < framesInt - 1; i++) {
				for (int j = 0; j < Samplefor50ms; j++) {
					if (i * (Samplefor50ms / 2) + j > buffer1.length) {
						break;

					}
					bufferFrames[i][j] = buffer1[(int) (i * (Samplefor50ms / 2) + j)];
 				}

			}
			} catch (Exception e) {
			System.err.println(e + "out of range");
			e.printStackTrace();
		}
	}

}
