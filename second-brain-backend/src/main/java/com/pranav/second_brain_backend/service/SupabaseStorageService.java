package com.pranav.second_brain_backend.service;

import com.pranav.second_brain_backend.config.SupabaseProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupabaseStorageService {

    private final SupabaseProperties supabaseProps;
    private final WebClient.Builder webClientBuilder;

    /**
     * Uploads a file to Supabase Storage and returns its public URL.
     *
     * @param file   the multipart file from the HTTP request
     * @param userId used as folder prefix inside the bucket
     * @return public URL of the uploaded file
     */
    public String uploadFile(MultipartFile file, Long userId) throws IOException {
        String bucket = supabaseProps.getStorage().getBucket();
        String uniqueFileName = UUID.randomUUID() + "-" + sanitize(file.getOriginalFilename());
        String storagePath = userId + "/" + uniqueFileName;

        byte[] bytes = file.getBytes();
        String contentType = file.getContentType() != null
                ? file.getContentType()
                : "application/octet-stream";

        WebClient client = webClientBuilder
                .baseUrl(supabaseProps.getUrl())
                .defaultHeader("Authorization", "Bearer " + supabaseProps.getServiceKey())
                .defaultHeader("apikey", supabaseProps.getServiceKey())
                .build();

        client.post()
                .uri("/storage/v1/object/" + bucket + "/" + storagePath)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(BodyInserters.fromValue(bytes))
                .retrieve()
                .toBodilessEntity()
                .block();

        // Public URL format for public buckets
        String publicUrl = supabaseProps.getUrl()
                + "/storage/v1/object/public/" + bucket + "/" + storagePath;

        log.info("Uploaded file to Supabase Storage: {}", publicUrl);
        return publicUrl;
    }

    /**
     * Returns the storage path portion from a full public URL.
     * Needed to call delete.
     */
    public String extractStoragePath(String publicUrl) {
        String bucket = supabaseProps.getStorage().getBucket();
        String marker = "/object/public/" + bucket + "/";
        int idx = publicUrl.indexOf(marker);
        return idx >= 0 ? publicUrl.substring(idx + marker.length()) : publicUrl;
    }

    /**
     * Deletes a file from Supabase Storage.
     *
     * @param storagePath e.g. "42/uuid-report.pdf"
     */
    public void deleteFile(String storagePath) {
        String bucket = supabaseProps.getStorage().getBucket();

        WebClient client = webClientBuilder
                .baseUrl(supabaseProps.getUrl())
                .defaultHeader("Authorization", "Bearer " + supabaseProps.getServiceKey())
                .defaultHeader("apikey", supabaseProps.getServiceKey())
                .build();

        // Supabase delete expects a JSON body: { "prefixes": ["path"] }
        String body = "{\"prefixes\":[\"" + storagePath + "\"]}";

        client.method(HttpMethod.DELETE)
                .uri("/storage/v1/object/" + bucket + "/" + storagePath)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();

        log.info("Deleted file from Supabase Storage: {}", storagePath);
    }

    private String sanitize(String name) {
        if (name == null)
            return "file";
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}