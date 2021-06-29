package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.languages.Lang;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.utils.TimeUtils;
import com.gmail.val59000mc.utils.UniversalSound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TimeBeforeSendBungeeThread implements Runnable{

	private final PlayerManager playerManager;
	private final UhcPlayer uhcPlayer;
	private int remainingTime;
	
	public TimeBeforeSendBungeeThread(PlayerManager playerManager, UhcPlayer uhcPlayer, int remainingTime){
		this.playerManager = playerManager;
		this.uhcPlayer = uhcPlayer;
		this.remainingTime = remainingTime;
	}

	@Override
	public void run() {
		remainingTime--;

		Player player;
		try {
			player = uhcPlayer.getPlayer();

			if(remainingTime <=10 || remainingTime%10 == 0){
				player.sendMessage(Lang.PLAYERS_SEND_BUNGEE.replace("%time%",TimeUtils.getFormattedTime(remainingTime)));
				playerManager.playsoundTo(uhcPlayer, UniversalSound.CLICK);
			}

			if(remainingTime <= 0){
				playerManager.sendPlayerToBungeeServer(player);
			}

		} catch (UhcPlayerNotOnlineException e) {
			// nothing to do for offline players
		}

		if(remainingTime > 0){
			Bukkit.getScheduler().runTaskLater(UhcCore.getPlugin(), this, 20);
		}
	}

}