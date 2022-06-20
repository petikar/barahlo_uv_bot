package petikar.barahlo_uv_bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigProperties {

    @Value("${bot.historyId}")
    private String historyId;

    public String getHistoryId() {
        return historyId;
    }
}
