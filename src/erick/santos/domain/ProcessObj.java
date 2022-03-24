package erick.santos.domain;

public class ProcessObj {
    private final String name;
    private int priority;
    private final long cpuBurst;
    private String status;
    private long createdTime;
    private long starTime;

    public ProcessObj(String name, int priority, long cpuBurst, String status) {
        this.name = name;
        this.priority = priority;
        this.cpuBurst = cpuBurst;
        this.status = status;
        this.createdTime = 0;
        this.starTime = 0;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getCpuBurst() {
        return cpuBurst;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStarTime() {
        return starTime;
    }

    public void setStarTime(long starTime) {
        if (this.starTime == 0)
            this.starTime = starTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        if (this.createdTime == 0)
            this.createdTime = createdTime;
    }
}
