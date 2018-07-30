package wavProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Arrays;

import calculations.FFT;
import calculations.MFCC;

public class ProcessingAudio implements Serializable {

	private int numChannels;
	private String filePath;
	private int numberOfFrames;
	private int numberOfValues;
	private int SpeakerID;
	private int SampleID;
	// private int framesInt;
	private double[] buffer;
	private int framesRead;
	private int SampleRate;
	private double Samplefor50ms;
	private double[][] bufferFrames;
	private double[][] bufferCalcFFT;
	private double[] rollOff;
	private double[] centroid;
	private double averageCentroid;
	private double[] medianCentroid;

	private double[] zcr;

	private double[][] wspMFCC;

	private double[][] MFCC;
	private MFCC mfcc;

	protected final static int numMelFilters = 23;
	protected final static double lowerFilterFreq = 133.3334;

	private static final long serialVersionUID = 1L;
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	private double energy[];
	public int getSpeakerID() {
		return SpeakerID;
	}

	public void setSpeakerID(int speakerID) {
		SpeakerID = speakerID;
	}

	public int getSampleID() {
		return SampleID;
	}

	public void setSampleID(int sample) {
		SampleID = sample;
	}



	public ProcessingAudio(String a) {
		try {
			long start = System.currentTimeMillis();
			this.filePath=a;
			

			// Open the wav file specified as the first argument
			WavFile wavFile = WavFile.openWavFile(new File(a));

			// Display information about the wav file
			wavFile.display();

			// Get the number of audio channels in the wav file
			numChannels = wavFile.getNumChannels();
			numberOfValues = (int) wavFile.getNumFrames();

			buffer = new double[(int) numberOfValues * numChannels];

			int timeFrame = 20; // 1000/20=50ms, one frame

			SampleRate = (int) wavFile.getSampleRate();

			Samplefor50ms = SampleRate / timeFrame;

			// Read frames into buffer
			framesRead = wavFile.readFrames(buffer, (int) numberOfValues * numChannels);

			int newRate = preProcessing((int) numberOfValues * numChannels);

			// counting number of frames
			numberOfFrames = (int) (newRate * numChannels / (Samplefor50ms));

			numberOfFrames *= 2;// number of frames needs to be doubled in order
								// to achieve overlapping

			bufferFrames = new double[numberOfFrames][(int) (Samplefor50ms) + 1];

			// pre-emphasis

			for (int s = newRate * numChannels - 1; s > 0; s--) {

				buffer[s] = buffer[s] - 0.97 * buffer[s - 1];
			}

			divideIntoFrames();

			energy = new double[numberOfFrames];
			rollOff = new double[numberOfFrames];
			centroid = new double[numberOfFrames];

			medianCentroid = new double[1];

			zcr = new double[numberOfFrames];

			MFCC = new double[numberOfFrames][];

			bufferCalcFFT = new double[numberOfFrames][(int) (Samplefor50ms) + 1];

			// FFT
			FFT fft = new FFT();
			try {
				for (int i = 0; i < numberOfFrames; i++) {

					bufferCalcFFT[i] = fft.calculateFramesFFT(bufferFrames[i]);
				}

			} catch (Exception e) {
				System.err.println(e + " pusty ");
				e.printStackTrace();
			}
			
			for (int i = 0; i < numberOfFrames; i++) {
				rollOff[i] = calcRollOff(bufferCalcFFT[i]);
			}

			
			energy = calculateEnergy(bufferFrames, numberOfFrames, Samplefor50ms);
			
			// centroid
			calculateCentroidFeatures();

			
			zcr = calculateZCR(bufferFrames, numberOfFrames, Samplefor50ms);

			
			// MFCC
			mfcc = new MFCC();
			for (int i = 0; i < numberOfFrames; i++) {
				MFCC[i] = mfcc.getMFCC(Samplefor50ms, bufferCalcFFT[i]);

			}
			wspMFCC = new double[MFCC[0].length][numberOfFrames];

			mfccToWspMfcc();
			// Close the wavFile
			wavFile.close();

		} catch (Exception e) {
			System.err.println(e);
		}

	}

	public void calculateCentroidFeatures() {
		for (int i = 0; i < numberOfFrames; i++) {
			centroid[i] = 0.0;
			centroid[i] = calculateCentroid(bufferCalcFFT[i]);
			averageCentroid += centroid[i];
		}
		averageCentroid /= numberOfFrames;

		double[] copyCentroid = new double[numberOfFrames];

		for (int i = 0; i < centroid.length; i++) {
			copyCentroid[i] = centroid[i];
		}
		Arrays.sort(copyCentroid);

		if (copyCentroid.length % 2 == 0) {
			medianCentroid[0] = (copyCentroid[copyCentroid.length / 2] + copyCentroid[copyCentroid.length / 2 - 1]) / 2;
		} else {
			medianCentroid[0] = copyCentroid[copyCentroid.length / 2];
		}
	}

