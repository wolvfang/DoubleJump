package space.wolv.doublejump.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;
import space.wolv.doublejump.ActionBar.ActionBarAPI;

public class PlayerFlightListener implements Listener
{
	Plugin plugin;
	ActionBarAPI actionBar;
	
	public PlayerFlightListener(Plugin plugin){
		this.plugin = plugin;
		actionBar = new ActionBarAPI(plugin);
	}
	
	@EventHandler
	public void onFlight(PlayerToggleFlightEvent event)
	{
		Player player = event.getPlayer();
		
		if (player.hasPermission("doublejump.jump"))
		{
			if (!(player.getGameMode() == GameMode.SURVIVAL) || !(player.getWorld() == Bukkit.getServer().getWorld("world") || player.getWorld() == Bukkit.getWorld("world_nether") || player.getWorld() == Bukkit.getWorld("world_the_end")))
			{
				return;
			}
			
			event.setCancelled(true);
			event.getPlayer().setAllowFlight(false);
			event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(1.6d).setY(1.0d));
			
			actionBar.sendActionBar(player, ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("doublejump.message")));
		}
	}
}