package assistente;

public class Config {

    public static final String BUSCA_VAGAS = "newer_than:2d (from:jobalerts-noreply@linkedin.com OR from:bebee.com OR from:glassdoor.com OR from:indeed.com OR from:jobsora.com OR from:catho.com.br OR from:infojobs.com.br)";

    public static final String BUSCA_NOTICIAS_TECNOLOGIA =
            "newer_than:2d (" +
                    "from:newsletter@filipedeschamps.com.br OR " +
                    "from:tech@newsletter.thedrops.com.br OR " +
                    "from:ai@newsletter.thedrops.com.br)";


    public static final String BUSCA_NOTICIAS_ECONOMIA =
            "newer_than:2d (" +
                    "from:moneydrop@mail.beehiiv.com OR " +
                    "from:thebizness@thenewscc.com.br OR " +
                    "from:money@jornal.thedrops.com.br)";

    public static final String BUSCA_NOTICIAS_GERAIS = "newer_than:2d from:created@thenewscc.com.br";
}