	public void divideIntoFrames() {
		System.out.println("Sample for 50ms " + Samplefor50ms);
		System.out.println("numberOfFrames " + numberOfFrames);

		try {
			for (int i = 0; i < numberOfFrames - 1; i++) {
				for (int j = 0; j < Samplefor50ms; j++) {
					if (i * (Samplefor50ms / 2) + j > buffer.length) {
						break;

					}
					bufferFrames[i][j] = buffer[(int) (i * (Samplefor50ms / 2) + j)];
					// each frame after first one, starts in the middle of
					// previos frame to avoid losing any information

				}

			}
		} catch (Exception e) {
			System.err.println(e + "out of range");
			e.printStackTrace();
		}
	}

	public double[] getAverageCentroid() {
		double avrgCentroid[] = new double[1];
		avrgCentroid[0] = averageCentroid;
		return avrgCentroid;
	}

	public void setAverageCentroid(double averageCentroid) {
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

	public void mfccToWspMfcc() {
		for (int i = 0; i < wspMFCC.length; i++) {
			for (int b = 0; b < numberOfFrames; b++) {
				wspMFCC[i][b] = MFCC[b][i];
			}
		}
	}

	public int preProcessing(long frames) throws FileNotFoundException {
		// this function removes silence from the start and from the bottom of
		// frame

		double silence = 0.023;// elements of sample below this value are
								// considered as silence

		int newStart = 1;
		int newEnd = 0;
		int ms10 = (int) SampleRate / 100; //
		double numberOfValuesGreaterThanValueOfSilenceInSubFrame;

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double sum = 0;
		double average;
		int newFrames = -1;

		for (int i = 0; i < frames - ms10; i += ms10) {
			numberOfValuesGreaterThanValueOfSilenceInSubFrame = 0;
			for (int j = 0; j < ms10; j++) {
				if (Math.abs(buffer[i + j]) > silence)
					numberOfValuesGreaterThanValueOfSilenceInSubFrame += 1;
			}
			if (numberOfValuesGreaterThanValueOfSilenceInSubFrame / ms10 > 0.4) {
				// if less than 60% of the value is silent, the current frame is
				// treated as the new first frame of the entire sample
				newStart = i;
				break;
			}
		}
		// and the same from the bottom
		for (int i = (int) (frames - 1); i >= ms10; i -= ms10) {
			numberOfValuesGreaterThanValueOfSilenceInSubFrame = 0;
			for (int j = ms10; j >= 0; j--) {
				if (Math.abs(buffer[i - j]) > silence)
					numberOfValuesGreaterThanValueOfSilenceInSubFrame += 1;
			}

			if (numberOfValuesGreaterThanValueOfSilenceInSubFrame / ms10 > 0.4) {
				newEnd = i;
				break;
			}
		}

		if (newStart > newEnd) // if whole record is silence
		{
			System.out.println("preprocessing rate"+frames);
			return (int) frames;
		}

		double tempBuffer[] = new double[buffer.length];

		newFrames = newEnd - newStart + 1; // new size of frame
		for (int i = 0; i < buffer.length; i++) {
			tempBuffer[i] = buffer[i];// copy of old buffer
		}

		buffer = new double[(int) newFrames];// buffer with new length

		//
		for (int i = 0; i < newFrames; i++) {

			buffer[i] = tempBuffer[newStart + i];// new buffer is filled with
													// non-silent values
			sum = sum + buffer[i];
		}

		average = sum / newFrames;

		for (int i = 0; i < newFrames; i++) {
			buffer[i] = buffer[i] - average;
			if (Math.abs(buffer[i]) > max)
				max = Math.abs(buffer[i]);
		}

		for (int i = 0; i < newFrames; i++) {
			buffer[i] = buffer[i] / max;
		}

		// remove DC
		double norm;

		for (int s = 0; s < (int) newFrames; s++) {

			buffer[s] -= average;
			if (buffer[s] > max)
				max = buffer[s];
			if (buffer[s] < min)
				min = buffer[s];

		}
		// normalization
		if (Math.abs(min) > max)
			norm = Math.abs(min);
		else
			norm = max;
		for (int s = 0; s < (int) newFrames; s++) {
			buffer[s] /= norm;
		}
		System.out.println("preprocessing oldrate"+frames);

		System.out.println("preprocessing newrate"+newFrames);

		return newFrames;
	}

}
