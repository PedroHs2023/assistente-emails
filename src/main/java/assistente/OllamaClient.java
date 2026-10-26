package assistente;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;


public class OllamaClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String perguntar(String pergunta) throws IOException, InterruptedException{

        String json = """
                {"model":"qwen3:8b","stream":false,"think":false,"messages":[{"role":"user","content":"%s"}]}
                """.formatted(pergunta);

        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:11434/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


        return response.body();

    }

}
