package emanondev.quests.quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.Defaults;
import emanondev.quests.Quests;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomLinkedGui;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.gui.SubExplorerFactory;
import emanondev.quests.mission.Mission;
import emanondev.quests.require.QuestRequire;
import emanondev.quests.task.Task;
import emanondev.quests.utils.MemoryUtils;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadable;
import emanondev.quests.utils.YmlLoadableWithCooldown;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class Quest extends YmlLoadableWithCooldown{
	public static final String PATH_MISSIONS = "missions";
	public static final String PATH_REQUIRES = "requires";
	
	private final LinkedHashMap<String,Mission> missions = new LinkedHashMap<String,Mission>();
	
	@Override
	public void setDirty(boolean value) {
		super.setDirty(value);
		if (this.isDirty()==false) {
			for (Mission mission : missions.values())
				mission.setDirty(false);
			this.displayInfo.setDirty(false);
		}
		else {
			getParent().setDirty(true);
		}
	}
	private final QuestManager parent;
	public QuestManager getParent(){
		return parent;
	}
	private final List<QuestRequire> requires = new ArrayList<QuestRequire>();
	public List<QuestRequire> getRequires(){
		return Collections.unmodifiableList(requires);
	}
	public Mission getMissionByNameID(String key) {
		return missions.get(key);
	}
	public Collection<Mission> getMissions(){
		return Collections.unmodifiableCollection(missions.values());
	}
	public Quest(MemorySection m,QuestManager parent) {
		super(m);
		if (parent == null)
			throw new NullPointerException();
		this.parent = parent;
		LinkedHashMap<String,Mission> map = this.loadMissions((MemorySection) m.get(PATH_MISSIONS));
		if (map!=null)
			missions.putAll(map);
		for (Mission mission : missions.values()) {
			if (isDirty())
				break;
			if (mission.isDirty())
				setDirty(true);
		}
			
		List<QuestRequire> req= loadRequires(m);
		if (req!=null)
			requires.addAll(req);		
		displayInfo = loadDisplayInfo(m);
		if (displayInfo.isDirty())
			this.setDirty(true);
		
		this.addToEditor(0, new SubExplorerFactory<Mission>(Mission.class,getMissions(),
				"&8Missions List"));
		this.addToEditor(1, new AddMissionFactory());
		this.addToEditor(2, new DeleteMissionFactory());
	}
	
	private class AddMissionFactory implements EditorButtonFactory{
		private class AddMissionGuiItem extends CustomButton {
			private ItemStack item = new ItemStack(Material.GLOWSTONE);

			public AddMissionGuiItem(CustomGui parent) {
				super(parent);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&a&lAdd &6&lNew Mission"));
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(StringUtils.fixColorsAndHolders("&6Click to create new Mission"));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				String key = QuestManager.getNewMissionID(Quest.this);
				if (!addMission(key,"New Mission")) {
					//TODO
					return;
				}
				clicker.performCommand("questadmin quest "+Quest.this.getNameID()+" mission "+key+" editor");
			}
		}

		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			return new AddMissionGuiItem(parent);
		}
	}
	private class DeleteMissionFactory implements EditorButtonFactory {
		private class DeleteMissionButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.NETHERRACK);
			public DeleteMissionButton(CustomGui parent) {
				super(parent);
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&c&lDelete &6&lMission");
				desc.add("&6Click to select and delete a Mission");
				StringUtils.setDescription(item,desc);
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new DeleteMissionSelectorGui(clicker,getParent()).getInventory());
			}
			private class DeleteMissionSelectorGui extends CustomMultiPageGui<CustomButton> {

				public DeleteMissionSelectorGui(Player p,CustomGui previusHolder) {
					super(p,previusHolder,6,1);
					this.setTitle(null,StringUtils.fixColorsAndHolders("&cSelect Mission to delete"));
					for (Mission mission : getMissions()) {
						this.addButton(new SelectMissionButton(mission));
					}
					this.setFromEndCloseButtonPosition(8);
					this.reloadInventory();
				}
				private class SelectMissionButton extends CustomButton{
					private ItemStack item = new ItemStack(Material.BOOK);
					private Mission mission;
					
					public SelectMissionButton(Mission mission) {
						super(DeleteMissionSelectorGui.this);
						this.mission = mission;
						this.update();
					}
					@Override
					public ItemStack getItem() {
						return item;
					}
					public void update() {
						ArrayList<String> desc = new ArrayList<String>();
						desc.add("&6Mission: '&e"+mission.getDisplayName()+"&6'");
						desc.add("&7 contains &e"+mission.getTasks().size()+" &7tasks");
						for (Task task : mission.getTasks()) {
							desc.add("&7 - &e"+task.getDisplayName()+" &7("+task.getTaskType().getKey()+")");
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
							this.setTitle(null,StringUtils.fixColorsAndHolders("&cConfirm Delete?"));
							this.setFromEndCloseButtonPosition(8);
							reloadInventory();
						}
						
						private class ConfirmationButton extends CustomButton {
							private ItemStack item = new ItemStack(Material.WOOL);
							public ConfirmationButton() {
								super(DeleteConfirmationGui.this);
								this.item.setDurability((short) 14);
								ArrayList<String> desc = new ArrayList<String>();
								desc.add("&cClick to Confirm quest Delete");
								desc.add("&cMission delete can't be undone");
								desc.add("");
								desc.add("&6Mission: '&e"+mission.getDisplayName()+"&6'");
								desc.add("&7 contains &e"+mission.getTasks().size()+" &7tasks");
								for (Task task : mission.getTasks()) {
									desc.add("&7 - &e"+task.getDisplayName()+" &7("+task.getTaskType().getKey()+")");
								}
								StringUtils.setDescription(item,desc);
								
							}
							@Override
							public ItemStack getItem() {
								return item;
							}
							@Override
							public void onClick(Player clicker, ClickType click) {
								deleteMission(mission);
								clicker.performCommand("questadmin quest "+Quest.this.getNameID()+" editor");
							}
						}
					}
				}
			}
		}
	
		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			if (missions.size() >0)
				return new DeleteMissionButton(parent);
			return null;
		}
	}
	private final QuestDisplayInfo displayInfo;
	@Override
	public QuestDisplayInfo getDisplayInfo() {
		return displayInfo;
	}

	public BaseComponent[] toComponent() {
		ComponentBuilder comp = new ComponentBuilder(""+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"   Quest Info   "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----");
		comp.append("\n"+ChatColor.DARK_AQUA+"ID: "
				+ChatColor.AQUA+this.getNameID());
		comp.append("\n"+ChatColor.DARK_AQUA+"DisplayName: "
				+ChatColor.AQUA+this.getDisplayName());
		
		if (!this.isRepetable())
			comp.append("\n"+ChatColor.DARK_AQUA+"Repeatable: "+ChatColor.RED+"Disabled");
		else
			comp.append("\n"+ChatColor.DARK_AQUA+"Repeatable: "+ChatColor.GREEN+"Enabled");
		comp.append("\n"+ChatColor.DARK_AQUA+"Cooldown: "+ChatColor.YELLOW+(this.getCooldownTime()/60/1000)+" minutes");
		if (missions.size() > 0) {
			comp.append("\n"+ChatColor.DARK_AQUA+"Missions:");
			for (Mission mission : missions.values()) {
				comp.append("\n"+ChatColor.AQUA+" - "+mission.getNameID())
					.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
							"/qa quest "+this.getNameID()+" mission "
							+mission.getNameID()+ " info"))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder(ChatColor.YELLOW+"Click for details")
							.create()));
			}
		}
		if (this.getWorldsList().size()>0) {
			if (this.isWorldListBlacklist())
				comp.append("\n"+ChatColor.RED+"Blacklisted "+ChatColor.DARK_AQUA+"Worlds:");
			else
				comp.append("\n"+ChatColor.GREEN+"WhiteListed "+ChatColor.DARK_AQUA+"Worlds:");
			for (String world : this.getWorldsList())
				comp.append("\n"+ChatColor.AQUA+" - "+world)
				.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
						"/qa quest "+this.getNameID()+" worlds remove "+world))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder(ChatColor.YELLOW+"Click to remove")
						.create()));
		}
		if (requires.size() > 0) {
			comp.append("\n"+ChatColor.DARK_AQUA+"Requires:");
			for (QuestRequire require : requires) {
				comp.append("\n"+ChatColor.AQUA+" - "+require.toText());
				
			}
		}
		comp.append("\n"+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----"
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"[--"
				+ChatColor.BLUE+"   Quest Info   "
				+ChatColor.GRAY+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"--]"
				+ChatColor.BLUE+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"-----");
		return comp.create();
	}
	private QuestDisplayInfo loadDisplayInfo(MemorySection m) {
		return new QuestDisplayInfo(m,this);
	}
	
	private List<QuestRequire> loadRequires(MemorySection m) {
		List<String> l = MemoryUtils.getStringList(m, PATH_REQUIRES);
		return Quests.getInstance().getRequireManager().convertQuestRequires(l);
	}
	private LinkedHashMap<String, Mission> loadMissions(MemorySection m) {
		if (m == null)
			return new LinkedHashMap<String, Mission>();
		Set<String> s = m.getKeys(false);
		LinkedHashMap<String,Mission> map = new LinkedHashMap<String,Mission>();
		s.forEach((key)->{
			try {
				Mission mission = new Mission((MemorySection) m.get(key),this);
				map.put(mission.getNameID(), mission);
			} catch (Exception e) {
				e.printStackTrace();
				Quests.getInstance().getLoggerManager().getLogger("errors")
				.log("Error while loading Mission on file quests.yml '"
						+m.getCurrentPath()+"."+key+"' could not be read as valid mission"
						,ExceptionUtils.getStackTrace(e));
			}
		});
		return map;
	}
	
	@Override
	protected boolean getDefaultCooldownUse() {
		return Defaults.QuestDef.getDefaultCooldownUse();
	}
	@Override
	protected boolean shouldCooldownAutogen() {
		return Defaults.QuestDef.shouldCooldownAutogen();
	}
	@Override
	protected long getDefaultCooldownMinutes() {
		return Defaults.QuestDef.getDefaultCooldownMinutes();
	}
	@Override
	protected List<String> getWorldsListDefault() {
		return Defaults.QuestDef.getWorldsListDefault();
	}
	@Override
	protected boolean shouldWorldsAutogen() {
		return  Defaults.QuestDef.shouldWorldsAutogen();
	}
	@Override
	protected boolean getUseWorldsAsBlacklistDefault() {
		return  Defaults.QuestDef.getUseWorldsAsBlacklistDefault();
	}
	@Override
	protected boolean shouldAutogenDisplayName() {
		return Defaults.QuestDef.shouldAutogenDisplayName();
	}
	
	public boolean addMission(String id,String displayName) {
		if (id == null || id.isEmpty() || 
				id.contains(" ")||id.contains(".")||id.contains(":"))
			return false;
		if (displayName == null)
			displayName = id.replace("_"," ");
		id = id.toLowerCase();
		if (missions.containsKey(id))
			return false;
		getSection().set(PATH_MISSIONS+"."+id+"."+YmlLoadable.PATH_DISPLAY_NAME,displayName);
		Mission m = new Mission((MemorySection) getSection().get(PATH_MISSIONS+"."+id),this);
		missions.put(m.getNameID(), m);
		parent.save();
		parent.reload();
		Quests.getInstance().getPlayerManager().reload();
		return true;
	}

	public boolean deleteMission(Mission mission) {
		if (mission == null || !missions.containsKey(mission.getNameID()) )
			return false;
		getSection().set(PATH_MISSIONS+"."+mission.getNameID(),null);
		missions.remove(mission.getNameID());
		parent.save();
		parent.reload();
		Quests.getInstance().getPlayerManager().reload();
		return true;
	}
}
