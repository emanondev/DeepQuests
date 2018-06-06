package emanondev.quests.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.gui.ItemEditorButton;
import emanondev.quests.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class CommandQuestItem extends CmdManager implements TabExecutor {
	public CommandQuestItem() {
		super("questitem",null,null);
		this.setPlayersOnly(true);
		//Quests.getInstance().registerCommand(this);
	}
	
	private static final HashMap<Player,ItemEditorButton> map = new HashMap<Player,ItemEditorButton>();

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
			p.sendMessage(StringUtils.fixColorsAndHolders("&cNo item is requested"));
			return true;
		}
		ItemStack item = p.getInventory().getItemInMainHand();
		if (item == null || item.getType()==Material.AIR) {
			map.get(p).onReicevedItem(null);
		}
		else
			map.get(p).onReicevedItem(item);
		p.openInventory(map.get(p).getParent().getInventory());
		map.remove(p);
		return true;
	}
	
	public static void requestItem(Player p,BaseComponent[] description,ItemEditorButton item) {
		map.put(p,item);
		p.closeInventory();
		ComponentBuilder comp = new ComponentBuilder(
				ChatColor.GOLD+"*******************************\n"+
				ChatColor.GOLD+"Keep the item in Hand and Click Me\n"+
				ChatColor.GOLD+"*******************************");
			comp.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
					"/questitem"));
		if (description!=null)
			comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,description));
		
		p.spigot().sendMessage(comp.create());
	}

}
