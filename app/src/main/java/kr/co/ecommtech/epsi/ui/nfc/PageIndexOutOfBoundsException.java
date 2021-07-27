package kr.co.ecommtech.epsi.ui.nfc;

public class PageIndexOutOfBoundsException extends Exception {
    String message;

    PageIndexOutOfBoundsException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\n" + message;
    }
}
