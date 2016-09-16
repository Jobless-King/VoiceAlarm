package com.naver.naverspeech.kfgd_naver;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.naver.speech.clientapi.SpeechConfig;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016-09-11.
 */
public class NaverSpeechManager {
    private final String CLIENT_ID;
    private final SpeechConfig SPEECH_CONFIG = SpeechConfig.OPENAPI_KR;

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private AudioWriterPCM writer;
    private IManagerCommand managerCommand;

    private boolean isRunning = false;

    public static NaverSpeechManager CreateNaverSpeechManager(Activity activity, String CLIENT_ID, IManagerCommand iManagerCommand){
        NaverSpeechManager naverSpeechManager = new NaverSpeechManager(CLIENT_ID);
        naverSpeechManager.handler = new RecognitionHandler(naverSpeechManager);
        naverSpeechManager.naverRecognizer = new NaverRecognizer(activity, naverSpeechManager.handler, CLIENT_ID, naverSpeechManager.SPEECH_CONFIG);
        naverSpeechManager.managerCommand = iManagerCommand;
        return naverSpeechManager;
    }

    public boolean getRecognizeState(){
        return this.isRunning;
    }

    // initSpeechRecognizer() must be called on resume time.
    public void initSpeechRecognizer(){
        // initialize() must be called on resume time.
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    public void startRecognize(){
        if(isRunning)
            return;
        naverRecognizer.recognize();
        isRunning = true;
    }

    public void stopRecognize(){
        naverRecognizer.getSpeechRecognizer().stop();
        isRunning = false;
    }

    // releaseRecognizer() must be called on pause time.
    public void releaseRecognizer(){
        // release() must be called on pause time.
        naverRecognizer.getSpeechRecognizer().stopImmediately();
        naverRecognizer.getSpeechRecognizer().release();
        isRunning = false;
    }

    private NaverSpeechManager(String CLIENT_ID) {
        this.CLIENT_ID = CLIENT_ID;
    }


    private void handleMessage(Message msg){
        if(managerCommand == null)
            return;

        switch (msg.what){
            case ClientState.CLIENT_READY:
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                managerCommand.clientReady();
                break;
            case ClientState.AUDIO_RECORDING:
                short[] text = (short[]) msg.obj;
                writer.write(text);
                managerCommand.audioRecording(text);
                break;
            case ClientState.PARTIAL_RESULT:
                managerCommand.partialResult((String) (msg.obj));
                break;
            case ClientState.FINAL_RESULT:
                managerCommand.finalResult((String[])msg.obj);
                break;
            case ClientState.RECOGNITION_ERROR:
                if(writer != null)
                    writer.close();
                isRunning = false;
                managerCommand.recognitionError(msg.obj.toString());
                break;
            case ClientState.CLIENT_INACTIVE:
                if(writer != null)
                    writer.close();
                isRunning = false;
                managerCommand.clientInactive();
                break;
        }
    }

    static class RecognitionHandler extends Handler {
        private final NaverSpeechManager mNaverSpeechManager;

        RecognitionHandler(NaverSpeechManager naverSpeechManager) {
            this.mNaverSpeechManager = naverSpeechManager;
        }

        @Override
        public void handleMessage(Message msg) {
            if(mNaverSpeechManager != null)
                mNaverSpeechManager.handleMessage(msg);
        }
    }
}
