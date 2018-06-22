package com.dpain.DiscordBot.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.dpain.DiscordBot.enums.Group;
import com.dpain.DiscordBot.enums.Property;
import com.dpain.DiscordBot.helper.LogHelper;
import com.dpain.DiscordBot.listener.twitch.TwitchAlerter;
import com.dpain.DiscordBot.system.MemberManager;
import com.dpain.DiscordBot.system.PropertiesManager;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;

public class UserEventListener implements net.dv8tion.jda.core.hooks.EventListener {
	private final static Logger logger = Logger.getLogger(UserEventListener.class.getName());
	
	private static TwitchAlerter alerter;
	private static Guild guild;
	
	public UserEventListener() {
		logger.log(Level.INFO, "Listening started!");
	}
	
    @Override
    public void onEvent(Event event) {
        if(event instanceof GuildMemberJoinEvent) {
        	GuildMemberJoinEvent castedEvent = (GuildMemberJoinEvent) event;
        	
        	MemberManager.load().addMember(castedEvent.getMember());
        	
        	if(PropertiesManager.load().getValue(Property.GREET_GUILD_MEMBER).equals("true")) {
        		castedEvent.getGuild().getDefaultChannel().sendMessage("Hi, " + castedEvent.getUser().getName() + "!\nWelcome to the Discord Server!").queue();
        	}
        	logger.log(Level.INFO, LogHelper.elog(castedEvent, "User joined!"));
        } else if(event instanceof GuildBanEvent) {
        	GuildBanEvent castedEvent = (GuildBanEvent) event;
        	
        	MemberManager.load().changeMemberGroup(guild.getMember(castedEvent.getUser()), Group.PRISONER);
        	
        	logger.log(Level.INFO, LogHelper.elog(castedEvent, "User is banned!"));
        } else if(event instanceof GuildMemberLeaveEvent) {
        	GuildMemberLeaveEvent castedEvent = (GuildMemberLeaveEvent) event;
        	
        	logger.log(Level.INFO, LogHelper.elog(castedEvent, "User left!"));
        } else if(event instanceof GuildUnbanEvent) {
        	GuildUnbanEvent castedEvent = (GuildUnbanEvent) event;
        	
        	logger.log(Level.INFO, LogHelper.elog(castedEvent, "User is unbanned!"));
        } else if(event instanceof UserUpdateGameEvent) {
        	UserUpdateGameEvent castedEvent = (UserUpdateGameEvent) event;
        	if(castedEvent.getGuild().getMember(castedEvent.getUser()).getGame() != null) {
        		String temp = castedEvent.getGuild().getMember(castedEvent.getUser()).getGame().getUrl();
        		if(temp != null && Game.isValidStreamingUrl(temp)) {
    				if(isTrustedTwitchStreamer(guild.getMember(castedEvent.getUser())) && !castedEvent.getUser().getId().equals(event.getJDA().getSelfUser().getId())) {
    					if(PropertiesManager.load().getValue(Property.USE_TWITCH_ALERTER).equals("true")) {
    						alerter.notifyTwitchStream(guild.getMember(castedEvent.getUser()));
    					}
    					logger.log(Level.INFO, LogHelper.elog(castedEvent, "User is streaming in Twitch.tv."));
    				}
    			}
        	}
		}
    }
    
    private boolean isTrustedTwitchStreamer(Member member) {
    	return MemberManager.load().getMemberGroup(member).getHierarchy() <= Group.TRUSTED_USER.getHierarchy();
    }
    
    public static void setDefaultGuild(Guild guild) {
    	UserEventListener.guild = guild;
    	instantiateTwitchAlerter();
	}
    
    public static void instantiateTwitchAlerter() {
    	alerter = new TwitchAlerter(guild);
    }
}