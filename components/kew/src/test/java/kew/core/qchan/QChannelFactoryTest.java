package kew.core.qchan;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import kew.core.qchan.spi.*;
import org.junit.Before;
import org.junit.Test;

public class QChannelFactoryTest
        <QM extends HasReceiptAck & HasSchedule & HasProps> {

    private QChannelFactory<QM, String> target;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        QConnector<QM> connector = mock(QConnector.class);
        QProducer<QM> producer = mock(QProducer.class);
        QConsumer<QM> consumer = mock(QConsumer.class);

        when(connector.newProducer()).thenReturn(producer);

        target = () -> connector;
    }

    @Test
    public void canBuildSource() throws Exception {
        Object x = target.buildSource((d, out) -> {});
        assertNotNull(x);
    }

    @Test
    public void canBuildSchedulingSource() throws Exception {
        Object x = target.buildSchedulingSource((d, out) -> {});
        assertNotNull(x);
    }

    @Test
    public void canBuildCountedScheduleSource() throws Exception {
        Object x = target.buildCountedScheduleSource((d, out) -> {});
        assertNotNull(x);
    }

    @Test
    public void canBuildSink() throws Exception {
        Object x = target.buildSink(d -> {}, in -> "");
        assertNotNull(x);
    }

    @Test
    public void canBuildSinkWithNoRedelivery() throws Exception {
        Object x = target.buildSink(d -> {}, in -> "", false);
        assertNotNull(x);
    }

    @Test
    public void canBuildCountedScheduleSink() throws Exception {
        Object x = target.buildCountedScheduleSink(m -> {}, in -> "");
        assertNotNull(x);
    }

    @Test
    public void canBuildCountedScheduleSinkWithNoRedelivery() throws Exception {
        Object x = target.buildCountedScheduleSink(m -> {}, in -> "", false);
        assertNotNull(x);
    }

    @Test
    public void canBuildReschedulableSink() throws Exception {
        Object x = target.buildReschedulableSink(
                (cs, d) -> null, (d, out) -> {}, in -> "");
        assertNotNull(x);
    }

    @Test
    public void canBuildReschedulableSinkWithNoRedelivery() throws Exception {
        Object x = target.buildReschedulableSink(
                (cs, d) -> null, (d, out) -> {}, in -> "", false);
        assertNotNull(x);
    }

}
