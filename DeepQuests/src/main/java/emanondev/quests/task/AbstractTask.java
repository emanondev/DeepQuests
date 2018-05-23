package emanondev.quests.task;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.Defaults;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomLinkedGui;
import emanondev.quests.gui.EditorGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.mission.Mission;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

/**
 * Any implementations of Task require to implement a Constructor like<br>
 * [YourSubTaskClass](MemorySection m,Mission parent)
 * 
 * @author emanon <br>
 *
 * 
 */
public abstract class AbstractTask extends YmlLoadable implements Task {
	/**
	 * path from MemorySection m of the task to the type of the task
	 */
	public static final String PATH_TASK_TYPE = "type";
	/**
	 * path from MemorySection m of the task to the max progress value of the task
	 */
	public static final String PATH_TASK_MAX_PROGRESS = "max-progress";
	

	public static final String PATH_TASK_DESCRIPTION_PROGRESS = "description-progress";
	public static final String PATH_TASK_DESCRIPTION_UNSTARTED = "description-unstarted";
	
	/**
	 * Config variables can be stored and read from MemorySection m<br>
	 * also additional info may be taken from Mission parent<br>
	 * 
	 * N.B. parent mission is not fully loaded when Task is generating<br>
	 * - parent.getTask() will be empty
	 * @param m must be != null
	 * @param parent must be != null
	 */
	public AbstractTask(MemorySection m,Mission parent,TaskType type) {
		super(m);
		if (parent == null||type ==null)
			throw new NullPointerException();
		this.parent = parent;
		this.type = type;
		this.maxProgress = loadMaxProgress(m);
		if (this.maxProgress <= 0)
			throw new IllegalArgumentException("task max progress must be always > 0");
		this.descUnstarted = loadDescUnstarted(m);
		this.descProgress = loadDescProgress(m);
		
		this.addToEditor(new EditMaxProgressFactory());
	}
	private String loadDescUnstarted(MemorySection m) {
		if (!m.isString(PATH_TASK_DESCRIPTION_UNSTARTED)) {
			String val = Defaults.TaskDef.getUnstartedDescription()
					.replace("<task>",this.getNameID());
			m.set(PATH_TASK_DESCRIPTION_UNSTARTED, val);
			dirty = true;
			return val;
		}
		return m.getString(PATH_TASK_DESCRIPTION_UNSTARTED, 
			Defaults.TaskDef.getUnstartedDescription()
			.replace("<task>",this.getNameID()));
	}
	private String loadDescProgress(MemorySection m) {
		if (!m.isString(PATH_TASK_DESCRIPTION_PROGRESS)) {
			String val = Defaults.TaskDef.getProgressDescription()
					.replace("<task>",this.getNameID());
			m.set(PATH_TASK_DESCRIPTION_PROGRESS, val);
			dirty = true;
			return val;
		}
		return m.getString(PATH_TASK_DESCRIPTION_PROGRESS, 
			Defaults.TaskDef.getProgressDescription()
			.replace("<task>",this.getNameID()));
	}
	public String getUnstartedDescription() {
		return this.descUnstarted;
	}
	public String getProgressDescription() {
		return this.descProgress;
	}
	private final String descUnstarted;
	private final String descProgress;
	private final TaskType type;
	private int maxProgress;
	private final Mission parent;
	/**
	 * 
	 * @return the Mission that holds this task
	 */
	public Mission getParent() {
		return parent;
	}
	public void setDirty(boolean value) {
		super.setDirty(value);
		if (this.isDirty()==true)
			parent.setDirty(true);
	}
	/**
	 * 
	 * @return the type of the task
	 */
	public TaskType getTaskType() {
		return type;
	}
	/**
	 * when a player starts a Task his progress on th task is 0<br>
	 * task is completed when player progress reach this value
	 * @return the maximus progress of this task
	 */
	public int getMaxProgress() {
		return maxProgress;
	}
	
	public boolean setMaxProgress(int value) {
		value = Math.max(1,value);
		if (this.maxProgress == value)
			return false;
		
		this.maxProgress = value;
		this.setDirty(true);
		return true;
	}
	
	/**
	 * returned value must be > 0
	 * @param m config
	 * @return the max progress amount
	 */
	private int loadMaxProgress(MemorySection m) {
		if (m == null)
			throw new NullPointerException();
		int pr = m.getInt(PATH_TASK_MAX_PROGRESS);
		if (pr < 1) {
			m.set(PATH_TASK_MAX_PROGRESS,1);
			pr = 1;
			dirty = true;
		}
		return pr;
	}
	/**
	 * utility to autogenerate descriptions on gui<br>
	 * this method should return a string that represent<br>
	 * info abouth the task<br><br>
	 * 
	 * ex:<br>
	 * a Mining task<br>
	 * task.getDefaultDescription() = "&eMine "+task.getMaxProgress()+" "+task.getBlockType()+" blocks"
	 * @return
	 */
	//protected abstract String getDefaultDescription();

	protected  List<String> getWorldsListDefault(){
		return Defaults.TaskDef.getWorldsListDefault();
	}
	protected boolean shouldWorldsAutogen() {
		return Defaults.TaskDef.shouldWorldsAutogen();
	}
	protected boolean getUseWorldsAsBlackListDefault() {
		return Defaults.TaskDef.getUseWorldsAsBlackListDefault();
	}
	@Override
	protected boolean shouldAutogenDisplayName() {
		return Defaults.TaskDef.shouldAutogenDisplayName();
	}
	
