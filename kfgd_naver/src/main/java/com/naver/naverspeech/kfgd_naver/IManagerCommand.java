package com.naver.naverspeech.kfgd_naver;

public interface IManagerCommand {
    public void clientReady();
    public void audioRecording(short[] text);
    public void partialResult(String partialText);
    public void finalResult(String[] finalText);
    public void recognitionError(String errorText);
    public void clientInactive();
}
