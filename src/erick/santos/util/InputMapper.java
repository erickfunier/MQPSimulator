package erick.santos.util;

import erick.santos.domain.ProcessObj;

public class InputMapper {
    // Verifica se a entrada eh valida com o validador e entao mapeia em um objeto Processo
    public static ProcessObj getProcessFromInput (String input) {
        if (InputValidator.validateInput(input)) {
            String[] inputData = input.split(",");
            String name = inputData[0];
            int priority = Integer.parseInt(inputData[1].replace(" ", ""));
            long cpuBurst = Integer.parseInt(inputData[2].replace(" ", ""));

            return new ProcessObj(name, priority, cpuBurst, "Stopped");
        } else {
            return null;
        }
    }
}