	/**
	 * checks if the task, his mission and associated quest are all allowed in this world
	 */
	@Override
	public boolean isWorldAllowed(World w) {
		return super.isWorldAllowed(w)
				&& getParent().isWorldAllowed(w)
				&& getParent().getParent().isWorldAllowed(w);
	}
	
	public boolean onProgress(QuestPlayer p) {
		return onProgress(p,1);
	}
	
	
	public boolean onProgress(QuestPlayer p,int amount) {
		return p.progressTask(this, amount);
	}
	
	/*
	 * displayname:
	 * worlds:
	 * 		list:
	 * 		isblacklist:
	 * type:
	 * maxprogress:
	 */
	@Override
	public BaseComponent[] toComponent() {
		ComponentBuilder comp = new ComponentBuilder(ChatColor.DARK_AQUA+"Type: "+ChatColor.GOLD+type.getKey())
			.append("\n"+ChatColor.DARK_AQUA+"ID: "+ChatColor.YELLOW+getNameID())
			.append("\n"+ChatColor.DARK_AQUA+"DisplayName: "+ChatColor.GREEN+getDisplayName())
			.append("\n"+ChatColor.DARK_AQUA+"MaxProgress: "+ChatColor.GREEN+getMaxProgress())
			.append("\n"+ChatColor.DARK_AQUA+"Description Unstarted: \n"
					+ChatColor.GREEN+" '"+getUnstartedDescription()+"'")
			.append("\n"+ChatColor.DARK_AQUA+"Description Progress: \n"
					+ChatColor.GREEN+" '"+getProgressDescription()+"'");
		if (this.getWorldsList().size()>0) {
			if (this.isWorldListBlackList())
				comp.append("\n"+ChatColor.RED+"BlackListed "+ChatColor.DARK_AQUA+"Worlds:");
			else
				comp.append("\n"+ChatColor.GREEN+"WhiteListed "+ChatColor.DARK_AQUA+"Worlds:");
			for (String world : this.getWorldsList())
				comp.append("\n"+ChatColor.AQUA+" - "+world)
				.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
						"/qa quest "+parent.getParent().getNameID()+" mission "
								+parent.getNameID()+" task "
								+this.getNameID()+" worlds remove "+world))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder(ChatColor.YELLOW+"Click to remove")
						.create()));
		}
		
		return comp.create();
	}
	
	private class EditMaxProgressFactory implements EditorButtonFactory {
		private class EditMaxProgressButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.DIODE);
			public EditMaxProgressButton(CustomGui parent) {
				super(parent);
				update();
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			public void update() {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lMax Progress Editor"));
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(StringUtils.fixColorsAndHolders("&6Click to edit task max progress"));
				lore.add(StringUtils.fixColorsAndHolders("&aMax progress is &e"+getMaxProgress()));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new MaxProgressEditorGui(clicker,
						(EditorGui) this.getParent()).getInventory());
			}
		}
		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			return new EditMaxProgressButton(parent);
		}
	}
	
	private class MaxProgressEditorGui extends CustomLinkedGui<CustomButton> {
		public MaxProgressEditorGui(Player p, EditorGui previusHolder) {
			super(p,previusHolder, 6);
			items.put(4, new MaxProgressButton());
			items.put(19, new MaxProgressEditorButton(1));
			items.put(20, new MaxProgressEditorButton(10));
			items.put(21, new MaxProgressEditorButton(100));
			items.put(22, new MaxProgressEditorButton(1000));
			items.put(23, new MaxProgressEditorButton(10000));
			items.put(24, new MaxProgressEditorButton(100000));
			items.put(25, new MaxProgressEditorButton(1000000));
			items.put(28, new MaxProgressEditorButton(-1));
			items.put(29, new MaxProgressEditorButton(-10));
			items.put(30, new MaxProgressEditorButton(-100));
			items.put(31, new MaxProgressEditorButton(-1000));
			items.put(32, new MaxProgressEditorButton(-10000));
			items.put(33, new MaxProgressEditorButton(-100000));
			items.put(34, new MaxProgressEditorButton(-1000000));
			this.setFromEndCloseButtonPosition(8);
			reloadInventory();
		}
		private class MaxProgressButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.DIODE);
			public MaxProgressButton() {
				super(MaxProgressEditorGui.this);
				update();
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			@Override
			public void update() {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6Max Task Progress: &e"+getMaxProgress()));
				item.setItemMeta(meta);
			}
			@Override
			public void onClick(Player clicker, ClickType click) {}
		}
		
		private class MaxProgressEditorButton extends CustomButton {
			private int amount;
			public MaxProgressEditorButton(int amount) {
				super(MaxProgressEditorGui.this);
				this.amount = amount;
				
				ItemMeta meta = item.getItemMeta();
				if (this.amount>0) {
					this.item.setDurability((short) 5);
					meta.setDisplayName(StringUtils.fixColorsAndHolders("&aAdd "+this.amount));
				}
				else {
					this.item.setDurability((short) 14);
					meta.setDisplayName(StringUtils.fixColorsAndHolders("&cRemove "+(-this.amount)));
				}
				item.setItemMeta(meta);
			}
			private ItemStack item = new ItemStack(Material.WOOL);
			@Override
			public ItemStack getItem() {
				return item;
			}
			public void update() {}
			@Override
			public void onClick(Player clicker, ClickType click) {
				if (setMaxProgress(getMaxProgress()+amount))
					getParent().update();
			}
		}
		
	}
	public String getGuiTitle() {
		return StringUtils.fixColorsAndHolders(
				"&8"+StringUtils.withoutColor(getDisplayName())+
				" <> "+StringUtils.withoutColor(parent.getDisplayName())+
				" <> "+StringUtils.withoutColor(parent.getParent().getDisplayName()));
	}
}
