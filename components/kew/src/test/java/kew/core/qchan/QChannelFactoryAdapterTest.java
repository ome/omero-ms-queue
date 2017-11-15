package kew.core.qchan;

import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import kew.core.msg.RepeatAction;
import kew.core.qchan.spi.*;
import util.io.SinkWriter;
import util.io.SourceReader;

public class QChannelFactoryAdapterTest
        <QM extends HasReceiptAck & HasSchedule & HasProps> {

    private QChannelFactory<QM, String> target;
    private QChannelFactoryAdapter<QM, String> wrapper;
    private SinkWriter<String, OutputStream> serializer;
    private SourceReader<InputStream, String> deserializer;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        target = mock(QChannelFactory.class);
        serializer = (out, d) -> {};
        deserializer = in -> "";
        wrapper = new QChannelFactoryAdapter<>(
                        target, serializer, deserializer);
    }

    @Test
    public void forwardBuildSource() throws Exception {
        wrapper.buildSource();
        verify(target).buildSource(any());
    }

    @Test
    public void forwardBuildSchedulingSource() throws Exception {
        wrapper.buildSchedulingSource();
        verify(target).buildSchedulingSource(eq(serializer));
    }

    @Test
    public void forwardBuildCountedScheduleSource() throws Exception {
        wrapper.buildCountedScheduleSource();
        verify(target).buildCountedScheduleSource(eq(serializer));
    }

    @Test
    public void forwardBuildSink() throws Exception {
        wrapper.buildSink(d -> {});
        verify(target).buildSink(any(), eq(deserializer));
    }

    @Test
    public void forwardBuildSinkWithNoRedelivery() throws Exception {
        wrapper.buildSink(d -> {}, false);
        verify(target).buildSink(any(), eq(deserializer), eq(false));
    }

    @Test
    public void forwardBuildCountedScheduleSink() throws Exception {
        wrapper.buildCountedScheduleSink(d -> {});
        verify(target).buildCountedScheduleSink(any(), eq(deserializer));
    }

    @Test
    public void forwardBuildCountedScheduleSinkWithNoRedelivery()
            throws Exception {
        wrapper.buildCountedScheduleSink(d -> {}, false);
        verify(target).buildCountedScheduleSink(
                any(), eq(deserializer), eq(false));
    }

    @Test
    public void forwardBuildReschedulableSink() throws Exception {
        wrapper.buildReschedulableSink((cs, d) -> null);
        verify(target).buildReschedulableSink(
                any(), eq(serializer), eq(deserializer));
    }

    @Test
    public void forwardBuildReschedulableSinkWithNoRedelivery()
            throws Exception {
        wrapper.buildReschedulableSink((cs, d) -> null, false);
        verify(target).buildReschedulableSink(
                            any(), eq(serializer), eq(deserializer), eq(false));
    }

    @Test
    public void forwardBuildRepeatSink() throws Exception {
        wrapper.buildRepeatSink(d -> RepeatAction.Repeat,
                                Collections.emptyList(),
                                d -> {});
        verify(target).buildRepeatSink(any(),
                                       eq(Collections.emptyList()),
                                       any(),
                                       eq(serializer),
                                       eq(deserializer));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullFactory() {
        new QChannelFactoryAdapter<>(null, (out, d) -> {}, in -> "");
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullSerializer() {
        new QChannelFactoryAdapter<>(target, null, in -> "");
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullDeserializer() {
        new QChannelFactoryAdapter<>(target, (out, d) -> {}, null);
    }

}
