package end2end.artemis;

import kew.core.msg.ChannelSink;

import java.util.*;

public class QReceiveBuffer<T> implements ChannelSink<T> {

    private final List<T> msgSequence;

    public QReceiveBuffer() {
        msgSequence = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void consume(T data) {
        msgSequence.add(data);
    }

    public List<T> currentMessageSequence() {
        return msgSequence;
    }

    public Set<T> waitForMessages(int howMany, int maxDelayInMs)
            throws InterruptedException {
        int delay = 100;
        for (int waitedFor = 0; waitedFor < maxDelayInMs; waitedFor += delay) {
            if (msgSequence.size() == howMany) {
                return new HashSet<>(msgSequence);
            }
            Thread.sleep(delay);
        }
        throw new IllegalStateException(
                String.format("waited for %sms and received %s messages",
                        maxDelayInMs, msgSequence.size()));
    }

}
