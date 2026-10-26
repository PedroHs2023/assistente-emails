package assistente;

import java.io.IOException;
import java.net.ConnectException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        try{

            System.out.println(new OllamaClient().perguntar("o que significa \"API\"?"));

        }catch(ConnectException e){

            System.out.println("Nao consegui falar com o Ollama. Ele esta aberto?");

        }

        
    }
}