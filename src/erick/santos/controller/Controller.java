package erick.santos.controller;

import erick.santos.domain.MultiLevelQueue;
import erick.santos.domain.ProcessObj;
import erick.santos.domain.Queue;
import erick.santos.port.Cli;
import erick.santos.util.InputMapper;

public class Controller {
    private final Cli cli;

    private final MultiLevelQueue multiLevelQueue;

    public Controller(Cli cli, MultiLevelQueue multiLevelQueue) {
        this.cli = cli;
        this.multiLevelQueue = multiLevelQueue;
    }

    /* Etapas de execucao start */
    // 1 - receber os processos (processReceive)
    // 2 - escalonamento de processos (processEscalation)
    // 3 - mostrar os resultados (processResults)
    public void processReceive() {
        String input = cli.getInput();

        while (input != null && !input.isBlank()) {

            ProcessObj processObj = InputMapper.getProcessFromInput(input);
            if (processObj != null) {
                addProcess(processObj);

            } else {
                cli.printMessage("Entrada invalida!\nUtilize o seguinte exemplo: Firefox, 123, 1233");
            }
            input = cli.getInput();
        }

    }

    public void processEscalation() {
        long createdTime = System.currentTimeMillis(); // usado como createdTime para todos os processos

        cli.printMessage("\nEscalonamento em execução:");
        cli.printProcessStatusHeader(); // Realiza a impressao de um header para a tabela de execucao dos processos

        /*
            A variavel @index eh usada para identificar o index do processo que deve ser executado
            o index da queue em execucao sera sempre o 0, pois eh a queue de maior prioridade, porem
            o processo de maior prioridade pode ter sido executado entao esse index tem que ser
            atualizado em tempo de execucao

            A variavel @looping em conjunto com o @watchDogCounter eh utilizada para parar a execucao
            do programa pois identifica que os processos serao apenas comutados e nao eh necessario
            continuar executando, apenas para nao precisar encerrar o programa manualmente
         */

        int index = 0;
        boolean looping = false; // usado para parar a execucao do programa
        int watchDogCounter = multiLevelQueue.getQueueByIndex(0).getSize();

        while (!looping) {
            //cli.printMultiLevelQueue(multiLevelQueue);

            long burstTime = multiLevelQueue.getQueueByIndex(0).getProcess(index).getCpuBurst();
            int queueSize = multiLevelQueue.getQueueByIndex(0).getSize(); // necessario para validar se a queue teve alguma modificacao
            int ageCounter = 10; // usado para realizar o aging

            multiLevelQueue.getQueueByIndex(0).getProcess(index).setCreatedTime(createdTime);
            multiLevelQueue.getQueueByIndex(0).getProcess(index).setStarTime(System.currentTimeMillis());
            multiLevelQueue.getQueueByIndex(0).getProcess(index).setStatus("Running");

            cli.printProcessStatus(multiLevelQueue.getQueueByIndex(0).getProcess(index));

            // salvar os dados do processo em execucao, necessario para atualizar uma vez que a queue pode ser modificada no aging
            String name = multiLevelQueue.getQueueByIndex(0).getProcess(index).getName();
            long cpuBurst = multiLevelQueue.getQueueByIndex(0).getProcess(index).getCpuBurst();

            // Burst Loop
            while (burstTime > 0) {
                runProcessSimulation();
                burstTime--;

                // Aging handler
                if (ageCounter == 0) {
                    aging();
                    ageCounter = 10;
                }
                ageCounter--;
            }

            int[] lastIndexes = updateProcessStatus(name, cpuBurst, "Stopped"); // retorna o index exato do processo para utilizar na impressao abaixo
            cli.printProcessStatus(multiLevelQueue.getQueueByIndex(lastIndexes[0]).getProcess(lastIndexes[1]));

            boolean changed = multiLevelQueue.getQueueByIndex(0).getSize() > queueSize; // verifica se houve alteracao na queue
            //printMultiLevelQueue();

            index = getHighPriorityIndex(cpuBurst, changed); // obter o processo com maior prioridade (lista mais prioritaria, processo mais prioritario)

            // Atualiza o watchDogCounter de acordo com o status da queue e dos processos executados, garantindo que todos serao executados uma vez
            // antes de encerrar
            if (!changed) {
                watchDogCounter--;
                if (watchDogCounter == 0)
                    looping = true;
            } else {
                watchDogCounter = multiLevelQueue.getQueueByIndex(0).getSize();
            }
        }
        cli.printMessage("Escalonamento finalizado\n");
    }

