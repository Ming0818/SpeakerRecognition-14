# SpeakerRecognition

Program recognizes a speaker, basing on earlier processed recordings. 
In order to match a recording with proper speaker,
program uses Dynamic Time Warping algorithm and features computed from a spectrum- and time domain.

In main menu one is able to choose if recognition will be performed with already existing samples or with new freshly recorded.

![](https://github.com/rc000/SpeakerRecognition/blob/master/SpeakerRecognition/src/screenshots/menu.png)

If recording  has been chosen, program displays options.
![](https://github.com/rc000/SpeakerRecognition/blob/master/SpeakerRecognition/src/screenshots/options.png)

Then we go to recording, the program shows which sample will be recorded after clicking the start

![](https://github.com/rc000/SpeakerRecognition/blob/master/SpeakerRecognition/src/screenshots/recording.png)

When all base samples are recorded, the time is coming for recording sample to recognize.
![](https://github.com/rc000/SpeakerRecognition/blob/master/SpeakerRecognition/src/screenshots/recordSampleToRecognize.png)

Subsequently program binds this sample with most similar sample from these recorded earlier.
![](https://github.com/rc000/SpeakerRecognition/blob/master/SpeakerRecognition/src/screenshots/Energy.png)
![](https://github.com/rc000/SpeakerRecognition/blob/master/SpeakerRecognition/src/screenshots/centroid.png)
![](https://github.com/rc000/SpeakerRecognition/blob/master/SpeakerRecognition/src/screenshots/MFCC.png)
There is also a possibility to compare sample to recognize with another, less similar sample.
![](https://github.com/rc000/SpeakerRecognition/blob/master/SpeakerRecognition/src/screenshots/othersamples.png)
![](https://github.com/rc000/SpeakerRecognition/blob/master/SpeakerRecognition/src/screenshots/anotherSample.png)

If loading samples has been chosen at the beggining:
![](https://github.com/rc000/SpeakerRecognition/blob/master/SpeakerRecognition/src/screenshots/loading.png)

