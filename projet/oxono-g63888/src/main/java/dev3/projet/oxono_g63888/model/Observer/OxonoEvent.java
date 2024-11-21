package dev3.projet.oxono_g63888.model.Observer;

import dev3.projet.oxono_g63888.model.OxonoException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OxonoEvent {

    private final ObservableEvent event;
    private final Map<String, Object> eventData;

    public OxonoEvent(ObservableEvent event) {
        if (event == null) {
            throw new OxonoException("Event cannot be null.");
        }
        this.event = event;
        this.eventData = new HashMap<>();
    }

    public OxonoEvent addData(String key, Object value) {
        if (key == null || key.isEmpty()) {
            throw new OxonoException("Key cannot be null or empty.");
        }
        eventData.put(key, value);
        return this;
    }

    public ObservableEvent getEvent() {
        return event;
    }

    public <T> T getEventData(String key, Class<T> type) {
        Object value = eventData.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }


    public Map<String, Object> getAllEventData() {
        return Collections.unmodifiableMap(eventData);
    }

    @Override
    public String toString() {
        return "OxonoEvent{" +
                "event=" + event +
                ", eventData=" + eventData +
                '}';
    }
}
