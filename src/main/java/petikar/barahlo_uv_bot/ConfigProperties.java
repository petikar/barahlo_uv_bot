package petikar.barahlo_uv_bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigProperties {

    @Value("${bot.historyId}")
    private String historyId;

    @Value("${bot.importantId}")
    private String importantId;

    public String getHistoryId() {
        return historyId;
    }

    public String getImportantId() {
        return importantId;
    }
}
