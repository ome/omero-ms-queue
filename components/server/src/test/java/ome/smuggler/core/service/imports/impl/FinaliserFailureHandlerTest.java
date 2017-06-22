package ome.smuggler.core.service.imports.impl;

import ome.smuggler.core.types.ImportFinalisationPhase;
import org.junit.Test;

import static ome.smuggler.core.service.imports.impl.Utils.*;

public class FinaliserFailureHandlerTest {

    public static FinaliserFailureHandler newFinaliserFailureHandler() {
        return new FinaliserFailureHandler(fullyMockedImportEnv());
    }

    @Test
    public void theresAnHandlerForEachFinalisationPhase() {
        FinaliserFailureHandler target = newFinaliserFailureHandler();
        for (ImportFinalisationPhase status : ImportFinalisationPhase.values()) {
            target.accept(failedProcessedImport());
        }
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullEnv() {
        new FinaliserFailureHandler(null);
    }

    @Test(expected = NullPointerException.class)
    public void acceptThrowsIfNullEnv() {
        newFinaliserFailureHandler().accept(null);
    }

}
