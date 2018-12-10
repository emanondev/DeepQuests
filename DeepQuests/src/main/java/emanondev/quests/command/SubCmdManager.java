package emanondev.quests.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import emanondev.quests.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class SubCmdManager {
	private final String permission;
	private final List<String> aliases = new ArrayList<String>();
	protected final HashMap<String,SubCmdManager> subsByAlias = new HashMap<String,SubCmdManager>();
	protected final HashSet<SubCmdManager> subs = new HashSet<SubCmdManager>();
	
	/**
	 * 
	 * @param name - nome di questo sottocomando (ex: /gemme bal - "bal" è il nome del sottocomando
	 * @param permission - permesso (facoltativo) impedisce l'uso di questo sottocomando a chi non ha il permesso
	 * @param subs - lista eventuale di sottocomandi 
	 */
	public SubCmdManager(String name,String permission,SubCmdManager... subs) {
		this(Arrays.asList(name),permission,subs);
	}
	/**
	 * 
	 * @param aliases - lista alias di questo sottocomando (ex: /f help, /f ?, /f aiuto : "help,?,aiuto" sono alias
	 * @param permission - permesso (facoltativo) impedisce l'uso di questo sottocomando a chi non ha il permesso
	 * @param subs - lista eventuale di sottocomandi 
	 */
	public SubCmdManager(List<String> aliases,String permission,SubCmdManager... subs) {
		if (aliases!=null)
			for (String alias : aliases) {
				if (alias == null || alias.isEmpty() )
					try {
						throw new NullPointerException("attemping to register null or empty alias");
					} catch (Exception e) { e.printStackTrace(); }
				else if (alias.contains(" "))
					try {
						throw new NullPointerException("attemping to register alias with a space");
					} catch (Exception e) { e.printStackTrace(); }
				else if (!this.aliases.contains(alias.toLowerCase()))
					this.aliases.add(alias.toLowerCase());
				else
					try {
						throw new IllegalArgumentException("attemping to register alias "+alias.toLowerCase()+" twice");
					} catch (Exception e) { e.printStackTrace(); }
			}
		this.permission = permission;
		if (subs != null && subs.length != 0 ) {
			for (SubCmdManager sub : subs) {
				if (sub !=null ) {
					this.subs.add(sub);
					for (String alias : sub.getAliases())
						if (!this.subsByAlias.containsKey(alias))
							this.subsByAlias.put(alias,sub);
						else
							try {
								throw new IllegalArgumentException("attemping to register subcommand alias "+alias.toLowerCase()+" twice");
							} catch (Exception e) { e.printStackTrace(); }
				}
			}
		}
	}
	/**
	 * da implementare per sottocomandi "foglia"
	 * @param params - lista attuale dei parametri (ex: /bal pay catullo 45, se siamo al sottocomando "pay" params è "catullo","45")
	 * @param sender - esecutore del comando
	 * @param label - alias del comando usato, solitamente superfluo
	 * @param args - lista grezza completa dei parametri (ex: /bal pay catullo 45 , "pay","catullo",45")
	 */
	public void onCmd(ArrayList<String> params,CommandSender sender,String label,String[] args) {
		if (subs.isEmpty()) {
			CmdUtils.notImplemented(sender);
			return;
		}
		if (params==null || params.isEmpty() || !subsByAlias.containsKey(params.get(0).toLowerCase())) {
			onHelp(params,sender,label,args);
			return;
		}
		SubCmdManager sub = subsByAlias.get(params.get(0).toLowerCase());
		
		if (sub.playersOnly() && !(sender instanceof Player)) {
			CmdUtils.playersOnly(sender);
			return;
		}
		if (sub.hasPermission(sender)) {
			params.remove(0);
			sub.onCmd(params,sender,label, args);
			return;
		}
		CmdUtils.lackPermission(sender, sub.getPermission());
	}
	/**
	 * 
	* da implementare per sottocomandi "foglia" o che richiedono tab particolari
	 * @param params - lista attuale dei parametri (ex: /itemedit lore a, se siamo al sottocomando "lore" params è "a")
	 * @param sender - esecutore del comando
	 * @param label - alias del comando usato, solitamente superfluo
	 * @param args - lista grezza completa dei parametri (ex: /itemedit lore a , "lore","a")
	 * 
	 * autocompleta con i sottocomandi <br>
	 * (ex: per itemedit i sottocomandi sono "set","remove","add" quindi<br>
	 * completerà con i sottocomandi che continuano "a", in questo caso "add") <br><br>
	 * 
	 * 
	 */
	public ArrayList<String> onTab(ArrayList<String> params,CommandSender sender,String label,String[] args) {
		if (params==null || params.isEmpty() || subs.isEmpty())
			return new ArrayList<String>();
		if (params.size()==1) {
			ArrayList<String> list = new ArrayList<String>();
			for (SubCmdManager sub : subs) {
				if (sub.playersOnly() && !(sender instanceof Player))
					continue;
				if (sub.hasPermission(sender) && sub.getFirstAlias().startsWith(params.get(0).toLowerCase()))
					list.add(sub.getFirstAlias());
			}
			return list;
		}
		
		if (!subsByAlias.containsKey(params.get(0).toLowerCase()))
			return new ArrayList<String>();
		
		SubCmdManager sub = subsByAlias.get(params.get(0).toLowerCase());
		if (!sub.hasPermission(sender))
			return new ArrayList<String>();
		params.remove(0);
		return sub.onTab(params,sender,label, args);
	}
	private boolean playerOnly = false;
	/**
	 * metodo da usare nel costruttore per impedire l'uso ai non player,
	 * se settato true quando un non giocatore usa il comando stamperà che il comando è solo per player
	 * @param value
	 * @return the object for chaining
	 */
	public SubCmdManager setPlayersOnly(boolean value) {
		playerOnly = value;
		return this;
	}
	public boolean playersOnly() {
		return playerOnly;
	}
	/**
	 * Utility 
	 * @param s
	 * @return true if s has permission for this or if permission is null
	 */
	public boolean hasPermission(CommandSender s) {
		if (permission == null || s.hasPermission(permission)) {
			return true;
		}
		return false;
	}
	/*
	public void onHelp(CommandSender s,String label,String[] args) {
		CmdUtils.fail(s);
	}*/
	public List<String> getAliases(){
		return Collections.unmodifiableList(aliases);
	}
	public String getFirstAlias() {
		return aliases.get(0);
	}
	public String getPermission() {
		return permission;
	}
	private String params = null;
	public String getParams() {
		return params;
	}
	/**
	 * for help utility
	 * @param params - (ex: /itemedit lore , params = "<add,remove,set> [...]"; for /itemedit lore add , params = "[text to add]")
	 * @return this
	 */
	protected SubCmdManager setParams(String params) {
		this.params = params;
		return this;
	}
	private String description;
	public String getDescription() {
		return description;
	}
	/**
	 * for help utility
	 * @param list - (ex: for /itemedit lore add , list = Arrays.asList("&6Aggiunge il testo nell'ultima linea","&cNota:se il testo non è presente aggiunge una linea vuota"))
	 * @return this
	 */
	protected void setDescription(List<String> list) {
		if (list==null || list.isEmpty()) {
			setDescription((String) null);
			return;
		}
		StringBuilder text = new StringBuilder("");
		for (String line : list)
			text.append("\n"+line);
		setDescription(text.toString().replaceFirst("\n" , ""));
	}
	protected void setDescription(String s) {
		this.description = s;
	}
	private boolean showLockedSuggestions() {
		return showLockedSuggestions;
	}
	/**
	 * mostrare i sottocomandi a cui non si ha accesso? <br>
	 * ex: facendo /itemedit lore, mostrerà i 3 sottocomandi add,remove,set<br>
	 * nel caso sia settato a true e sender non abbia il permesso itemedit.lore.remove<br>
	 * mostrerà a sender solo add e set<br>
	 * nel caso sia settato a false e sender non abbia il permesso itemedit.lore.remove<br>
	 * mostrerà a sender add,set e remove ma marcherà remove in rosso <br>
	 * per mostrare l'assenza del permesso per eseguirlo
	 * @param value
	 */
	protected void setShowLockedSuggestions(boolean value) {
		showLockedSuggestions = value;
	}
	private boolean showLockedSuggestions = true;
	/**
	 * schermata di aiuto, si consiglia di non reimplementare
	 * @param params
	 * @param sender
	 * @param label
	 * @param args
	 */
	public void onHelp(ArrayList<String> params,CommandSender sender,String label,String[] args) {
		String previusArgs = getPreviusParams(params,label,args);
		ComponentBuilder comp = new ComponentBuilder(
				""+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"   Help   "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----");
		if (!subs.isEmpty()) {
			comp.append("\n"+ChatColor.BLUE+" - /"+previusArgs+ " [...]");
			if (getDescription()!=null)
				comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder( StringUtils.fixColorsAndHolders(getDescription()) ).create()
						));
			comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/"+previusArgs));
		
			for (SubCmdManager sub : subs) {
				if (sub.hasPermission(sender)) {
					if (sub.getParams()==null)
						comp.append("\n"+ChatColor.DARK_AQUA+sub.getFirstAlias());
					else
						comp.append("\n"+ChatColor.DARK_AQUA+sub.getFirstAlias()+" "+ChatColor.AQUA+sub.getParams());
					if (sub.getDescription()!=null)
						comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new ComponentBuilder( StringUtils.fixColorsAndHolders(sub.getDescription()) ).create()
								));
					comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/"+previusArgs+" "+sub.getFirstAlias()));
				}
				else if (showLockedSuggestions()) {
					if (sub.getParams()==null)
						comp.append("\n"+ChatColor.RED+sub.getFirstAlias());
					else
						comp.append("\n"+ChatColor.RED+sub.getFirstAlias()+" "+ChatColor.GOLD+sub.getParams());
					if (getDescription()!=null)
						comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder( StringUtils.fixColorsAndHolders(getDescription()) ).create()
								));
				}
			}
		}
		else {
			if (getParams()!=null)
				comp.append("\n"+ChatColor.RED+" - /"+previusArgs+" "+ChatColor.GOLD+getParams());
			else
				comp.append("\n"+ChatColor.RED+" - /"+previusArgs);
			if (getDescription()!=null)
				comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder( StringUtils.fixColorsAndHolders(getDescription()) ).create()
						));
			comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/"+previusArgs));
			
		}
		comp.append("\n"+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"   Help   "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----");
		
		sender.spigot().sendMessage(comp.create());
	}
	private String getPreviusParams(ArrayList<String> params,String label,String[] args) {
		int max = args.length-params.size();
		StringBuilder text = new StringBuilder(label);
		for (int i = 0; i < max ; i++) {
			text.append(" "+args[i]);
		}
		return text.toString();
	}
}
