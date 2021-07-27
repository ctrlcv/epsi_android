package kr.co.ecommtech.epsi.ui.nfc;

public interface INTagCallback {
    void commandStart();
    void commandDone();
    void commandError();
}
