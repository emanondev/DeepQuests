package emanondev.quests.quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import emanondev.quests.Quests;
import emanondev.quests.YMLConfig;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomLinkedGui;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.gui.SubExplorerFactory;
import emanondev.quests.gui.TextEditorButton;
import emanondev.quests.mission.Mission;
import emanondev.quests.utils.Savable;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class QuestManager implements Savable {
	private final static String PATH_QUESTS = "quests";
	private final static String PATH_QUEST_COUNTER = "quest-counter";
	private final static String PATH_MISSION_COUNTER = "mission-counter";
	private final static String PATH_TASK_COUNTER = "task-counter";
	
	private final YMLConfig data;
	public String getNewTaskID(Mission m) {
		long i = data.getLong(PATH_TASK_COUNTER,0);
		String key = null;
		boolean found = false;
		do {
			if (i<10)
				key = "t000"+i;
			else if (i<100)
				key = "t00"+i;
			else if (i<1000)
				key = "t0"+i;
			else
				key = "t"+i;
			if (m.getTaskByNameID(key)==null)
				found = true;
			i++;
		} while (i<10000 && found == false);
		if (found == false) {
			do {
				key = "t"+i;
				if (m.getTaskByNameID(key)==null)
					found = true;
				i++;
			} while (found == false);
		}
		data.set(PATH_TASK_COUNTER,i);
		return key;
	}
	public String getNewMissionID(Quest q) {
		long i = data.getLong(PATH_MISSION_COUNTER,0);
		String key;
		boolean found = false;
		do {
			if (i<10)
				key = "m000"+i;
			else if (i<100)
				key = "m00"+i;
			else if (i<1000)
				key = "m0"+i;
			else
				key = "m"+i;
			if (q.getMissionByNameID(key)==null)
				found = true;
			i++;
		}while (i<10000 && found == false);
		if (found == false) {
			do {
				key = "m"+i;
				if (q.getMissionByNameID(key)==null)
					found = true;
				i++;
			} while (found == false);
		}
		data.set(PATH_MISSION_COUNTER,i);
		return key;
	}
	public String getNewQuestID() {
		long i = data.getLong(PATH_QUEST_COUNTER,0);
		String key;
		boolean found = false;
		do {
			if (i<10)
				key = "q000"+i;
			else if (i<100)
				key = "q00"+i;
			else if (i<1000)
				key = "q0"+i;
			else
				key = "q"+i;
			if (getQuestByNameID(key)==null)
				found = true;
			i++;
		}while (i<10000 && found == false);
		if (found == false) {
			do {
				key = "q"+i;
				if (getQuestByNameID(key)==null)
					found = true;
				i++;
			}while (found == false);
		}
		data.set(PATH_QUEST_COUNTER,i);
		return key;
	}
	
	
	private static final HashMap<String,Quest> quests = new HashMap<String,Quest>();
	
	public QuestManager(JavaPlugin plugin,String filename) {
		data = new YMLConfig(plugin,filename);
	}
	public void save() {
		data.save();
		setDirty(false);
	}
	public void reload() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			Inventory inv = p.getOpenInventory().getTopInventory();
			if (inv != null && inv.getHolder()!=null)
				if (inv.getHolder() instanceof CustomGui)
					p.closeInventory();
		}
		quests.clear();
		data.reload();
		Object unknow = data.get(PATH_QUESTS);
		if (unknow!=null && unknow instanceof MemorySection) {
			MemorySection section = (MemorySection) unknow;
			Set<String> s = section.getValues(false).keySet();
			s.forEach((key)->{
				boolean dirty = false;
				try {
					Quest quest = new Quest((MemorySection) section.get(key),this);
					quests.put(quest.getNameID(),quest);
					if (quest.isDirty())
						dirty = true;
				}catch (Exception e) {
					e.printStackTrace();
					Quests.getInstance().getLoggerManager().getLogger("errors")
						.log("Error while loading Quests on file quests.yml '"
								+key+"' could not be read as valid quest"
								,ExceptionUtils.getStackTrace(e));
				}
				for (Quest quest : quests.values()){
					if (isDirty())
						break;
					if (quest.isDirty())
						setDirty(true);
				}
				if (dirty)
					data.save();
			});
		}
	}
	
	
	public Quest getQuestByNameID(String key) {
		return quests.get(key);
	}
	public Collection<Quest> getQuests() {
		return Collections.unmodifiableCollection(quests.values());
	}

	private boolean dirty = false;
	
	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setDirty(boolean value) {
		if (dirty == value)
			return;
		dirty = value;
		if (dirty == true) {
			/*Bukkit.getScheduler().runTaskLater(Quests.getInstance(), new Runnable() {
				@Override
				public void run() {*/
					QuestManager man = Quests.getInstance().getQuestManager();
					if (!man.isDirty())
						return;
					data.save();
					man.setDirty(false);
				/*}
			}, 20);*/
		}
		else {
			for (Quest quest : quests.values()) {
				quest.setDirty(false);
			}
		}
		
	}
	
	public boolean addQuest(String id,String displayName) {
		if (id == null || id.isEmpty() || 
				id.contains(" ")||id.contains(".")||id.contains(":"))
			return false;
		if (displayName == null)
			displayName = id.replace("_"," ");
		id = id.toLowerCase();
		if (quests.containsKey(id))
			return false;
		data.set(PATH_QUESTS+"."+id+"."+YmlLoadable.PATH_DISPLAY_NAME,displayName);
		Quest q = new Quest((MemorySection) data.get(PATH_QUESTS+"."+id),this);
		quests.put(q.getNameID(), q);
		save();
		reload();
		Quests.getInstance().getPlayerManager().reload();
		return true;
	}

	public boolean deleteQuest(Quest quest) {
		if (quest == null || !quests.containsKey(quest.getNameID()) )
			return false;
		data.set(PATH_QUESTS+"."+quest.getNameID(),null);
		quests.remove(quest.getNameID());
		save();
		reload();
		Quests.getInstance().getPlayerManager().reload();
		return true;
	}
	public void openEditorGui(Player clicker, CustomGui prevGui) {
		clicker.openInventory(new QuestsEditorGui(clicker,prevGui).getInventory());
	}
	public void openEditorGui(Player clicker) {
		openEditorGui(clicker,null);
	}
	private class QuestsEditorGui extends CustomLinkedGui<CustomButton> {
		public QuestsEditorGui(Player p, CustomGui previusHolder) {
			super(p,previusHolder, 6);
			this.setFromEndCloseButtonPosition(8);
					
			this.addButton(13,new AddQuestButton(this));
			if (quests.size()>0) {
				this.addButton(16,new DeleteQuestButton(this));
				this.addButton(10,new SubExplorerFactory<Quest>(Quest.class,getQuests(),
						"&8Quests List").getCustomButton(this));	
			}
			this.setTitle(null, StringUtils.fixColorsAndHolders("&8Quests Manager Editor"));
			reloadInventory();
		}
		
		private class AddQuestButton extends CustomButton {

			public AddQuestButton(CustomGui parent) {
				super(parent);
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&a&lAdd &6&lQuest");
				desc.add("&6Click to create a new Quest");
				StringUtils.setDescription(item,desc);
			}
			private ItemStack item = new ItemStack(Material.GLOWSTONE);
			@Override
			public ItemStack getItem() {
				return item;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new CreateQuestGui(clicker,this.getParent()).getInventory());
			}
			private class CreateQuestGui extends CustomLinkedGui<CustomButton>{
				private String displayName = null;
				public CreateQuestGui(Player p, CustomGui previusHolder) {
					super(p, previusHolder, 6);
					this.setFromEndCloseButtonPosition(8);
					this.addButton(22, new CreateQuestButton());
					this.setTitle(null,StringUtils.fixColorsAndHolders("&8Create a New Quest"));
					reloadInventory();
				}
				private class CreateQuestButton extends TextEditorButton {
					private ItemStack item = new ItemStack(Material.NAME_TAG);
					public CreateQuestButton() {
						super(CreateQuestGui.this);
						update();
					}
					
					@Override
					public ItemStack getItem() {
						return item;
					}
					public void update() {
						ArrayList<String> desc = new ArrayList<String>();
						desc.add("&6Click to select a display name");
						StringUtils.setDescription(item, desc);
					}
					private String key = null;
					@Override
					public void onReicevedText(String text) {
						if (text==null || text.isEmpty()) {
							CreateQuestGui.this.getPlayer().sendMessage(
									StringUtils.fixColorsAndHolders("&cInvalid Name"));
							return;
						}
						displayName = text;
						key = getNewQuestID();
						if (!addQuest(key,displayName)) {
							return;
						}
						Bukkit.getScheduler().runTaskLater(Quests.getInstance(), new Runnable() {
							@Override
							public void run() {
								CreateQuestGui.this.getPlayer()
									.performCommand("questadmin quest "+key+" editor");
							}
						}, 2);
					}


					@Override
					public void onClick(Player clicker, ClickType click) {
						if (displayName==null)
							this.requestText(clicker, null, setDisplayNameDescription);
						else
							if (key!=null)
								CreateQuestGui.this.getPlayer().performCommand("questadmin quest "+key+" editor");
					}
				}
			}
		}
		private class DeleteQuestButton extends CustomButton {

			public DeleteQuestButton(CustomGui parent) {
				super(parent);
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&c&lDelete &6&la Quest");
				desc.add("&6Click to select and delete a Quest");
				StringUtils.setDescription(item,desc);
			}
			private ItemStack item = new ItemStack(Material.NETHERRACK);
			@Override
			public ItemStack getItem() {
				return item;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new DeleteQuestSelectorGui(clicker).getInventory());
			}
			private class DeleteQuestSelectorGui extends CustomMultiPageGui<CustomButton> {

				public DeleteQuestSelectorGui(Player p) {
					super(p,QuestsEditorGui.this,6,1);
					this.setTitle(null,StringUtils.fixColorsAndHolders("&cSelect Quest to delete"));
					for (Quest quest : getQuests()) {
						this.addButton(new SelectQuestButton(quest));
					}
					this.setFromEndCloseButtonPosition(8);
					this.reloadInventory();
				}
				private class SelectQuestButton extends CustomButton{
					private ItemStack item = new ItemStack(Material.BOOK);
					private Quest quest;
					
					public SelectQuestButton(Quest quest) {
						super(DeleteQuestSelectorGui.this);
						this.quest = quest;
						this.update();
					}
					@Override
					public ItemStack getItem() {
						return item;
					}
					public void update() {
						ArrayList<String> desc = new ArrayList<String>();
						desc.add("&6Quest: '&e"+quest.getDisplayName()+"&6'");
						desc.add("&7 contains &e"+quest.getMissions().size()+" &7missions");
						for (Mission mission : quest.getMissions()) {
							desc.add("&7 - &e"+mission.getDisplayName());
						}
						StringUtils.setDescription(item,desc);
					}
					@Override
					public void onClick(Player clicker, ClickType click) {
						clicker.openInventory(new DeleteConfirmationGui(clicker,getParent()).getInventory());
					}
					private class DeleteConfirmationGui extends CustomLinkedGui<CustomButton> {

						public DeleteConfirmationGui(Player p, CustomGui previusHolder) {
							super(p, previusHolder, 6);
							this.addButton(22,new ConfirmationButton());
							this.setFromEndCloseButtonPosition(8);
							this.setTitle(null,StringUtils.fixColorsAndHolders("&cConfirm Delete?"));
							reloadInventory();
						}
						
						private class ConfirmationButton extends CustomButton {
							private ItemStack item = new ItemStack(Material.WOOL);
							public ConfirmationButton() {
								super(DeleteConfirmationGui.this);
								this.item.setDurability((short) 14);
								ArrayList<String> desc = new ArrayList<String>();
								desc.add("&cClick to Confirm quest Delete");
								desc.add("&cQuest delete can't be undone");
								desc.add("");
								desc.add("&7Quest: '&e"+quest.getDisplayName()+"&6'");
								desc.add("&7 contains &e"+quest.getMissions().size()+" &7missions");
								for (Mission mission : quest.getMissions()) {
									desc.add("&7 - &e"+mission.getDisplayName());
								}
								StringUtils.setDescription(item,desc);
								
							}
							@Override
							public ItemStack getItem() {
								return item;
							}
							@Override
							public void onClick(Player clicker, ClickType click) {
								deleteQuest(quest);
								clicker.performCommand("questadmin editor");
							}
						}
					}
				}
			}
		}
	}
	private final static BaseComponent[] setDisplayNameDescription = new ComponentBuilder(
			ChatColor.GOLD+"Click suggest the command\n\n"+
			ChatColor.GOLD+"Set the display name for the quest\n"+
			ChatColor.YELLOW+"/questtext <display name>"
			).create();
}
