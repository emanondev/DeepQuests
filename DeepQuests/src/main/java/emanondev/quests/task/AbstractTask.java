package emanondev.quests.task;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import emanondev.quests.Defaults;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.BossBarData;
import emanondev.quests.mission.Mission;
import emanondev.quests.newgui.button.AmountSelectorButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QCWithWorlds;
import emanondev.quests.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

/**
 * 
 * @author emanon <br>
 *
 * 
 */
public abstract class AbstractTask extends QCWithWorlds implements Task {
	/**
	 * path from ConfigSection m of the task to the type of the task
	 */
	public static final String PATH_TASK_TYPE = "type";
	/**
	 * path from ConfigSection m of the task to the max progress value of the task
	 */
	public static final String PATH_TASK_MAX_PROGRESS = "max-progress";
	

	public static final String PATH_TASK_DESCRIPTION_PROGRESS = "description-progress";
	public static final String PATH_TASK_DESCRIPTION_UNSTARTED = "description-unstarted";
	
	/**
	 * Config variables can be stored and read from ConfigSection m<br>
	 * also additional info may be taken from Mission parent<br>
	 * 
	 * N.B. parent mission is not fully loaded when Task is generating<br>
	 * - parent.getTask() will be empty
	 * @param m must be != null
	 * @param parent must be != null
	 */
	public AbstractTask(ConfigSection m,Mission parent,TaskType type) {
		super(m,parent);
		if (type ==null)
			throw new NullPointerException();
		this.type = type;
		this.maxProgress = loadMaxProgress();
		if (this.maxProgress <= 0)
			throw new IllegalArgumentException("task max progress must be always > 0");
		this.descUnstarted = loadDescUnstarted();
		this.descProgress = loadDescProgress();
		bossBarData = new BossBarData(m,this);
	}
	private final BossBarData bossBarData;
	

	public BarStyle getBossBarStyle() {
		return bossBarData.getStyle();
	}
	public BarColor getBossBarColor() {
		return bossBarData.getColor();
	}
	private String loadDescUnstarted() {
		if (!getSection().isString(PATH_TASK_DESCRIPTION_UNSTARTED)) {
			String val = Defaults.TaskDef.getUnstartedDescription()
					.replace("<task>",this.getID());
			getSection().set(PATH_TASK_DESCRIPTION_UNSTARTED, val);
			setDirtyLoad();
			return val;
		}
		return getSection().getString(PATH_TASK_DESCRIPTION_UNSTARTED, 
			Defaults.TaskDef.getUnstartedDescription()
			.replace("<task>",this.getID()));
	}
	private String loadDescProgress() {
		if (!getSection().isString(PATH_TASK_DESCRIPTION_PROGRESS)) {
			String val = StringUtils.revertColors(Defaults.TaskDef.getProgressDescription()
					.replace("<task>",this.getID()));
			getSection().set(PATH_TASK_DESCRIPTION_PROGRESS, val);
			setDirtyLoad();
			return val;
		}
		return StringUtils.revertColors(getSection().getString(PATH_TASK_DESCRIPTION_PROGRESS, 
			Defaults.TaskDef.getProgressDescription()
			.replace("<task>",this.getID())));
	}
	public boolean setUnstartedDescription(String name) {
		if (name==null)
			return false;
		name = StringUtils.revertColors(name.replace("<task>",this.getID()));
		if (name.equals(descUnstarted))
			return false;
		this.descUnstarted = name;
		getSection().set(PATH_TASK_DESCRIPTION_UNSTARTED,descUnstarted);
		getParent().reloadDisplay();
		setDirty(true);
		return true;
	}
	public boolean setProgressDescription(String name) {
		if (name==null)
			return false;
		name = StringUtils.revertColors(name.replace("<task>",this.getID()));
		if (name.equals(descProgress))
			return false;
		this.descProgress = name;
		getSection().set(PATH_TASK_DESCRIPTION_PROGRESS,descProgress);
		getParent().reloadDisplay();
		setDirty(true);
		return true;
	}
	public String getUnstartedDescription() {
		return this.descUnstarted;
	}
	public String getProgressDescription() {
		return this.descProgress;
	}
	private String descUnstarted;
	private String descProgress;
	private final TaskType type;
	private int maxProgress;
	/**
	 * 
	 * @return the Mission that holds this task
	 */
	public Mission getParent() {
		return (Mission) super.getParent();
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
		getSection().set(PATH_TASK_MAX_PROGRESS, maxProgress);
		getParent().reloadDisplay();
		this.setDirty(true);
		return true;
	}
	
	/**
	 * returned value must be > 0
	 * @param m config
	 * @return the max progress amount
	 */
	private int loadMaxProgress() {
		int pr = getSection().getInt(PATH_TASK_MAX_PROGRESS);
		if (pr < 1) {
			getSection().set(PATH_TASK_MAX_PROGRESS,1);
			pr = 1;
			setDirtyLoad();
		}
		return pr;
	}

