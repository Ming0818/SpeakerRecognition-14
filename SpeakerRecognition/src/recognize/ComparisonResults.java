package recognize;

// comparison results for each sample
class SingleSampleResults {
	double value[];
	int speakerID;
	int sampleID;

	public double getValue(int index) {
		return value[index];
	}

	public void setValue(int index, double value) {
		this.value[index] = value;
	}

	public SingleSampleResults(int length) {
		value = new double[length];
	}

	public int getSpeakerID() {
		return speakerID;
	}

	public void setSpeakerID(int speakerID) {
		this.speakerID = speakerID;
	}

	public int getSampleID() {
		return sampleID;
	}

	public void setSampleID(int sampleID) {
		this.sampleID = sampleID;
	}

}

public class ComparisonResults {
	SingleSampleResults singleSampleResults[];
	int numbersOfAssignedFeaturesToEachSpeaker[];
	int mostSimilarSample[][];
	int recognizedSpeakerID;
	int recognizedSampleID;

	int numberOfSamples, numberOfComparisonValues, speakers;

	public SingleSampleResults[] getSingleSampleResults() {
		return singleSampleResults;
	}

	public void setSingleSampleResults(SingleSampleResults[] singleSampleResults) {
		this.singleSampleResults = singleSampleResults;
	}

	public int[] getNumbersOfAssignedFeaturesToEachSpeaker() {
		return numbersOfAssignedFeaturesToEachSpeaker;
	}

	public void setNumbersOfAssignedFeaturesToEachSpeaker(int[] numbersOfAssignedFeaturesToEachSpeaker) {
		this.numbersOfAssignedFeaturesToEachSpeaker = numbersOfAssignedFeaturesToEachSpeaker;
	}

	public int[][] getMostSimilarSample() {
		return mostSimilarSample;
	}

	public void setMostSimilarSample(int[][] mostSimilarSample) {
		this.mostSimilarSample = mostSimilarSample;
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	public int getNumberOfComparisonValues() {
		return numberOfComparisonValues;
	}

	public void setNumberOfComparisonValues(int numberOfComparisonValues) {
		this.numberOfComparisonValues = numberOfComparisonValues;
	}

	public int getSpeakers() {
		return speakers;
	}

	public void setSpeakers(int speakers) {
		this.speakers = speakers;
	}

	public void setRecognizedSpeakerID(int recognizedSpeakerID) {
		this.recognizedSpeakerID = recognizedSpeakerID;
	}

	public void setRecognizedSampleID(int recognizedSampleID) {
		this.recognizedSampleID = recognizedSampleID;
	}

	public ComparisonResults(int numberOfSamples, int numberOfComparisonValues, int numberOfSpeakers) {
		this.numberOfSamples = numberOfSamples;
		this.numberOfComparisonValues = numberOfComparisonValues;
		this.numbersOfAssignedFeaturesToEachSpeaker = new int[numberOfSpeakers];
		this.mostSimilarSample = new int[numberOfSpeakers][numberOfSamples / numberOfSpeakers];
		this.recognizedSpeakerID = 0;
		this.recognizedSampleID = 0;

		singleSampleResults = new SingleSampleResults[numberOfSamples];

		for (int i = 0; i < numberOfSamples; i++) {
			singleSampleResults[i] = new SingleSampleResults(numberOfComparisonValues);
		}

		for (int i = 0; i < numbersOfAssignedFeaturesToEachSpeaker.length; i++) {
			numbersOfAssignedFeaturesToEachSpeaker[i] = 0;
		}
		for (int i = 0; i < mostSimilarSample.length; i++) {
			for (int j = 0; j < mostSimilarSample[0].length; j++)
				mostSimilarSample[i][j] = 0;
		}

	}

	public void setValue(int i, int j, double value) {
		singleSampleResults[i].setValue(j, value);
	}



	public void compareResults() {
		double[] minValue = new double[numberOfComparisonValues];
		int[] minValueIndex = new int[numberOfComparisonValues];
		for (int i = 0; i < minValue.length; i++) {
			minValue[i] = Double.POSITIVE_INFINITY;
		}

		for (int i = 0; i < numberOfSamples; i++) {
			for (int j = 0; j < numberOfComparisonValues; j++) {
				if (singleSampleResults[i].getValue(j) < minValue[j]) {
					minValue[j] = singleSampleResults[i].getValue(j);
					minValueIndex[j] = i;
				}
			}
		}

		for (int i = 0; i < minValueIndex.length; i++) {
			System.out.println("index " + i + " speaker " + singleSampleResults[minValueIndex[i]].getSpeakerID()
					+ " sample " + singleSampleResults[minValueIndex[i]].getSampleID());
			numbersOfAssignedFeaturesToEachSpeaker[singleSampleResults[minValueIndex[i]].getSpeakerID() - 1]++;
			mostSimilarSample[singleSampleResults[minValueIndex[i]].getSpeakerID()- 1][singleSampleResults[minValueIndex[i]].getSampleID() - 1]++;
		}

		findWhichSpeakerHasMostAssignedFeatures();
		findWhichSampleOfRecognizedSpeakerIsMostSimilar();
	}

	void findWhichSpeakerHasMostAssignedFeatures() {
		int biggestNumberOfAssignedFeaturesToSpeaker = 0;
		for (int i = 0; i < numbersOfAssignedFeaturesToEachSpeaker.length; i++) {
			if (numbersOfAssignedFeaturesToEachSpeaker[i] > biggestNumberOfAssignedFeaturesToSpeaker) {
				recognizedSpeakerID = i + 1;
				biggestNumberOfAssignedFeaturesToSpeaker = numbersOfAssignedFeaturesToEachSpeaker[i];
			}
		}
	}

	void findWhichSampleOfRecognizedSpeakerIsMostSimilar() {
		int biggestNumberOfAssignedFeaturesToSingleSample = 0;
		for (int j = 0; j < mostSimilarSample[0].length; j++) {
			if (mostSimilarSample[recognizedSpeakerID - 1][j] > biggestNumberOfAssignedFeaturesToSingleSample) {
				recognizedSampleID = j + 1;
				biggestNumberOfAssignedFeaturesToSingleSample = mostSimilarSample[recognizedSpeakerID - 1][j];
			}
		}
	}



	public void setnumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	public int getnumberOfComparisonValues() {
		return numberOfComparisonValues;
	}

	public void setnumberOfComparisonValues(int numberOfComparisonValues) {
		this.numberOfComparisonValues = numberOfComparisonValues;
	}

	public int getRecognizedSpeakerID() {
		return recognizedSpeakerID;
	}

	public int getRecognizedSampleID() {
		return recognizedSampleID;
	}

	public int getSpeakerID(int index) {
		return singleSampleResults[index].getSpeakerID();
	}

	public void setSpeakerID(int index, int speakerID) {
		singleSampleResults[index].setSpeakerID(speakerID);
	}

	public int getSampleID(int index) {
		return singleSampleResults[index].getSampleID();
	}

	public void setSampleID(int index, int sampleID) {
		singleSampleResults[index].setSampleID(sampleID);
	}

}