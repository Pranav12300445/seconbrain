package com.pranav.second_brain_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "supabase")
public class SupabaseProperties {

    private String url;
    private String serviceKey;
    private Storage storage = new Storage();

    @Data
    public static class Storage {
        private String bucket;
    }
}