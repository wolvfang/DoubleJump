package space.wolv.doublejump.ActionBar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import space.wolv.doublejump.DoubleJump;

public class ActionBarAPI
{
    static boolean works = true;
    Plugin plugin;
    
    public ActionBarAPI(Plugin plugin)
    {
    	this.plugin = plugin;
    }
	
	public void sendActionBar(Player player, String message)
	{
		// Call the event, if cancelled don't send Action Bar
		ActionBarMessageEvent actionBarMessageEvent = new ActionBarMessageEvent(player, message);
		Bukkit.getPluginManager().callEvent(actionBarMessageEvent);
		if (actionBarMessageEvent.isCancelled())
		{
			return;
		}

		try
		{
			Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + DoubleJump.nmsver + ".entity.CraftPlayer");
			Object p = c1.cast(player);
			Object ppoc;
			Class<?> c4 = Class.forName("net.minecraft.server." + DoubleJump.nmsver + ".PacketPlayOutChat");
			Class<?> c5 = Class.forName("net.minecraft.server." + DoubleJump.nmsver + ".Packet");
			if ((DoubleJump.nmsver.equalsIgnoreCase("v1_8_R1") || !DoubleJump.nmsver.startsWith("v1_8_")) && !DoubleJump.nmsver.startsWith("v1_9_")) 
			{
				Class<?> c2 = Class.forName("net.minecraft.server." + DoubleJump.nmsver + ".ChatSerializer");
				Class<?> c3 = Class.forName("net.minecraft.server." + DoubleJump.nmsver + ".IChatBaseComponent");
				Method m3 = c2.getDeclaredMethod("a", String.class);
				Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
				ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(cbc, (byte) 2);
			} 
			else 
			{
				Class<?> c2 = Class.forName("net.minecraft.server." + DoubleJump.nmsver + ".ChatComponentText");
				Class<?> c3 = Class.forName("net.minecraft.server." + DoubleJump.nmsver + ".IChatBaseComponent");
				Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
				ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);
			}
			Method m1 = c1.getDeclaredMethod("getHandle");
			Object h = m1.invoke(p);
			Field f1 = h.getClass().getDeclaredField("playerConnection");
			Object pc = f1.get(h);
			Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
			m5.invoke(pc, ppoc);
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
			works = false;
		}
	}

	public void sendActionBar(final Player player, final String message, int duration) 
	{
		sendActionBar(player, message);

		if (duration >= 0)
		{
			// Sends empty message at the end of the duration. Allows messages shorter than 3 seconds, ensures precision.
			new BukkitRunnable() 
			{
				@Override
				public void run() 
				{
					sendActionBar(player, "");
				}
			}.runTaskLater(plugin, duration + 1);
		}

		// Re-sends the messages every 3 seconds so it doesn't go away from the player's screen.
		while (duration > 60) {
			duration -= 60;
			int sched = duration % 60;
			new BukkitRunnable() {
				@Override
				public void run() {
					sendActionBar(player, message);
				}
			}.runTaskLater(plugin, (long) sched);
		}
	}

	public void sendActionBarToAllPlayers(String message) {
		sendActionBarToAllPlayers(message, -1);
	}

	public void sendActionBarToAllPlayers(String message, int duration) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendActionBar(p, message, duration);
		}
	}
}