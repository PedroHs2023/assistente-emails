package assistente;

import java.io.IOException;
import java.net.ConnectException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        try {
            OllamaClient cliente = new OllamaClient();

            String sistemaJson = """
                    Responda apenas JSON no formato: {"itens":[{"nome":"","ano":0}]}
                    """;
            String pergunta = "liste 3 linguagens de programação e o ano em que foram criadas";

            String resposta = cliente.perguntar(sistemaJson, pergunta, true);

            // 1. transforma o texto em árvore e pega a lista "itens"
            JsonArray itens = JsonParser.parseString(resposta)
                    .getAsJsonObject()
                    .getAsJsonArray("itens");

            // 2. para cada item da lista...
            for (JsonElement item : itens) {
                JsonObject linguagem = item.getAsJsonObject();     // trata o item como objeto { }
                String nome = linguagem.get("nome").getAsString(); // texto
                int ano = linguagem.get("ano").getAsInt();         // número
                System.out.println(nome + " - " + ano);
            }

        } catch (ConnectException e) {
            System.out.println("Não consegui falar com o Ollama. Ele está aberto?");
        }
    }
}