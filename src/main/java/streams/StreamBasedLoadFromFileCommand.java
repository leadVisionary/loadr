package streams;

import com.visionarysoftwaresolutions.loadr.api.Command;

import org.slf4j.Logger;

import java.io.File;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Stream;

public final class StreamBasedLoadFromFileCommand<T> implements Command<File> {
    private final Logger log;
    private final int publishers;
    private final Function<File, Stream<String>> extract;
    private final Function<String, T> transform;
    private final Function<T, String> load;


    public StreamBasedLoadFromFileCommand(final Logger log,
                                          final int publishers,
                                          final Function<File, Stream<String>> extract,
                                          final Function<String, T> transform,
                                          final Function<T, String> load
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
            final long written = loadPool.submit(() ->
                    extract.apply(parameter)
                    .parallel()
                    .map(transform)
                    .map(load)
                    .count()).get();
            log.info(String.format("%nWrote out %d records for %s%n", written, parameter.getAbsolutePath()));
        } catch (Exception e) {
            log.error("died while waiting", e);
        }
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
