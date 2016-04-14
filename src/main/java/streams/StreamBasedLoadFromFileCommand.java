package streams;

import com.visionarysoftwaresolutions.loadr.api.Command;

import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Stream;

public final class StreamBasedLoadFromFileCommand<T,U> implements Command<File> {
    private final Logger log;
    private final int publishers;
    private final Function<String, T> extract;
    private final Function<T, U> transform;
    private final Function<U, String> load;


    public StreamBasedLoadFromFileCommand(final Logger log,
                                          final int publishers,
                                          final Function<String, T> extract,
                                          final Function<T, U> transform,
                                          final Function<U, String> load
                                          ) {
        if (log == null) {
            throw new IllegalArgumentException("log should not be null");
        }
        this.log = log;
        if (publishers < 0) {
            throw new IllegalArgumentException("should not get publishers < 0");
        }
        this.publishers = publishers;
        if (extract == null) {
            throw new IllegalArgumentException("extract should not be null");
        }
        this.extract = extract;
        if (transform == null) {
            throw new IllegalArgumentException("transform should not be null");
        }
        this.transform = transform;
        if (load == null) {
            throw new IllegalArgumentException("load should not be null");
        }
        this.load = load;
    }

    @Override
    public void execute(final File parameter) {
        final ForkJoinPool loadPool = new ForkJoinPool(publishers, loadThreadFactory(), loadExceptionHandler(log), true);
        try {
            final Stream<String> lines = Files.lines(parameter.toPath());
            final long written = loadPool.submit(applyETL(lines)).get();
            log.info(String.format("%nWrote out %d records for %s%n", written, parameter.getAbsolutePath()));
        } catch (Exception e) {
            log.error("died while waiting", e);
        }
    }

    private Callable<Long> applyETL(final Stream<String> lines) {
        return () -> lines
                .parallel()
                .map(extract)
                .map(transform)
                .map(load)
                .count();
    }

    private Thread.UncaughtExceptionHandler loadExceptionHandler(final Logger log) {
        return (t, e) -> log.warn(String.format("%s died due to %n", t.getName()), e);
    }

    private ForkJoinPool.ForkJoinWorkerThreadFactory loadThreadFactory() {
        return new ForkJoinPool.ForkJoinWorkerThreadFactory() {
            private volatile int count = 0;

            @Override
            public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
                final ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
                thread.setName(String.format("ETL-thread-%d", count));
                count = count +1;
                return thread;
            }
        };
    }
}
