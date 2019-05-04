package com.streamer.worker.http;

public enum JobSatus {

    UNKNOWN(0), EXSIT(1001), NOT_FOUND(1002), RUNNING(2003), STOPED(2004);

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    JobSatus(int status) {
        this.status = status;
    }

    public static JobSatus valOf(int status) {
        JobSatus current = JobSatus.UNKNOWN;
        for (JobSatus satus : values()) {
            if (status == satus.getStatus()) {
                current = satus;
            }
        }
        return current;
    }
}
