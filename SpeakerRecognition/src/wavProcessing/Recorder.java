package wavProcessing;

import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
 
/**
 * A sample program is to demonstrate how to record sound in Java
 * author: www.codejava.net
 */
public class  Recorder {
    // record duration, in milliseconds
    static long RECORD_TIME;  
    File wavFile;

    public Recorder()
    {
    	
    }
   public Recorder (String file)
    {
    	wavFile= new File(file);
    }
    // path of the wav file

 
    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    TargetDataLine line;
 
    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }
 
    /**
     * Captures the sound and record into a WAV file
     */
    double [] start() {
     	List<Byte> lista = new ArrayList<>();
        byte b[]=new byte[44100*3];
        double d[]=new double[44100*3];

        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing
 
            System.out.println("Start capturing...");
 
            AudioInputStream ais = new AudioInputStream(line);
 
            System.out.println("Start recording...");
 
            // start recording
            AudioSystem.write(ais, fileType, wavFile);
              ais.read( b);
              
            
              
 
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        for (int i=0;i<b.length;i++)
        {
        	d[i]=b[i];
        }
        return d;
    }
 
    /**
     * Closes the target data line to finish capturing and recording
     */
   public  void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }
 
    /**
     * Entry to run the program
     */
    public   double [] main(String file,int seconds ) {
        final  Recorder recorder = new  Recorder(file);
        RECORD_TIME=seconds*1000;
         // creates a new thread that waits for a specified
        // of time before stopping
        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(RECORD_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                recorder.finish();
            }
        });
 
        stopper.start();
        double []d=new double [44100*3];
        // start recording
        d=recorder.start();
        return d;
    }
}
