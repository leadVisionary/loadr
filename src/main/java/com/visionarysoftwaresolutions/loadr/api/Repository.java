package com.visionarysoftwaresolutions.loadr.api;

public interface Repository<T> {
    void save(T toSave);
}
