package kr.co.ecommtech.epsi.ui.nfc;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.util.ArrayList;

public class NdefMessageParser {
    public static ArrayList<ParsedRecord> parse(NdefMessage message) {
        return getRecords(message.getRecords());
    }

    public static ArrayList<ParsedRecord> getRecords(NdefRecord[] records) {
        ArrayList<ParsedRecord> elements = new ArrayList<ParsedRecord>();
        for (NdefRecord record : records) {
            if (UriRecord.isUri(record)) {
                elements.add(UriRecord.parse(record));
            } else if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record));
            }
        }
        return elements;
    }
}
