package kew.core.qchan.impl;

import kew.core.qchan.spi.QMessageType;
import kew.core.qchan.spi.QMsgFactory;

public class TestQMsgFactory implements QMsgFactory<TestQMsg> {

    @Override
    public TestQMsg queueMessage(QMessageType t) {
        TestQMsg msg = new TestQMsg();
        msg.type = t;
        return msg;
    }

}
