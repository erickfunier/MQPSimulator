package erick.santos.port;

import erick.santos.domain.MultiLevelQueue;
import erick.santos.domain.ProcessObj;

import java.util.Scanner;

public class Cli {
    private final Scanner input;

    public Cli() {
        input = new Scanner(System.in);
    }

    /* Input */
    public String getInput() {
        return input.nextLine();
    }

    /* Prints Output */
    public void printMessage(String message) {
        System.out.print(message);
    }

    public void printProcessStatusHeader() {
        String message = "\tNome\t|\tstatus\t|\tcreated time\t|\tstarted time\t|\twaiting time (ms)\t\n";
        printMessage(message);
    }

    public void printProcessStatus(ProcessObj processObj) {
        String message = "\t" + processObj.getName() +
                "\t|\t" + processObj.getStatus() +
                "\t|\t" + processObj.getCreatedTime() +
                "\t|\t" + processObj.getStarTime() +
                "\t|\t" + (processObj.getStarTime() - processObj.getCreatedTime()) + "\n";
        printMessage(message);
    }

    public void printMultiLevelQueue(MultiLevelQueue multiLevelQueue) {
        if (multiLevelQueue.isNotEmpty()) {
            for (int i = 0; i < multiLevelQueue.getSize(); i++) {
                printMessage("Lista " + i + " prioridade = " + multiLevelQueue.getQueueByIndex(i).getPriority() + "\n");
                for (int j = 0; j < multiLevelQueue.getQueueByIndex(i).getSize(); j++) {
                    printMessage("Process name: " + multiLevelQueue.getQueueByIndex(i).getProcess(j).getName() + "\n");
                }
            }
        } else {
            printMessage("Vazio\n");
        }
    }
}