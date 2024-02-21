package de.rollocraft.allminecraft.Discord.Manager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import javax.security.auth.login.LoginException;

public class DiscordBotManager {

    private JDA jda;
    private String channelId;

    public DiscordBotManager(String botToken, String channelId) throws LoginException, InterruptedException {
        this.jda = JDABuilder.createDefault(botToken).build().awaitReady();
        this.channelId = channelId;
    }

    public void start() {
        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        jda.getPresence().setActivity(Activity.playing("Server gestartet"));

        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            channel.getManager().setName("serverstatus-online").queue();
        }
    }

    public void stop() {
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            channel.getManager().setName("serverstatus-offline").queue();
        }
        jda.shutdown();
    }
}