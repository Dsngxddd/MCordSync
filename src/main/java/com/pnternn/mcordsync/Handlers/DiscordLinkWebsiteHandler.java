package com.pnternn.mcordsync.Handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pnternn.mcordsync.MCordSync;
import com.pnternn.mcordsync.Managers.DiscordLinkManager;
import com.pnternn.mcordsync.Models.DiscordUserData;
import com.pnternn.mcordsync.Config.ConfigurationHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;

public class DiscordLinkWebsiteHandler implements HttpHandler{
    public DiscordLinkWebsiteHandler() {
        super();
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String dataMethod = exchange.getRequestMethod();
        if(dataMethod.equals("POST")) {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            JsonObject json = JsonParser.parseString(new Scanner(exchange.getRequestBody()).useDelimiter("\\A").next()).getAsJsonObject();
            String code = null;
            if(json.get("code") != null){
                code = json.get("code").getAsString();
            }
            String state = json.get("get").getAsString();
            if(state.equals("uuid")){
                if(DiscordLinkManager.getUUID(code) == null){
                    JsonObject response = new JsonObject();
                    response.addProperty("success", "false");
                    exchange.sendResponseHeaders(200, 0);
                    exchange.getResponseBody().write(response.toString().getBytes());
                    return;
                }
                UUID uuid = DiscordLinkManager.getUUID(code);
                JsonObject response = new JsonObject();
                response.addProperty("uuid", uuid.toString());
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().write(response.toString().getBytes());
            }else if(state.equals("uri")){
                JsonObject response = new JsonObject();
                response.addProperty("uri", "https://discord.com/oauth2/authorize?client_id="+ ConfigurationHandler.getValue("bot.id") +"&response_type=code&scope=identify&redirect_uri=http://"+ConfigurationHandler.getValue("bot.host")+":"+ConfigurationHandler.getValue("bot.port")+"/");
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().write(response.toString().getBytes());
            }else if(state.equals("discord_account")){
                JsonObject response = new JsonObject();
                response.addProperty("success", "true");
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().write(response.toString().getBytes());

                String accessCode = json.get("access_code").getAsString();

                URL url = new URL("https://discord.com/api/oauth2/token");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                String urlParameters = "client_id="+ConfigurationHandler.getValue("bot.id")+"&client_secret="+ConfigurationHandler.getValue("bot.secret")+"&grant_type=authorization_code&code=" + accessCode + "&redirect_uri=http://"+ConfigurationHandler.getValue("bot.host")+":"+ConfigurationHandler.getValue("bot.port")+"/";
                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                try (OutputStream os = con.getOutputStream()) {
                    os.write(postData);
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                String token = JsonParser.parseString(content.toString()).getAsJsonObject().get("access_token").getAsString();
                url = new URL("https://discord.com/api/users/@me");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "Bearer " + token);
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();

                JsonObject discordData = JsonParser.parseString(content.toString()).getAsJsonObject();
                String discordID = discordData.get("id").getAsString();
                String username = discordData.get("global_name").getAsString();
                String avatar = discordData.get("avatar").getAsString();
                DiscordLinkManager.addUserData(new DiscordUserData(DiscordLinkManager.getUUID(code), discordID, username, avatar), true);
                Bukkit.getPlayer(DiscordLinkManager.getUUID(code)).sendMessage("§3PirateSkyblock §7» §fDicord hesabınız bağlandı isminiz: §5" + username);
                DiscordLinkManager.giveDiscordRoles(discordID);
            }else if(state.equals("retrieve_discord_account")){
                if(DiscordLinkManager.getUUID(code) != null){
                    UUID playerUUID = DiscordLinkManager.getUUID(code);
                    JsonObject response = new JsonObject();
                    response.addProperty("name", Bukkit.getPlayer(playerUUID).getName());
                    response.addProperty("id",  DiscordLinkManager.getUserData(playerUUID).getDiscordID());
                    response.addProperty("avatar", DiscordLinkManager.getUserData(playerUUID).getAvatar());
                    response.addProperty("username", DiscordLinkManager.getUserData(playerUUID).getUsername());
                    exchange.sendResponseHeaders(200, 0);
                    exchange.getResponseBody().write(response.toString().getBytes());
                    DiscordLinkManager.removeCode(code);
                }else{
                    JsonObject response = new JsonObject();
                    response.addProperty("success", "false");
                    exchange.sendResponseHeaders(200, 0);
                    exchange.getResponseBody().write(response.toString().getBytes());
                }
                DiscordLinkManager.removeCode(code);
            }
        }else if (dataMethod.equals("GET")) {
            String url = exchange.getRequestURI().getPath();
            if(url.equals("/")){
                url = "/index.html";
            }
            exchange.getResponseHeaders().set("Content-Type", determineContentType(url));
            FileInputStream file = new FileInputStream(MCordSync.getInstance().getDataFolder().getAbsolutePath() + "/website" + url);
            exchange.sendResponseHeaders(200, 0);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(file.readAllBytes());
            responseBody.close();
        }
        exchange.close();
    }
    private String determineContentType(String fileName) {
        if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else {
            return "application/octet-stream";
        }
    }
}
