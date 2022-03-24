package erick.santos.domain;

import java.util.ArrayList;
import java.util.List;

public class MultiLevelQueue {
    private final List<Queue> multiLevelQueue;

    public MultiLevelQueue() {
        this.multiLevelQueue = new ArrayList<>();
    }

    /* Queue getters */
    public Queue getQueueByPriority(int priority) {
        for (Queue queue : multiLevelQueue) {
            if (queue.getPriority() == priority) {
                return queue;
            }
        }
        return null;
    }

    public Queue getQueueByIndex(int index) {
        return multiLevelQueue.get(index);
    }

    /* MultiLevel Queue handler */
    public void addQueue(Queue queue) {
        //System.out.println("Queue criada, prioridade = " + queue.getPriority());
        for (int i = 0; i < multiLevelQueue.size(); i++) {
            if (queue.getPriority() < multiLevelQueue.get(i).getPriority()) {
                multiLevelQueue.add(i, queue);
                return;
            }
        }
        multiLevelQueue.add(queue);
    }

    public void removeQueue(int index) {
        if (!multiLevelQueue.isEmpty() && index < multiLevelQueue.size())
            multiLevelQueue.remove(index);
    }

    /* Utilidades */
    public int getSize() {
        return multiLevelQueue.size();
    }

    public boolean isNotEmpty() {
        return multiLevelQueue.size() != 0;
    }
}
