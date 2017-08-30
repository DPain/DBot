package com.dpain.DiscordBot.enums;

public enum Property {
	// Enums of each properties for the bot.
	USERNAME("username"),
	BOT_ID("bot-id"),
	BOT_TOKEN("bot-token"),
	APP_ID("app-id"),
	OWNER_USER_ID("owner-user-id"),
	GUILD_ID("guild-id"),
	WEATHER_API_KEY("weather-api-key"),
	GAME_ROLE_FEATURE("game-role-feature"),
	USE_TWITCH_ALERTER("use-twitch-alerter"),
	GREET_GUILD_MEMBER("greet-guild-member");
	
	private String key;
	Property(String key) {
		this.key = key;
	}
	
	public String getKey() {
        return key;
    }
}
