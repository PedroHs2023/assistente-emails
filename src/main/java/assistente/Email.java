package assistente;

import java.util.List;

public record Email(String id, String assunto, String texto, List<Link> linksFiltrados) {
}
