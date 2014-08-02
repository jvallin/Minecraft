/**
* 
* PrivateProperty plugin Bukkit
* 
* @author Balckangel
* @version 1.2
* @date 01/01/2012
* @modification 02/08/2014
* 
* Principle : Permet la gestion de coffre et de four personnel
* Version de Bukkit : for MC 1.7.10
*
*/

package balckangel.PrivateProperty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class PrivateProperty extends JavaPlugin
{
	public PrivatePropertyListener listener = new PrivatePropertyListener();
	public static List<String> playerList = new ArrayList<String>(); /* Liste qui contient les joueurs utilisant le set */
    
	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/PrivateProperty/config.yml");
	
	public void onEnable() /* Actions exécutées au démarrage du plugin */
	{
		load();
    	if((Boolean) config.get("Configuration.Active"))
		{
			getServer().getPluginManager().registerEvents(listener, this);
		}
	}

	public void onDisable() /* Actions exécutées à la fermeture du plugin */
	{
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(!(Boolean) config.get("Configuration.Active") && !sender.getName().equals("CONSOLE")) /* si le plugin est désactivé et que ce n'est pas la console */
		{
			sender.sendMessage(config.getString("Configuration.Messages.Desactive"));
			return true;			
		}
	
		if(commandLabel.equalsIgnoreCase("pp")) /* Représente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
		{
			if (args.length == 1) /* Si il y a un argument */
			{
				if(sender.getName().equals("CONSOLE"))
				{
					if (args[0].equalsIgnoreCase("reload"))
					{
						load();
						sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Reload"));
						return true;
					}
					else if (args[0].equalsIgnoreCase("on"))					
					{
						config.set("Configuration.Active", true);
						sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Active"));
						saveYML();
						return true;
					}
					else if (args[0].equalsIgnoreCase("off"))
					{
						config.set("Configuration.Active", false);
						sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Desactive"));
						saveYML();
						return true;
					}
					else
					{
						sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
						return false;
					}
				}
				else
				{	
					Player player = null;
					
					try
					{
						player = (Player) sender;
					}
					catch (Exception e)
					{
						sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
						return false;
					}
						
					if(args[0].equalsIgnoreCase("set")) /* Si l'argument est "set" */
					{
						
						if(!(playerList.contains(player.getName()))) /* Si il est pas dans la liste */
						{
							playerList.add(player.getName());
						}
						
						player.sendMessage(config.getString("Configuration.Messages.Instruction"));
						return true;
					}
				}
			}		
		}
		
		sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
		return false;
	}
	
	/* Listener */
	public class PrivatePropertyListener implements Listener
	{
		boolean possible = false;
		BlockState pano = null;
		
		@EventHandler
		public void onPlayerInteract(PlayerInteractEvent event)
		{
			if((Boolean) config.get("Configuration.Active")) /* si le plugin est activé */
			{
				final Player player = event.getPlayer();
				final Action clic = event.getAction();
				String player_name = player.getName();
				Location loc;
				
				if(Action.RIGHT_CLICK_BLOCK == clic || Action.LEFT_CLICK_BLOCK == clic)
				{
					if(event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.FURNACE || event.getClickedBlock().getType() == Material.BURNING_FURNACE) /* si c'est un coffre ou un four */
					{
						loc = event.getClickedBlock().getLocation();
						loc.setY(loc.getY()+1);
						
						if(loc.getBlock().getType() == Material.WALL_SIGN || loc.getBlock().getType() == Material.SIGN_POST) /* Si il y a un panneau au dessus */
						{
							Sign panneau = (Sign) loc.getBlock().getState();
							String[] contenu = panneau.getLines();
							
							IsPossible(contenu, player_name);
								
							if(possible == false) /* Si pas possible */
							{
								event.setCancelled(true);
								
								if(event.getClickedBlock().getType() == Material.CHEST) /* Si c'est un coffre */
								{
									player.sendMessage(PrivateProperty.config.getString("Configuration.Messages.NotPermitChest"));
								}
								else if(event.getClickedBlock().getType() == Material.FURNACE || event.getClickedBlock().getType() == Material.BURNING_FURNACE)/* Si c'est un four */
								{
									player.sendMessage(PrivateProperty.config.getString("Configuration.Messages.NotPermitFurnace"));
								}
							}
						}
					}
					else if (event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST) /* si c'est un panneau */
					{
						Sign panneau = (Sign) event.getClickedBlock().getState();
						String[] contenu = panneau.getLines();
						
						IsPossible(contenu, player_name);
						
						if(Action.RIGHT_CLICK_BLOCK == clic) /* si le joueur clic droit */
						{
							if(PrivateProperty.playerList.contains(player_name)) /* Si il est dans la liste */
							{
								PrivateProperty.playerList.remove(player_name);
								
								if(possible == true) /* Si possible */
								{
									panneau.setLine(0, "["+player_name+"]"); /* set de la ligne 0 à faire */
									panneau.update(true); /* Pour reload le texte du panneau */
									player.sendMessage(PrivateProperty.config.getString("Configuration.Messages.Locked")+player_name);
								}
								else
								{
									player.sendMessage(PrivateProperty.config.getString("Configuration.Messages.NotLock"));
								}
							}
						}
						else if(Action.LEFT_CLICK_BLOCK == clic) /* Si il clique gauche */
						{	
								if(possible == false) /* Si pas possible */
								{
									event.setCancelled(true);
									player.sendMessage(PrivateProperty.config.getString("Configuration.Messages.NotPermitSign"));
								}
						}
					
					}
				}
				
				possible = false;
			}
		}
		
		@EventHandler
		public void onBlockBreak(BlockBreakEvent event)
		{
			if((Boolean) config.get("Configuration.Active")) /* si le plugin est activé */
			{
				final Player player = event.getPlayer();
				String player_name = player.getName();
				Location loc = event.getBlock().getLocation();
				
				//if(event.getBlock().getType() == Material.CHEST || event.getBlock().getType() == Material.FURNACE || event.getBlock().getType() == Material.BURNING_FURNACE) /* si c'est un coffre ou un four */
				//{
				if(IsPan(loc))
				{
					if(pano != null)
					{
						Sign panneau = (Sign) pano;
						String[] contenu = panneau.getLines();
						
						IsPossible(contenu, player_name);
							
						if(possible == false) /* Si pas possible */
						{
							event.setCancelled(true);
							player.sendMessage(PrivateProperty.config.getString("Configuration.Messages.NotPermitBreak"));
						}
					}
				//}
				}
				pano = null;
				possible = false;
			}
		}
		
		
		public void IsPossible(String[] contenu, String nom)
		{
			if(contenu[0].isEmpty()) /* Si c'est vide */
			{
				possible = true;
			}
			else
			{
				if(contenu[0].startsWith("[")) /* Si commence par [ */
				{
					if(!(contenu[0].endsWith("]"))) /* Si ne fini pas par ] */
					{
						possible = true;
					}
					else /* si fini par ] */
					{
						if(contenu[0].equalsIgnoreCase("["+nom+"]")) /* Si c'est son nom */
						{
							possible = true;
						}
					}
				}
				else /* si ne commence pas par [ */
				{
					possible = true;
				}
			}
		}
		
		public Boolean IsPan(Location position)
		{
			Location temp = position.clone();
			temp.setY(temp.getY()+1);
			if(temp.getBlock().getType() == Material.SIGN_POST)
			{
				pano = temp.getBlock().getState();
				return true;
			}
			temp.setY(temp.getY()-1);
			temp.setX(temp.getX()-1);
			if(temp.getBlock().getType() == Material.WALL_SIGN)
			{
				pano = temp.getBlock().getState();
				return true;
			}
			temp.setX(temp.getX()+2);
			if(temp.getBlock().getType() == Material.WALL_SIGN)
			{
				pano = temp.getBlock().getState();
				return true;
			}
			temp.setX(temp.getX()-1);
			temp.setZ(temp.getZ()-1);
			if(temp.getBlock().getType() == Material.WALL_SIGN)
			{
				pano = temp.getBlock().getState();
				return true;
			}
			temp.setZ(temp.getZ()+2);
			if(temp.getBlock().getType() == Material.WALL_SIGN)
			{
				pano = temp.getBlock().getState();
				return true;
			}		
			return false;
		}
	}
	
	/* Fichier YML */
	public void load()
	{
		if(configFile.exists()) /* Lecture du fichier */
		{
			config = YamlConfiguration.loadConfiguration(configFile);
		}
		else /* Création du fichier */
		{	
			config = new YamlConfiguration();
			config.createSection("Configuration.Active");
			
			config.createSection("Configuration.Messages.Active"); /* Lorsque l'on active le plugin */
			config.createSection("Configuration.Messages.Desactive"); /* Lorsque l'on désactive le plugin */
			config.createSection("Configuration.Messages.Reload"); /* Lorsque le plugin est reload */
			config.createSection("Configuration.Messages.Permit"); /* Si l'utilisateur n'a pas le droit utiliser une commande */
			config.createSection("Configuration.Messages.Instruction"); /* Instruction */
			config.createSection("Configuration.Messages.Locked"); /* Lorsqu'un joueur verrouille un panneau */
			config.createSection("Configuration.Messages.NotLock"); /* Si il ne peux pas verrouiller */
			config.createSection("Configuration.Messages.NotPermitChest"); /* Si ce n'est pas son coffre */
			config.createSection("Configuration.Messages.NotPermitFurnace"); /* Si ce n'est pas son four */
			config.createSection("Configuration.Messages.NotPermitSign"); /* Si ce n'est pas son panneau */
			config.createSection("Configuration.Messages.NotPermitBreak"); /* Si il y a un panneau derriére qui n'est pas é lui */
			
			
			config.set("Configuration.Active", true);
			
			config.set("Configuration.Messages.Active", "Plugin PrivateProperty active");
			config.set("Configuration.Messages.Desactive", "Plugin PrivateProperty desactive");
			config.set("Configuration.Messages.Reload", "Plugin PrivateProperty reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.Instruction", "Cliquez droit sur un panneau pour le verrouiller");
			config.set("Configuration.Messages.Locked", "Panneau verrouille ");
			config.set("Configuration.Messages.NotLock", "Vous ne pouvez verrouiller ce panneau");
			config.set("Configuration.Messages.NotPermitChest", "Ce n'est pas votre coffre");
			config.set("Configuration.Messages.NotPermitFurnace", "Ce n'est pas votre four");
			config.set("Configuration.Messages.NotPermitSign", "Ce n'est pas votre panneau");
			config.set("Configuration.Messages.NotPermitBreak", "Un paneau qui ne vous appartient pas est présent sur ce bloc");
			
			saveYML();
			config = YamlConfiguration.loadConfiguration(configFile);
			System.out.println("Le fichier de configuration de PrivateProperty a ete cree.");
		}    
    }
	
	public void saveYML()
	{
		try
		{
			config.save(configFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}