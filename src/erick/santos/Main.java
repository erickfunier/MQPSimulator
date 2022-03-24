package erick.santos;

import erick.santos.controller.Controller;
import erick.santos.domain.MultiLevelQueue;
import erick.santos.port.Cli;

public class Main {
    public static void main(String[] args) {
        // Criando a multi level queue, iniciando a UI (CLI) e o controller
        MultiLevelQueue multiLevelQueue = new MultiLevelQueue();
        Cli cli = new Cli(); // usado para entrada e saida de dados para o usuario
        Controller controller = new Controller(cli, multiLevelQueue); // usado para executar as acoes do programa

        // Obtem os processos da input
        controller.processReceive();

        // Realiza a impressao da multilevel queue antes de iniciar o escalonamento e execucao
        cli.printMultiLevelQueue(multiLevelQueue);

        // Inicia o escalonamento e mostra os resultados
        if (multiLevelQueue.isNotEmpty()) {
            controller.processEscalation();
            controller.processResults();
        }
    }
}
