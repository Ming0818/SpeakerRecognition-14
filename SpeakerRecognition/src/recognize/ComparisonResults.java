package recognize;

// comparison results for each sample
class SingleSampleResults
{
	double value[];
	int speakerID;
	int sampleID;
	
	public double getValue(int index) {
		return value[index];
	}
	public void setValue(int index, double value) {
		this.value[index] = value;
	}
	
	public SingleSampleResults(int length)
	{
		value=new double[length];
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


public class ComparisonResults
{
	SingleSampleResults singleSampleResults[];

	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	int columns,rows,speakers;
	public int getRecognizedSpeakerID() {
		return recognizedSpeakerID;
	}

	public int getRecognizedSampleID() {
		return recognizedSampleID;
	}
	
	int results[];
	int closestSample[][];
	int recognizedSpeakerID;
	int recognizedSampleID;
	public int getSpeakerID(int index) {
		return singleSampleResults[index].getSpeakerID();
	}
	public void setSpeakerID(int index,int speakerID) {
		singleSampleResults[index].setSpeakerID(speakerID);		
		}
	
	public int getSampleID(int index) {
		return singleSampleResults[index].getSampleID();
		}
	
	public void setSampleID(int index,int sampleID) {
		singleSampleResults[index].setSampleID(sampleID);		
	}
	public ComparisonResults(int columns, int rows,int numberOfSpeakers)
	{
		this.columns=columns;
		this.rows=rows;
		this.results=new int [numberOfSpeakers];
		this.closestSample=new int[numberOfSpeakers][columns/numberOfSpeakers];
		this.recognizedSpeakerID=0;
		this.recognizedSampleID=0;
		
		singleSampleResults=new SingleSampleResults[columns];
		for(int i=0;i<columns;i++)
		{
			singleSampleResults[i]=new SingleSampleResults(rows);

		}
		for(int i=0;i<results.length;i++)
		{
			results[i]=0;
		}
		for(int i=0;i<closestSample.length;i++)
		{
			for(int j=0;j<closestSample[0].length;j++)
			closestSample[i][j]=0;
		}
	
	}
	public void setValue(int i,int j,double value)
	{
		singleSampleResults[i].setValue(j, value);
	}
	public void printValues()
	{
		for(int i=0;i<columns;i++)
		{
			for(int j=0;j<rows;j++)
			{
				System.out.print(singleSampleResults[i].getValue(j)+" ");
			}
			System.out.println();
		}
	}
	public void compareResults()
	{
		double []min=new double[rows];
		int []minIndex=new int[rows];
		for(int i=0;i<min.length;i++)
		{
			min[i]=Double.POSITIVE_INFINITY;
		}

		
		for(int i=0;i<columns;i++)
		{
			for(int j=0;j<rows;j++)
			{
				if(singleSampleResults[i].getValue(j)<min[j])
				{
					min[j]=singleSampleResults[i].getValue(j);
					minIndex[j]=i;
				}
			}
		}
		
		for(int i=0;i<minIndex.length;i++)
		{
			System.out.println("index "+i+" speaker "+singleSampleResults[minIndex[i]].getSpeakerID()+" sample "+singleSampleResults[minIndex[i]].getSampleID());
			results[singleSampleResults[minIndex[i]].getSpeakerID()-1]++;
			closestSample[singleSampleResults[minIndex[i]].getSpeakerID()-1][singleSampleResults[minIndex[i]].getSampleID()-1]++;

			
		}

		findWhichSpeakerHasMostAssignedFeatures();
		findWhichSampleOfRecognizedSpeakerIsClosest();

		
			


		}
	
	void findWhichSpeakerHasMostAssignedFeatures()
	{
		int maxvalue=0;
		for(int i=0;i<results.length;i++)
		{
			if(results[i]>maxvalue)
			{
				recognizedSpeakerID=i+1;
				maxvalue=results[i];
			}
			System.out.print(i+1+" wynik "+results[i]+" ");
			
		}
	}
	void findWhichSampleOfRecognizedSpeakerIsClosest()
	{
		int maxValueToCompare=0;
		for(int j=0;j<closestSample[0].length;j++)
		{
			if(closestSample[recognizedSpeakerID-1][j]>maxValueToCompare)
			{
				recognizedSampleID=j+1;
				maxValueToCompare=closestSample[recognizedSpeakerID-1][j];
			}

		}

	}

	
}