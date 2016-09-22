package com.galaxy.voicealarm;

import com.naver.naverspeech.kfgd_naver.IManagerCommand;

/**
 * Created by ymc12 on 2016-09-17.
 */
public class VoiceManager implements IManagerCommand{
    @Override
    public void clientReady() {

    }

    @Override
    public void audioRecording(short[] text) {

    }

    @Override
    public void partialResult(String partialText) {

    }

    @Override
    public void finalResult(String[] finalText) {

    }

    @Override
    public void recognitionError(String errorText) {

    }

    @Override
    public void clientInactive() {

    }
}
