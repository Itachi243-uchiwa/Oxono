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

    /**
     * Adds a key-value pair to the event data.
     *
     * @param key   the key to associate with the value; must not be null or empty
     * @param value the value to associate with the key
     * @return the current {@code OxonoEvent} instance for method chaining
     * @throws OxonoException if the key is null or empty
     */
    public OxonoEvent addData(String key, Object value) {
        if (key == null || key.isEmpty()) {
            throw new OxonoException("Key cannot be null or empty.");
        }
        eventData.put(key, value);
        return this;
    }

    /**
     * Retrieves the associated event.
     *
     * @return an instance of ObservableEvent representing the event.
     */
    public ObservableEvent getEvent() {
        return event;
    }

    /**
     * Retrieves event data associated with the specified key and casts it to the requested type if possible.
     *
     * @param key the key associated with the event data to retrieve
     * @param type the class type to which the event data should be cast
     * @return the event data cast to the specified type, or null if the data does not exist or cannot be cast
     */
    public <T> T getEventData(String key, Class<T> type) {
        Object value = eventData.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    /**
     * Returns a string representation of the OxonoEvent instance, including its event type and associated event data.
     *
     * @return a string representation of this OxonoEvent instance.
     */
    @Override
    public String toString() {
        return "OxonoEvent{" +
                "event=" + event +
                ", eventData=" + eventData +
                '}';
    }
}
