package com.krish.automessaging.service;

public interface BaseServiceProvider<T> {
    T getTrimmedValue(T data);
}
