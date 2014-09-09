/**
* 
* EditSign plugin Bukkit
* 
* @author Balckangel
* @version 1.3
* @date 08/01/2012
* @modification 09/09/0214
* 
* Principle : Permet de modifier le texte d'un panneau
* Version de Bukkit : for MC 1.7.10
*
*/

package balckangel.EditSign;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class EditSign extends JavaPlugin
{
	public static Map<String, Message> collection = new HashMap<String, Message>();
	public EditSignListener listener = new EditSignListener();
    
	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/EditSign/config.yml");
	
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
		collection.clear();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(!(Boolean) config.get("Configuration.Active") && !sender.getName().equals("CONSOLE")) /* si le plugin est désactivé et que ce n'est pas la console */
		{
			sender.sendMessage(config.getString("Configuration.Messages.Desactive"));
			return true;			
		}
	
		if(commandLabel.equalsIgnoreCase("es")) /* Représente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
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
			}
			
			if (args.length >= 1) /* Si il y a un ou plus d'un argument */
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
				
				String nom = player.getName(); /* nom du joueur */
				int entier;
				String texte = "";
				
				try
				{
					entier = Integer.parseInt(args[0]);
				}
				catch (NumberFormatException e)
				{
					player.sendMessage(config.getString("Configuration.Messages.Nombre"));
					return false;
				}
				
				if(entier > 0 && entier < 5) /* Si le premier argument est un nombre compris entre 1 et 4 */
				{
					if(collection.containsKey(nom)) /* Si le joueur est déjà dans la liste */
					{
						collection.remove(nom);
					}

					for(int i=1; i < args.length; i++)
					{
						texte += args[i];
						if(i+1 != args.length)
						{
							texte += " "; 
						}
					}
					
					Message content = new Message(entier, texte);
					
					collection.put(nom, content);
					
					player.sendMessage(config.getString("Configuration.Messages.Instruction"));
					return true;
				}
				else
				{
					player.sendMessage(config.getString("Configuration.Messages.Nombre"));
					return false;
				}
			}
		}

		sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
		return false;
	}
	
	/* Listener */
	public class EditSignListener implements Listener
	{
		@EventHandler
		public void onPlayerInteract(PlayerInteractEvent event)
		{
			if((Boolean) config.get("Configuration.Active")) /* si le plugin est activé */
			{
				final Player player = event.getPlayer();
				final Action clic = event.getAction();
				String player_name = player.getName();
				
				if(Action.RIGHT_CLICK_BLOCK == clic) /* si le joueur clic droit */
				{
					if (event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.SIGN) /* si c'est un panneau */
					{
						Sign panneau = (Sign) event.getClickedBlock().getState();
						String[] contenu = panneau.getLines();
						
						Message content;
						
						if(EditSign.collection.containsKey(player_name)) /* Si le joueur est dans la liste */
						{
							if(IsPossible(contenu[0], player_name)) /* Si possible */
							{
								content = EditSign.collection.get(player_name);
								
								panneau.setLine(content.getNuLigne()-1, content.getMessage()); /* set de la ligne à faire */
								panneau.update(true); /* Pour reload le texte du panneau */
								player.sendMessage(EditSign.config.getString("Configuration.Messages.Check"));
							}
							else
							{
								player.sendMessage(EditSign.config.getString("Configuration.Messages.NotPermitSign"));
							}
							
							EditSign.collection.remove(player_name);
						}
					}
				}
			}
		}
		
		public boolean IsPossible(String fisrtLine, String nom)
		{
			if(fisrtLine.isEmpty()) /* Si c'est vide */
			{
				return true;
			}
			else
			{
				if(fisrtLine.startsWith("[")) /* Si commence par [ */
				{
					if(!(fisrtLine.endsWith("]"))) /* Si ne fini pas par ] */
					{
						return true;
					}
					else /* si fini par ] */
					{
						if(fisrtLine.equalsIgnoreCase("["+nom+"]")) /* Si c'est son nom */
						{
							return true;
						}
					}
				}
				else /* si ne commence pas par [ */
				{
					return true;
				}
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
			config.createSection("Configuration.Messages.Permit"); /* Si le serveur essai d'entrer une commande joueur */
			config.createSection("Configuration.Messages.Instruction"); /* Instruction */
			config.createSection("Configuration.Messages.Nombre"); /* Si le numero de ligne n'est pas entre 1 et 4 */
			config.createSection("Configuration.Messages.Check"); /* Lorsqu'un joueur modifie un panneau */
			config.createSection("Configuration.Messages.NotPermitSign"); /* Si ce n'est pas son panneau */
			
			
			config.set("Configuration.Active", true);
			
			config.set("Configuration.Messages.Active", "Plugin EditSign active");
			config.set("Configuration.Messages.Desactive", "Plugin EditSign desactive");
			config.set("Configuration.Messages.Reload", "Plugin EditSign reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.Instruction", "Cliquez droit sur un panneau pour le modifier");
			config.set("Configuration.Messages.Nombre", "Veuillez entrer un numero de ligne entre 1 et 4");
			config.set("Configuration.Messages.Check", "Panneau modifie");
			config.set("Configuration.Messages.NotPermitSign", "Ce n'est pas votre panneau, phrase supprimee");
			
			saveYML();
			config = YamlConfiguration.loadConfiguration(configFile);
			System.out.println("Le fichier de configuration de EditSign a ete cree.");
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