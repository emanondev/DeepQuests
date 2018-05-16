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
	
	public SubCmdManager(String name,String permission,SubCmdManager... subs) {
		this(Arrays.asList(name),permission,subs);
	}
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
	public SubCmdManager setPlayersOnly(boolean value) {
		playerOnly = value;
		return this;
	}
	public boolean playersOnly() {
		return playerOnly;
	}
	
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
	protected void setParams(String params) {
		this.params = params;
	}
	private String description;
	public String getDescription() {
		return description;
	}
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
	protected void setShowLockedSuggestions(boolean value) {
		showLockedSuggestions = value;
	}
	private boolean showLockedSuggestions = true;
	public void onHelp(ArrayList<String> params,CommandSender sender,String label,String[] args) {
		String previusArgs = getPreviusParams(params,label,args);
		ComponentBuilder comp = new ComponentBuilder(
				""+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"   Help   "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+"\n"+ChatColor.BLUE+" - /"+previusArgs+ " [...]");
		if (getDescription()!=null)
			comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder( StringUtils.fixColorsAndHolders(getDescription()) ).create()
					));
		comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/"+previusArgs));
		
		if (!subs.isEmpty())
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
