package ome.smuggler.core.service.imports.impl;

import ome.smuggler.core.msg.RepeatAction;
import ome.smuggler.core.service.imports.ImportFinaliser;
import ome.smuggler.core.types.ImportFinalisationPhase;
import org.junit.Test;

import static ome.smuggler.core.service.imports.impl.FinaliserEventTriggerTest.dummyFinaliser;
import static ome.smuggler.core.service.imports.impl.Utils.*;
import static org.junit.Assert.*;


public class FinaliserEventConsumerTest {

    class TestFinaliser extends Finaliser {

        RepeatAction outcome;

        TestFinaliser(RepeatAction outcome) {
            super(dummyImportEnv());
            this.outcome = outcome;
        }

        @Override
        protected ImportFinaliser handlerFor(ImportFinalisationPhase status) {
            return x -> outcome;
        }
    }

    @Test
    public void theresAnHandlerForEachFinalisationPhase() {
        Finaliser target = dummyFinaliser();
        for (ImportFinalisationPhase status : ImportFinalisationPhase.values()) {
            ImportFinaliser handler = target.handlerFor(status);
            assertNotNull(handler);
        }
    }

    @Test
    public void consumeDelegatesExecution() {
        for (RepeatAction expected : RepeatAction.values()) {
            Finaliser target = new TestFinaliser(expected);
            RepeatAction actual = target.consume(succeededProcessedImport());
            assertEquals(expected, actual);
        }
    }

    @Test(expected = NullPointerException.class)
    public void consumeThrowsIfNullProcessedImport() {
        dummyFinaliser().consume(null);
    }

}
