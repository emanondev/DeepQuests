package emanondev.quests.reward.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.gui.button.TextEditorButton;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.reward.Reward;
import emanondev.quests.reward.RewardType;
import emanondev.quests.utils.Savable;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import emanondev.quests.reward.AbstractReward;
import emanondev.quests.reward.AbstractRewardType;

public class ConsoleCommandRewardType extends AbstractRewardType implements RewardType {
	
	public ConsoleCommandRewardType() {
		super("CONSOLECOMMAND");
	}

	public class ConsoleCommandReward extends AbstractReward implements Reward {
		private static final String PATH_COMMAND = "command";
		private String command;
		public ConsoleCommandReward(MemorySection section,YmlLoadable gui) {
			super(section, gui);
			command = section.getString(PATH_COMMAND);
			this.addToEditor(1, new CommandEditorButtonFactory());
		}
		@Override
		public RewardType getType() {
			return ConsoleCommandRewardType.this;
		}
		public boolean setCommand(String cmd) {
			if (cmd!=null && cmd.equals(command))
				return false;
			if (cmd==null && command==null)
				return false;
			this.command = cmd;
			getSection().set(PATH_COMMAND,command);
			if (getParent() instanceof Savable)
				((Savable) getParent()).setDirty(true);
			return true;
		}
		private class CommandEditorButtonFactory implements EditorButtonFactory {
			private class CommandEditorButton extends TextEditorButton {
				private ItemStack item = new ItemStack(Material.COMMAND);
				public CommandEditorButton(CustomGui parent) {
					super(parent);
					update();
				}
				@Override
				public ItemStack getItem() {
					return item;
				}
				public void update() {
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&6&lReward Command Editor");
					desc.add("&6Click to edit");
					desc.add("&7Current value:");
					if (command!=null)
						desc.add("&7'&f"+command+"&7'");
					else
						desc.add("&7no command is set");
					desc.add("");
					desc.add("&7Represent the command of the Reward");
					StringUtils.setDescription(item, desc);
				}
				@Override
				public void onClick(Player clicker, ClickType click) {
					this.requestText(clicker, StringUtils.revertColors(command), changeCommandHelp);
				}
				@Override
				public void onReicevedText(String text) {
					if (text == null)
						text = "";
					if (setCommand(text)) {
						update();
						getParent().reloadInventory();
					}
					else
						getOwner().sendMessage(StringUtils.fixColorsAndHolders(
								"&cSelected command was not a valid command"));
				}
			}
			@Override
			public CustomButton getCustomButton(CustomGui parent) {
				return new CommandEditorButton(parent);
			}
		}
		@Override
		public String getInfo() {
			return "'"+command+"'";
		}
		@Override
		public void applyReward(QuestPlayer qPlayer, int amount) {
			if (command==null)
				return;
			String cmd = command.replace("%player%", qPlayer.getPlayer().getName()).replace("%player_name%", qPlayer.getPlayer().getName());
			for (int i = 0; i<amount ; i++)
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd);
		}
		
		
	}
	private static final BaseComponent[] changeCommandHelp = new ComponentBuilder(
			ChatColor.GOLD+"Click suggest the command executed by console to set as reward\n\n"+
			ChatColor.GOLD+"or just Change override old command with new command\n"+
			ChatColor.YELLOW+"/questtext <new command>\n\n"+
			ChatColor.GRAY+"use %player% as playername holder"
			).create();
		
	@Override
	public Material getGuiItemMaterial() {
		return Material.COMMAND;
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList(
				"&7Perform a console command,",
				"&7%player% is replaced with player name",
				"&7Command should not contain the starting '/'",
				"&7Example: 'give %player diamond'%"
				);
	}
	@Override
	public Reward getInstance(MemorySection section,YmlLoadable parent) {
		return new ConsoleCommandReward(section,parent);
	}
}
