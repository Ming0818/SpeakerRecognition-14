/*
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -------------------------------------------------------------
*
* TarsosDSP is developed by Joren Six at IPEM, University Ghent
*  
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/

package calculations;

import java.io.Serializable;

public class MFCC implements Serializable{
    protected final static int numMelFilters = 23;
    protected final static double lowerFilterFreq = 133.3334;
    public int numCepstra = 13;
   public MFCC()
   {
	   
   }
   public double [] getMFCC(double samplingRate,double sample [])
   {
	   int[] cbin = fftBinIndices(samplingRate,sample.length);
		double[] fbank = melFilter(sample,cbin);
		double[] f = nonLinearTransformation(fbank);
		double[] cepc = cepCoefficients(f);
		return cepc;
   }
	
	  public int[] fftBinIndices(double samplingRate,int frameSize){
	        int cbin[] = new int[numMelFilters + 2];

	        cbin[0] = (int)Math.round(lowerFilterFreq / samplingRate * frameSize);
	        cbin[cbin.length - 1] = (int)(frameSize / 2);

	        for (int i = 1; i <= numMelFilters; i++){
	            double fc = centerFreq(i,samplingRate);

	            cbin[i] = (int)Math.round(fc / samplingRate * frameSize);
	        }

	        return cbin;
	    }
	    public double[] melFilter(double bin[], int cbin[]){
	        double temp[] = new double[numMelFilters + 2];

	        for (int k = 1; k <= numMelFilters; k++){
	            double num1 = 0, num2 = 0;

	            for (int i = cbin[k - 1]; i <= cbin[k]; i++){
	                num1 += ((i - cbin[k - 1] + 1) / (cbin[k] - cbin[k-1] + 1)) * bin[i];
	            }

	            for (int i = cbin[k] + 1; i <= cbin[k + 1]; i++){
	                num2 += (1 - ((i - cbin[k]) / (cbin[k + 1] - cbin[k] + 1))) * bin[i];
	            }

	            temp[k] = num1 + num2;
	        }

	        double fbank[] = new double[numMelFilters];
	        for (int i = 0; i < numMelFilters; i++){
	            fbank[i] = temp[i + 1];
	        }

	        return fbank;
	    }
	    public double[] cepCoefficients(double f[]){
	        double cepc[] = new double[numCepstra];

	        for (int i = 0; i < cepc.length; i++){
	            for (int j = 1; j <= numMelFilters; j++){
	                cepc[i] += f[j - 1] * Math.cos(Math.PI * i / numMelFilters * (j - 0.5));
	            }
	        }

	        return cepc;
	    }
	    public double[] nonLinearTransformation(double fbank[]){
	        double f[] = new double[fbank.length];
	        final double FLOOR = -50;

	        for (int i = 0; i < fbank.length; i++){
	            f[i] = Math.log(fbank[i]);

	            // check if ln() returns a value less than the floor
	            if (f[i] < FLOOR) f[i] = FLOOR;
	        }

	        return f;
	    }
	  private static double centerFreq(int i,double samplingRate){
	        double mel[] = new double[2];
	        mel[0] = freqToMel(lowerFilterFreq);
	        mel[1] = freqToMel(samplingRate / 2);

	        // take inverse mel of:
	        double temp = mel[0] + ((mel[1] - mel[0]) / (numMelFilters + 1)) * i;
	        return inverseMel(temp);
	    }
	    protected static double log10(double value){
	        return Math.log(value) / Math.log(10);
	    }
	  protected static double freqToMel(double freq){
	        return 2595 * log10(1 + freq / 700);
	    }
	    private static double inverseMel(double x){
	        double temp = Math.pow(10, x / 2595) - 1;
	        return 700 * (temp);
	    }
}
