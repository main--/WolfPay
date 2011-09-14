package me.main__.WolfPay;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTameEvent;

import com.iConomy.iConomy;
import com.iConomy.system.Holdings;

public class WolfPayEntityListener extends EntityListener {
	
	public static WolfPay plugin;
	
	@Override
	public void onEntityTame(EntityTameEvent event)
	{
		if ((!event.isCancelled()) && (event.getEntity() instanceof Wolf) && (event.getOwner() instanceof Player))
		{
			//since I've checked casting should now be safe
			Wolf wolf = (Wolf) event.getEntity();
			Player tamer = (Player) event.getOwner();
			
			//first: has he the permission?
			if (Util.permission(tamer, "wolfpay.tame"))
			{
				//next: is he inside the free-limit?
				List<Wolf> wolves = Util.getWolves(tamer);
				wolves.add(wolf); //since this event isn't fully processed, we need to add the wolf manually.
				int allowedwolves = WolfPay.freewolves;
				if (WolfPay.savebought)
				{
					Account account = 
							plugin.getDatabase().find(Account.class).where().ieq("playerName", tamer.getName()).findUnique();
					if (account != null)
					{
						allowedwolves += account.getBoughtwolves();
					}
				}
				if (wolves.size() <= (WolfPay.freewolves + allowedwolves))
				{
					//works. Now just display a message to him
					tamer.sendMessage(ChatColor.GREEN.toString() + "You have now x of y wolves."
							.replaceAll("x", String.valueOf(wolves.size()))
							.replaceAll("y", String.valueOf(allowedwolves)));
					if (wolves.size() == WolfPay.freewolves) //if he has reached the limit
						tamer.sendMessage(ChatColor.YELLOW.toString() + "For the next wolf you'll have to pay.");
					
					wolf.setOwner(tamer);
				}
				else
				{
					//then: check if he has enough money
					Holdings balance = iConomy.getAccount(tamer.getName()).getHoldings();
					if (balance.hasEnough(WolfPay.price))
					{
						//yay, let's go on!
						balance.subtract(WolfPay.price);
						tamer.sendMessage(ChatColor.GREEN + "You have successfully paid money and tamed a wolf!"
								.replaceAll("money", iConomy.format(WolfPay.price)));
						
						wolf.setOwner(tamer);
						
						if (WolfPay.savebought)
						{
							//we want to save this in the database
							Account account = 
									plugin.getDatabase().find(Account.class).where().ieq("playerName", tamer.getName()).findUnique();
							
							if (account == null)
							{
								account = new Account();
								account.setBoughtwolves(1);
								account.setPlayerName(tamer.getName());
							}
							else
							{
								//upgrade the account
								account.setBoughtwolves(account.getBoughtwolves() + 1);
							}
							
							plugin.getDatabase().save(account);
						}
					}
					else
					{
						tamer.sendMessage(ChatColor.RED.toString() + "Sorry, but you don't have enough money to do this.");
						event.setCancelled(true);
					}					
				}
			}
			else
			{
				tamer.sendMessage(ChatColor.RED.toString() + "Sorry, but you don't have the permission to do this.");
				event.setCancelled(true);
			}
		}
		//else: Either not a wolf tamed or not tamed by a player. ATM not possible but maybe in future updates.
		
	}
}