    public void processResults() {
        cli.printMessage("\nResultados do escalonamento:");
        cli.printProcessStatusHeader(); // Realiza a impressao de um header para a tabela de execucao dos processos
        cli.printAllProcessStatus(multiLevelQueue); // Realiza a impressao do status de todos os processos
        cli.printMessage("\nMultilevel Queue escalonada:\n");
        cli.printMultiLevelQueue(multiLevelQueue);
    }
    /* Etapas de execucao start */

    /* Handling processes start*/
    private void addProcess(ProcessObj processObj) {
        // Verifica se a queue ja existe
        if (multiLevelQueue.getQueueByPriority(processObj.getPriority()) == null) {
            Queue queueLevel = new Queue(processObj.getPriority());
            multiLevelQueue.addQueue(queueLevel);

            //cli.printMessage("Queue criada, prioridade = " + processObj.getPriority() + "\n");
        }
        multiLevelQueue.getQueueByPriority(processObj.getPriority()).addProcess(processObj);
        //cli.printMessage("Processo adicionado " + processObj.getName() + "\n");
    }

    public int[] updateProcessStatus(String name, long cpuBurst, String status) {
        // Percorrer todas as queues para encontrar o processo a ser atualizado, por fim, retorna sua posicao
        for (int i = 0; i < multiLevelQueue.getSize(); i++) {
            for (int j = 0; j < multiLevelQueue.getQueueByIndex(i).getSize(); j++) {
                if (multiLevelQueue.getQueueByIndex(i).getProcess(j).getName().equals(name) &&
                        multiLevelQueue.getQueueByIndex(i).getProcess(j).getCpuBurst() == cpuBurst) {
                    multiLevelQueue.getQueueByIndex(i).getProcess(j).setStatus(status);
                    return new int[]{i, j};
                }
            }
        }
        return new int[0];
    }

    public ProcessObj getLowestPriorityProcess() {
        // Obtem o processo da ultima queue (de menor prioridade) e da ultima posicao (com maior burst)
        Queue queue = multiLevelQueue.getQueueByIndex(multiLevelQueue.getSize() - 1);
        if (queue.getPriority() > 0) {
            ProcessObj processObj = queue.getProcess(queue.getSize() - 1); // obtem o processo

            // remove o processo da queue atual
            multiLevelQueue.getQueueByIndex(multiLevelQueue.getSize() - 1).removeProcess(queue.getSize() - 1);

            // se queue ficar vazia, remove a queue
            if (multiLevelQueue.getQueueByIndex(multiLevelQueue.getSize() - 1).isEmpty())
                multiLevelQueue.removeQueue(multiLevelQueue.getSize() - 1);

            return processObj;
        }
        return null;
    }

    public int getHighPriorityIndex(long lastBurst, boolean changed) {
        /*
            Obtem o index do processo seguinte, se a fila foi modificada (variavel @chanegd) e foi inserido um processo
            de maior prioridade esse sera selecionado, caso contrario, obtem o processo com o proximo burstm maior que
            o atual, e se nao encontrar retorna para o comeco da queue
         */
        if (changed) {
            for (int i = 0; i < multiLevelQueue.getQueueByIndex(0).getSize(); i++) {
                if (multiLevelQueue.getQueueByIndex(0).getProcess(i).getCpuBurst() < lastBurst) {
                    return i;
                }
            }
        }
        for (int i = 0; i < multiLevelQueue.getQueueByIndex(0).getSize(); i++) {
            if (multiLevelQueue.getQueueByIndex(0).getProcess(i).getCpuBurst() > lastBurst) {
                return i;
            }
        }
        return 0;
    }
    /* Handling processes end*/

    /* Funcoes uteis para o escalonamento start*/
    public void aging() {
        // obtem o ultimo processo da queue de menor prioridade e aumenta sua prioridade
        ProcessObj processObj = getLowestPriorityProcess();

        if (processObj != null) {
            processObj.setPriority(processObj.getPriority() - 1);

            addProcess(processObj); // adiciona o processo na queue da sua prioridade
        }
    }

    private void runProcessSimulation() {
        /*
            Coloca o thread principal em sleep, o tempo em ms pode variar pra mais dependendo da carga do sistema,
            portanto nao ha precisao do valor de 10 ms, apenas que sera de no minimo 10 ms
         */
        try {
            Thread.sleep(10);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
    /* Funcoes uteis para o escalonamento start*/
}
