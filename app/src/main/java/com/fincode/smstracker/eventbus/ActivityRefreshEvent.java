package com.fincode.smstracker.eventbus;

public class ActivityRefreshEvent {
    private boolean successSent;

    public ActivityRefreshEvent(boolean successSent) {
        this.successSent = successSent;
    }

    public boolean isSuccessSent() {
        return successSent;
    }
}
