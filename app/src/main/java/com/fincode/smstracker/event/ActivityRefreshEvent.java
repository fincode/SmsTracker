package com.fincode.smstracker.event;

public class ActivityRefreshEvent {
    private boolean successSent;
    private boolean sentFinished;

    public ActivityRefreshEvent(boolean successSent, boolean sentFinished) {
        this.successSent = successSent;
        this.sentFinished = sentFinished;
    }

    public boolean isSentFinished() {
        return sentFinished;
    }

    public boolean isSuccessSent() {
        return successSent;
    }
}
