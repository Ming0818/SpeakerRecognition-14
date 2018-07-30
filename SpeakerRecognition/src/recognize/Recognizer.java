package recognize;

import java.util.List;

import calculations.DTW;
import wavProcessing.ProcessingAudio;

public class Recognizer {

	List<ProcessingAudio> listOfProcessedSamples;
	int numberOfSpeakers;
	ProcessingAudio sampleToRecognize;

	ComparisonResults comparisonResults;
	int recognizedSpeakerID;
	int recognizedSampleID;
	public int getRecognizedSpeakerID() {
		return comparisonResults.getRecognizedSpeakerID();
	}



	public int getRecognizedSampleID() {
		return comparisonResults.getRecognizedSampleID();
	}

	

	public Recognizer(List<ProcessingAudio> listOfProcessedSamples,ProcessingAudio sampleToRecognize,int numberOfSpeakers)
	{
		this.listOfProcessedSamples=listOfProcessedSamples;
		this.sampleToRecognize=sampleToRecognize;
		this.numberOfSpeakers=numberOfSpeakers;
	}
	
	public ProcessingAudio getRecognizedSample()
	{
		for(ProcessingAudio pAudio:listOfProcessedSamples)
		{

			if(pAudio.getSpeakerID()==recognizedSpeakerID && pAudio.getSampleID()==recognizedSampleID)
			{
				return pAudio;
			}
		}
		return null;
		
	}
	public void Recognize()
	{
		DTW dtw = new DTW();
		comparisonResults = new ComparisonResults(listOfProcessedSamples.size(),19,numberOfSpeakers);//19 features to compare
		
		for(int i=0;i<comparisonResults.getColumns();i++)//number of samples
		{
			
			comparisonResults.setSpeakerID(i, listOfProcessedSamples.get(i).getSpeakerID());
			comparisonResults.setSampleID(i,  listOfProcessedSamples.get(i).getSampleID());
	

			comparisonResults.setValue(i, 0, dtw.calcDTW(sampleToRecognize.getEnergy(), listOfProcessedSamples.get(i).getEnergy()));
			comparisonResults.setValue(i, 1,dtw.calcDTW(sampleToRecognize.getZcr(), listOfProcessedSamples.get(i).getZcr()));
			comparisonResults.setValue(i, 2,dtw.calcDTW(sampleToRecognize.getCentroid(), listOfProcessedSamples.get(i).getCentroid()));
			comparisonResults.setValue(i, 4,dtw.calcDTW(sampleToRecognize.getAverageCentroid(), listOfProcessedSamples.get(i).getAverageCentroid()));
			comparisonResults.setValue(i, 5,dtw.calcDTW(sampleToRecognize.getMedianCentroid(), listOfProcessedSamples.get(i).getMedianCentroid()));
			for (int j = 0; j < sampleToRecognize.getWspMFCC().length; j++) {
				comparisonResults.setValue(i, 6+j,dtw.calcDTW(sampleToRecognize.getWspMFCC()[j], listOfProcessedSamples.get(i).getWspMFCC()[j]));
			}
		}
		comparisonResults.compareResults();
		recognizedSpeakerID=comparisonResults.getRecognizedSpeakerID();
		recognizedSampleID=comparisonResults.getRecognizedSampleID();
		
	}
}
