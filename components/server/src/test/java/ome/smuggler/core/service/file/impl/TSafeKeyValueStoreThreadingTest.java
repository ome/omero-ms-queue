package ome.smuggler.core.service.file.impl;

import static util.sequence.Arrayz.array;

import ome.smuggler.core.types.PositiveN;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class TSafeKeyValueStoreThreadingTest {

    @DataPoints
    public static Integer[] nThreadSupply = array(2, 4, 8);

    @Theory
    public void callsWithTheSameKeyAreSerialised(Integer nThreads) {
        TSafeKeyValueStoreRunner runner = new TSafeKeyValueStoreRunner(
                                                        PositiveN.of(nThreads));
        runner.runAndAssertNoInterference();
    }

}
