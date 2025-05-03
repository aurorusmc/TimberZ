package com.zetaplugins.timberz.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetaplugins.timberz.TimberZ;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public final class VersionChecker {
    private static final String MODRINTH_SLUG = "timberz";
    private static final String MODRINTH_ID = "CHANGETHIS";
    private static final String MODRINTH_PROJECT_URL = "https://api.modrinth.com/v2/project/" + MODRINTH_ID;

    private final TimberZ plugin;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean NEW_VERSION_AVAILABLE = false;

    public VersionChecker(TimberZ plugin) {
        this.plugin = plugin;
        checkForUpdates();
    }

    public void checkForUpdates() {
        String latestVersion = fetchLatestVersionFromModrinth();
        if (latestVersion == null) return;

        String currentVersion = plugin.getDescription().getVersion();
        if (!latestVersion.trim().equals(currentVersion.trim())) {
            NEW_VERSION_AVAILABLE = true;
            plugin.getLogger().info("A new version of TimberZ is available! Version: " + latestVersion +
                    "\nDownload the latest version here: https://modrinth.com/plugin/" + MODRINTH_SLUG + "/versions");
        }
    }

    public String fetchLatestVersionFromModrinth() {
        try {
            JsonNode projectJson = getJsonFromUrl(MODRINTH_PROJECT_URL);
            if (projectJson == null) return null;

            JsonNode versions = projectJson.get("versions");
            if (versions == null || !versions.isArray() || versions.size() == 0) {
                plugin.getLogger().warning("No versions found in Modrinth project data.");
                return null;
            }

            String latestVersionId = versions.get(versions.size() - 1).asText();
            return fetchVersionNumberById(latestVersionId);

        } catch (IOException e) {
            plugin.getLogger().warning("Error fetching latest version from Modrinth: " + e.getMessage());
        }
        return null;
    }

    private String fetchVersionNumberById(String versionId) throws IOException {
        String versionUrl = MODRINTH_PROJECT_URL + "/version/" + versionId;
        JsonNode versionJson = getJsonFromUrl(versionUrl);

        if (versionJson != null && versionJson.has("version_number")) {
            return versionJson.get("version_number").asText();
        }

        plugin.getLogger().warning("Failed to retrieve version_number for version ID: " + versionId);
        return null;
    }

    private JsonNode getJsonFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return objectMapper.readTree(connection.getInputStream());
            } else {
                plugin.getLogger().warning("HTTP request failed. URL: " + urlString +
                        ", Response code: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read from URL: " + urlString + ", Error: " + e.getMessage());
        }
        return null;
    }
}
