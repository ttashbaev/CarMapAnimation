package com.example.timur.carmapanimation.ui;

public interface MainContract {

    interface View {

    }

    interface Presenter extends LifeCycle<View>{
        void getMap();
    }
}
