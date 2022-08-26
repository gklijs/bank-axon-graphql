package tech.gklijs.legacy.api.bank;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TopicConfiguration {
    private static final String LEGACY_TOPIC = "legacy-bank-events";

    public String getTopic(){
        return LEGACY_TOPIC;
    }
}
