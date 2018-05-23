package emanondev.quests.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import emanondev.quests.gui.TextEditorButton;
import emanondev.quests.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class CommandQuestText extends CmdManager implements TabExecutor {
	public CommandQuestText() {
		super("questtext",null,null);
		this.setPlayersOnly(true);
		//Quests.getInstance().registerCommand(this);
	}
	
	private static final HashMap<Player,TextEditorButton> map = new HashMap<Player,TextEditorButton>();

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		return new ArrayList<String>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!(sender instanceof Player)) {
			CmdUtils.playersOnly(sender);
			return true;
		}
		Player p = (Player) sender;
		if (!map.containsKey(p)){
			p.sendMessage(StringUtils.fixColorsAndHolders("&cNo text is requested"));
			return true;
		}
		if (args.length>0) {
			StringBuilder text = new StringBuilder("");
			for (String arg : args)
				text.append(" "+arg);
			map.get(p).onReicevedText(text.toString().replaceFirst(" ",""));
		}
		else
			map.get(p).onReicevedText(null);
		
		p.openInventory(map.get(p).getParent().getInventory());
		map.remove(p);
		return true;
	}
	
	public static void requestText(Player p,String baseText,BaseComponent[] description,TextEditorButton item) {
		map.put(p,item);
		p.closeInventory();
		ComponentBuilder comp = new ComponentBuilder(
				ChatColor.GOLD+"****************************\n"+
				ChatColor.GOLD+"           Click Me           \n"+
				ChatColor.GOLD+"****************************");
		if (baseText==null)
			comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
					"/questtext "));
		else
			comp.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
					"/questtext "+baseText));
		if (description!=null)
			comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,description));
		
		p.spigot().sendMessage(comp.create());
	}

}
