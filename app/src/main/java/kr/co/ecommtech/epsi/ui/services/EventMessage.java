package kr.co.ecommtech.epsi.ui.services;

import java.util.Arrays;

public class EventMessage {
    public Event what;
    public Object[] data;

    public EventMessage(Event what) {
        this.what = what;
    }

    public EventMessage(Event what, Object... data) {
        this.what = what;
        this.data = data;
    }

    @Override
    public String toString() {
        return "EventMessage{" +
                "what=" + what +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
