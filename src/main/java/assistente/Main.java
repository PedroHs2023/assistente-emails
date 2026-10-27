package assistente;

import java.io.IOException;
import java.net.ConnectException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        try{
            OllamaClient cliente = new OllamaClient();


            System.out.println(cliente.perguntar("qual a capital do brasil"));

        }catch(ConnectException e){

            System.out.println("Nao consegui falar com o Ollama. Ele esta aberto?");

        }

        
    }
}