package ome.smuggler.core.service.imports.impl;

import ome.smuggler.core.msg.ChannelSource;
import ome.smuggler.core.msg.SchedulingSource;
import ome.smuggler.core.service.file.KeyValueStore;
import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.service.file.impl.MemoryKeyValueStore;
import ome.smuggler.core.service.log.LogService;
import ome.smuggler.core.service.mail.MailRequestor;
import ome.smuggler.core.service.omero.ImportService;
import ome.smuggler.core.service.omero.SessionService;
import ome.smuggler.core.types.*;
import util.lambda.Functions;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ome.smuggler.core.types.ValueParserFactory.email;
import static ome.smuggler.core.types.ValueParserFactory.uri;
import static org.mockito.Mockito.*;
import static util.sequence.Streams.*;

public class Utils {

    public static ImportEnv dummyImportEnv() {
        return mock(ImportEnv.class);
    }

    @SuppressWarnings("unchecked")
    public static ImportEnv fullyMockedImportEnv(String sysAdminEmail) {
        return new ImportEnv(
            mock(ImportConfigSource.class),
            mock(SessionService.class),
            mock(ImportService.class),
            mock(ChannelSource.class),     // ChannelSource<QueuedImport>
            mock(SchedulingSource.class),  // SchedulingSource<ProcessedImport>
            mock(KeyValueStore.class),     // KeyValueStore<ImportBatchId, ImportBatchStatus>
            mock(TaskFileStore.class),     // TaskFileStore<ImportId>
            mock(MailRequestor.class),
            Optional.ofNullable(email(sysAdminEmail).getRight()),
            mock(LogService.class)
        );
    }

    public static ImportEnv fullyMockedImportEnv() {
        return fullyMockedImportEnv(null);
    }


    @SuppressWarnings("unchecked")
    public static ImportEnv mockedImportEnvWithMemBatchStore(String sysAdminEmail) {
        return new ImportEnv(
                mock(ImportConfigSource.class),
                mock(SessionService.class),
                mock(ImportService.class),
                mock(ChannelSource.class),     // ChannelSource<QueuedImport>
                mock(SchedulingSource.class),  // SchedulingSource<ProcessedImport>
                new MemoryKeyValueStore<>(),   // KeyValueStore<ImportBatchId, ImportBatchStatus>
                mock(TaskFileStore.class),     // TaskFileStore<ImportId>
                mock(MailRequestor.class),
                Optional.ofNullable(email(sysAdminEmail).getRight()),
                mock(LogService.class)
        );
    }

    public static ImportEnv mockedImportEnvWithMemBatchStore() {
        return mockedImportEnvWithMemBatchStore(null);
    }

    public static Map<ImportBatchId, ImportBatchStatus> batchStoreData(
            ImportEnv mockedImportEnvWithMemBatchStore) {
        MemoryKeyValueStore<ImportBatchId, ImportBatchStatus> data =
                (MemoryKeyValueStore<ImportBatchId, ImportBatchStatus>)
                        mockedImportEnvWithMemBatchStore.batchStore();
        return data.store();
    }

    public static ImportInput newImportInput(String sessionKey) {
        String uniqueTargetUri = "target/file/" + new BaseStringId().id();
        return new ImportInput(email("user@some.edu").getRight(),
                               uri(uniqueTargetUri).getRight(),
                               uri("omero:1234").getRight(),
                               sessionKey);
    }

    public static Stream<ImportInput> newImportRequests(int howMany) {
        return IntStream.range(0, howMany)
                        .mapToObj(n -> newImportInput("sessionKey"));
    }

    public static Stream<ImportInput> newImportRequests(String...sessionKey) {
        return Stream.of(sessionKey).map(Utils::newImportInput);
    }

    public static ImportBatch newImportBatch(String...sessionKey) {
        Stream<ImportInput> rs = Stream.of(sessionKey)
                                       .map(Utils::newImportInput);
        return new ImportBatch(rs);
    }

    public static QueuedImport newQueuedImport() {
        return newImportBatch("sessionKey").imports().findFirst().get();
    }

    public static ProcessedImport failedProcessedImport() {
        return ProcessedImport.failed(newQueuedImport());
    }

    public static ProcessedImport succeededProcessedImport() {
        return ProcessedImport.succeeded(newQueuedImport());
    }

    @SafeVarargs
    public static ImportBatchStatus createAndProcessBatch(
            Function<QueuedImport, ProcessedImport>...succeededOrFailed) {
        String[] sessionKeys = IntStream.range(0, succeededOrFailed.length)
                                        .mapToObj(i -> "k" + i)
                                        .toArray(String[]::new);
        ImportBatch batch = newImportBatch(sessionKeys);
        ImportBatchStatus status = new ImportBatchStatus(batch);

        zipWith(Functions::apply,
                Stream.of(succeededOrFailed),
                batch.imports())
            .forEach(status::addToCompleted);

        return status;
    }

}
