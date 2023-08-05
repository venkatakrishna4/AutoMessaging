package com.krish.automessaging.service;

/**
 * The Interface BaseServiceProvider.
 *
 * @param <T>
 *            the generic type
 */
public interface BaseServiceProvider<T> {

    /**
     * Gets the trimmed value.
     *
     * @param data
     *            the data
     *
     * @return the trimmed value
     */
    String getTrimmedValue(String data);

    T getNewObject(T data);
}
