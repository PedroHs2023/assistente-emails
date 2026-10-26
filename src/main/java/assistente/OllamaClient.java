package assistente;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class OllamaClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String perguntar(String pergunta) throws IOException, InterruptedException{

        JsonObject mensagem = new JsonObject();
        mensagem.addProperty("content", pergunta);
        mensagem.addProperty("role","user");

        JsonArray mensagens = new JsonArray();
        mensagens.add(mensagem);

        JsonObject corpo = new JsonObject();
        corpo.addProperty("model","qwen3:8b");
        corpo.addProperty("stream",false);
        corpo.addProperty("think",false);
        corpo.add("messages",mensagens);

        String json = new Gson().toJson(corpo);

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:11434/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String content = JsonParser.parseString(response.body())
                .getAsJsonObject()
                .getAsJsonObject("message")
                .get("content")
                .getAsString();

        return content;

    }

}
