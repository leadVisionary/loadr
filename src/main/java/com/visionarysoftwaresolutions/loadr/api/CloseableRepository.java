package com.visionarysoftwaresolutions.loadr.api;

import java.io.Closeable;

public interface CloseableRepository<T> extends Repository<T>, AutoCloseable {
}
