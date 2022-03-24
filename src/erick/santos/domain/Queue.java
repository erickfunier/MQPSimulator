package erick.santos.domain;

import java.util.ArrayList;
import java.util.List;

public class Queue {
    private final List<ProcessObj> queueList;
    private final int priority;

    public Queue(int priority) {
        queueList = new ArrayList<>();
        this.priority = priority;
    }

    /* Process getters */
    public ProcessObj getProcess(int index) {
        return queueList.get(index);
    }

    /* Process handling */
    public void addProcess(ProcessObj processObj) {
        for (int i = 0; i < queueList.size(); i++) {
            if (processObj.getCpuBurst() < getProcess(i).getCpuBurst()) {
                queueList.add(i, processObj);
                return;
            }
        }
        queueList.add(processObj);
    }

    public void removeProcess(int index) {
        if (index < queueList.size())
            queueList.remove(index);
    }

    /* Utilidades */
    public boolean isEmpty() {
        return queueList.size() == 0;
    }

    public int getSize() {
        return queueList.size();
    }

    public int getPriority() {
        return priority;
    }
}
