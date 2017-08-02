package kew.core.qchan.impl;

import kew.core.qchan.spi.HasProps;
import kew.core.qchan.spi.HasReceiptAck;
import kew.core.qchan.spi.HasSchedule;
import kew.core.qchan.spi.QMessageType;
import util.types.FutureTimepoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestQMsg implements HasProps, HasReceiptAck, HasSchedule {

    public FutureTimepoint schedule;
    public Map<String, String> stringProps;
    public Map<String, Long> longProps;
    public QMessageType type;
    public Boolean removedFromQueue;

    public TestQMsg() {
        stringProps = new HashMap<>();
        longProps = new HashMap<>();
    }

    @Override
    public void setScheduledDeliveryTime(FutureTimepoint when) {
        schedule = when;
    }

    @Override
    public void putProp(String key, String value) {
        stringProps.put(key, value);
    }

    @Override
    public void putProp(String key, long value) {
        longProps.put(key, value);
    }

    @Override
    public Optional<String> lookupStringValue(String key) {
        return Optional.ofNullable(stringProps.get(key));
    }

    @Override
    public Optional<Long> lookupLongValue(String key) {
        return Optional.ofNullable(longProps.get(key));
    }

    @Override
    public void removeFromQueue() {
        removedFromQueue = true;
    }

}
