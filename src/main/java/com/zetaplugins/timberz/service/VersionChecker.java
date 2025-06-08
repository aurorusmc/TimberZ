package com.zetaplugins.timberz.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetaplugins.timberz.TimberZ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * VersionChecker is a utility class that checks for updates of the TimberZ plugin
 * by querying the Modrinth API for the latest version compatible with the current Minecraft version.
 */
public final class VersionChecker {
    private final TimberZ plugin;
    private final Logger logger;
    private final String modrinthProjectId;
    private boolean newVersionAvailable = false;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public VersionChecker(TimberZ plugin, String modrinthProjectId) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.modrinthProjectId = modrinthProjectId;
        checkForUpdates();
    }

    private String getModrinthProjectUrl() {
        return "https://api.modrinth.com/v2/project/" + modrinthProjectId;
    }

    private void checkForUpdates() {
        String latestVersion = fetchLatestVersion();
        if (latestVersion != null) {
            String currentVersion = plugin.getDescription().getVersion();
            if (!latestVersion.trim().equals(currentVersion.trim())) {
                newVersionAvailable = true;

                final String reset = "\u001B[0m";
                final String bold = "\u001B[1m";
                final String darkGray = "\u001B[90m";
                final String lightGray = "\u001B[37m";
                final String green = "\u001B[32m";

                String message = "\n" +
                        darkGray + "==========================================" + reset + "\n" +
                        bold + "A new version of TimberZ is available!" + reset + "\n" +
                        bold + "New Version: " + reset + lightGray + currentVersion + " -> " + bold + green + latestVersion + reset + "\n" +
                        bold + "Download here: " + reset + lightGray + "https://modrinth.com/plugin/timberz/version/" + latestVersion + "\n" +
                        darkGray + "==========================================" + reset;

                logger.info(message);
            }
        }
    }

    private String fetchLatestVersion() {
        String mcVersion = plugin.getServer().getMinecraftVersion();
        String encodedGameVersion = URLEncoder.encode("[\"" + mcVersion + "\"]", StandardCharsets.UTF_8);
        String versionsUrl = getModrinthProjectUrl() + "/version?game_versions=" + encodedGameVersion;

        JsonNode versionsArray = fetchJsonArrayFromUrl(versionsUrl);
        if (versionsArray != null && versionsArray.isArray() && versionsArray.size() > 0) {
            JsonNode latestVersionNode = versionsArray.get(0);
            return latestVersionNode.get("version_number").asText();
        }
        return null;
    }

    private JsonNode fetchJsonFromUrl(String urlString) {
        try {
            HttpURLConnection connection = createHttpConnection(urlString);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String response = readResponse(connection);
                return OBJECT_MAPPER.readTree(response);
            } else {
                logger.warning("Failed to retrieve data from " + urlString + " Response code: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            logger.warning("Error fetching data: " + e.getMessage());
        }
        return null;
    }

    private JsonNode fetchJsonArrayFromUrl(String urlString) {
        return fetchJsonFromUrl(urlString);
    }

    private HttpURLConnection createHttpConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    public boolean isNewVersionAvailable() {
        return newVersionAvailable;
    }
}