	protected  List<String> getWorldsListDefault(){
		return Defaults.TaskDef.getWorldsListDefault();
	}
	protected boolean shouldWorldsAutogen() {
		return Defaults.TaskDef.shouldWorldsAutogen();
	}
	protected boolean getUseWorldsAsBlacklistDefault() {
		return Defaults.TaskDef.getUseWorldsAsBlacklistDefault();
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

	private static final BaseComponent[] changeUnstartedDescDescription = new ComponentBuilder(
			ChatColor.GOLD+"Click suggest the command and the old task description when unstarted\n\n"+
			ChatColor.GOLD+"Change override old description with new description\n"+
			ChatColor.YELLOW+"/questtext <new description>\n\n"+
			ChatColor.GRAY+"Values between { } are placeholders"
			).create();
	
	private static final BaseComponent[] changeProgressDescDescription = new ComponentBuilder(
			ChatColor.GOLD+"Click suggest the command and the old task description when in progress\n\n"+
			ChatColor.GOLD+"Change override old description with new description\n"+
			ChatColor.YELLOW+"/questtext <new description>\n\n"+
			ChatColor.GRAY+"Values between { } are placeholders"
			).create();
	
	@Override
	public TaskEditor createEditorGui(Player p,Gui previusHolder) {
		return new TaskEditor(p,previusHolder);
	}
	
	public List<String> getInfo(){
		List<String> info = new ArrayList<String>();
		info.add("&9&lTask: &6"+ this.getDisplayName());
		info.add("&8Type: &7"+getTaskType().getKey());

		info.add("&8ID: "+ this.getID());
		info.add("");
		info.add("&9Priority: &e"+getPriority());
		info.add("&9Max Progress: &e"+getMaxProgress());
		info.add("&9Quest: &e"+getParent().getParent().getDisplayName());
		info.add("&9Mission: &e"+getParent().getDisplayName());
		if (this.getWorldsList().size() > 0) {
			if (this.isWorldListBlacklist()) {
				info.add("&9Blacklisted Worlds:");
				for (String world : this.getWorldsList())
					info.add("&9 - &c" + world);
			}
			else {
				info.add("&9WhiteListed Worlds:");
				for (String world : this.getWorldsList())
					info.add("&9 - &a" + world);
			}
		}
		return info;
	}
	
	
	protected class TaskEditor extends QCWithWorldsEditor {

		public TaskEditor(Player p, Gui previusHolder) {
			super("&8Task: &9"+AbstractTask.this.getDisplayName(), p, previusHolder);
			this.putButton(8,new MaxProgressButton());
			this.putButton(16, new UnstartedDescriptionButton());
			this.putButton(17, new ProgressDescriptionButton());
			this.putButton(15, AbstractTask.this.getPriorityData().getPriorityEditorButton(this));
			this.putButton(25, bossBarData.getStyleSelectorButton(this));
			this.putButton(26, bossBarData.getColorSelectorButton(this));
		}
		private class UnstartedDescriptionButton extends emanondev.quests.newgui.button.TextEditorButton {

			public UnstartedDescriptionButton() {
				super(new ItemBuilder(Material.NAME_TAG).setGuiProperty().build(), TaskEditor.this);
			}

			@Override
			public List<String> getButtonDescription() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lUnstarted Task Description Editor");
				desc.add("&6Click to edit");
				desc.add("&7Current value:");
				desc.add("&7'&f"+descUnstarted+"&7'");
				desc.add("");
				desc.add("&7Represent the description of the task");
				desc.add("&7When the task is not started by the player");
				return desc;
			}

			@Override
			public void onReicevedText(String text) {
				AbstractTask.this.setUnstartedDescription(text);
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				this.requestText(clicker, changeUnstartedDescDescription);
			}
			
		}
		
		private class ProgressDescriptionButton extends emanondev.quests.newgui.button.TextEditorButton {

			public ProgressDescriptionButton() {
				super(new ItemBuilder(Material.NAME_TAG).setGuiProperty().build(), TaskEditor.this);
			}

			@Override
			public List<String> getButtonDescription() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lProgress Task Description Editor");
				desc.add("&6Click to edit");
				desc.add("&7Current value:");
				desc.add("&7'&f"+descProgress+"&7'");
				desc.add("");
				desc.add("&7Represent the description of the task");
				desc.add("&7When the task is on progress by the player");
				return desc;
			}

			@Override
			public void onReicevedText(String text) {
				AbstractTask.this.setUnstartedDescription(text);
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				this.requestText(clicker, changeProgressDescDescription);
			}
			
		}
		
		private class MaxProgressButton extends AmountSelectorButton {

			public MaxProgressButton() {
				super("Max Progress Editor", new ItemBuilder(Material.DIODE).setGuiProperty().build(), TaskEditor.this);
			}

			@Override
			public List<String> getButtonDescription() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lMax Progress Editor");
				desc.add("&6Click to edit");
				desc.add("&7Max progress is &e"+getMaxProgress());
				return desc;
			}

			@Override
			public long getCurrentAmount() {
				return AbstractTask.this.maxProgress;
			}

			@Override
			public boolean onAmountChangeRequest(long i) {
				return setMaxProgress((int) i);
			}
				
		}
	}
}
