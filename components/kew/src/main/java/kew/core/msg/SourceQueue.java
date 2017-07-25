package kew.core.msg;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * A channel source that queues messages in memory and allows to retrieve the
 * head of the queue to consume messages or do a bulk retrieval of all queued
 * messages.
 */
public class SourceQueue<M, D> implements MessageSource<M, D> {

    private final ConcurrentLinkedQueue<ChannelMessage<M, D>> sendBuffer;
    
    /**
     * Creates a new instance.
     */
    public SourceQueue() {
        sendBuffer = new ConcurrentLinkedQueue<>();
    }
    
    @Override
    public void send(ChannelMessage<M, D> msg) throws Exception {
        requireNonNull(msg, "msg");
        sendBuffer.offer(msg);
    }
    
    /**
     * @return the oldest message item (metadata + data) on the queue or empty 
     * if the queue has no items.
     */
    public Optional<ChannelMessage<M, D>> head() {
        return Optional.ofNullable(sendBuffer.poll());
    }
    
    /**
     * Same as {@link #head()} but discards message metadata.
     * @return the data of the oldest message item on the queue or empty if the
     * queue has no items.
     */
    public Optional<D> headData() {
        return head().map(ChannelMessage::data);
    }
    
    /**
     * Removes all the items currently in the queue, returning them in FIFO
     * order.
     * @return the items in the queue or empty if the queue has no items.
     */
    public List<ChannelMessage<M, D>> dequeue() {
        ArrayList<ChannelMessage<M, D>> queued = new ArrayList<>();
        while (!sendBuffer.isEmpty()) {
            ChannelMessage<M, D> head = sendBuffer.poll();
            if (head != null) {   // (*)
                queued.add(head);
            } 
        }
        return queued;
    }
    /* NOTE. Race conditions.
     * Another thread may call head() and remove the last element, then we'd 
     * poll() and get null, which we don't want to add to queued.
     */
    
    /**
     * Same as {@link #dequeue()} but discards message metadata.
     * @return the data in each queued message or empty if the queue has no 
     * items.
     */
    public List<D> dequeueData() {
        return dequeue().stream().map(ChannelMessage::data).collect(toList());
    }
    
}
