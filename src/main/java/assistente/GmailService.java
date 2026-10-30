package assistente;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;

public class GmailService {

    // ===== Constantes =====
    private static final String NOME_DO_APP = "Assistente de E-mails";
    private static final String ARQUIVO_CREDENCIAIS = "credentials.json";
    private static final String PASTA_TOKEN = "tokens";
    private static final List<String> ESCOPOS = List.of(GmailScopes.GMAIL_READONLY);
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final Gmail gmail;

    // ===== Construtor =====
    public GmailService() throws IOException, GeneralSecurityException {
        NetHttpTransport transporte = GoogleNetHttpTransport.newTrustedTransport();

        this.gmail = new Gmail.Builder(transporte, JSON_FACTORY, autorizar(transporte))
                .setApplicationName(NOME_DO_APP)
                .build();
    }

    // ===== Listar e-mails =====
    public List<Message> listarMensagens(int quantidade) throws IOException {
        return listarMensagens(null, quantidade);
    }

    public List<Message> listarMensagens(String busca, int quantidade) throws IOException {
        return gmail.users()
                .messages()
                .list("me")
                .setQ(busca)
                .setMaxResults((long) quantidade)
                .execute()
                .getMessages();
    }

    // ===== Cabeçalhos (assunto, remetente...) =====
    public String buscarAssunto(String id) throws IOException {
        return buscarCabecalho(id, "Subject");
    }

    public String buscarRemetente(String id) throws IOException {
        return buscarCabecalho(id, "From");
    }

    public String buscarCabecalho(String id, String nomeProcurado) throws IOException {
        Message mensagem = gmail.users()
                .messages()
                .get("me", id)
                .setFormat("metadata")
                .execute();

        for (MessagePartHeader cabecalho : mensagem.getPayload().getHeaders()) {
            String nome = cabecalho.getName();
            String valor = cabecalho.getValue();

            if (nome.equals(nomeProcurado)) {
                return valor;
            }
        }

        return "";
    }

    // ===== Conteúdo (HTML ou texto simples) =====
    public String buscarHtml(String id) throws IOException {
        Message mensagem = gmail.users()
                .messages()
                .get("me", id)
                .setFormat("full")
                .execute();

        return procurarParte(mensagem.getPayload(), "text/html");
    }

    public String buscarTexto(String id) throws IOException {
        Message mensagem = gmail.users()
                .messages()
                .get("me", id)
                .setFormat("full")
                .execute();

        return procurarParte(mensagem.getPayload(), "text/plain");
    }

    // ===== Ajudantes privados =====
    private String procurarParte(MessagePart parte, String tipo) {

        // Regra 1: esta parte é do tipo que eu procuro?
        if (parte.getMimeType().equals(tipo)) {
            return new String(parte.getBody().decodeData(), StandardCharsets.UTF_8);
        }

        // Regra 2: esta parte é uma pasta? procura nas filhas
        if (parte.getParts() != null) {
            for (MessagePart filha : parte.getParts()) {
                String resultado = procurarParte(filha, tipo);
                if (resultado != null) {
                    return resultado;
                }
            }
        }

        // Regra 3: não achei
        return null;
    }

    private GoogleClientSecrets carregarSegredos() throws IOException {
        try (FileReader leitor = new FileReader(ARQUIVO_CREDENCIAIS)) {
            return GoogleClientSecrets.load(JSON_FACTORY, leitor);
        }
    }

    private Credential autorizar(NetHttpTransport transporte) throws IOException {
        GoogleAuthorizationCodeFlow fluxo = new GoogleAuthorizationCodeFlow.Builder(
                transporte, JSON_FACTORY, carregarSegredos(), ESCOPOS)
                .setDataStoreFactory(new FileDataStoreFactory(new File(PASTA_TOKEN)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receptor = new LocalServerReceiver.Builder()
                .setPort(8888)
                .build();

        return new AuthorizationCodeInstalledApp(fluxo, receptor).authorize("user");
    }
}