package com.example.timur.carmapanimation.ui;

public interface LifeCycle<V> {

    void bind(V view);

    void unbind();
}
