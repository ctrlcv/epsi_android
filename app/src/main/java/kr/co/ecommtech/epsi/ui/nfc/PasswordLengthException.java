package kr.co.ecommtech.epsi.ui.nfc;

public class PasswordLengthException extends Exception {
    PasswordLengthException() {}

    @Override
    public String getMessage() {
        return super.getMessage() + "Password length must be 4 bytes";
    }
}
