package com.fincode.smstracker.eventbus;


import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class BusProvider {

    private static final Bus bus = new MainThreadBus();

    public static Bus getInstance() {
        return bus;
    }

    private BusProvider() {
    }

    public static class MainThreadBus extends Bus {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void post(final Object event) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
            } else {
                handler.post(() -> MainThreadBus.super.post(event));
            }
        }
    }
}
