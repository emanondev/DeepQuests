package emanondev.quests.citizenbinds;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.configuration.YMLConfig;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomLinkedGui;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.newgui.gui.MapGui;
import emanondev.quests.newgui.gui.MissionsMenu;
import emanondev.quests.quest.Quest;
import emanondev.quests.utils.StringUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.md_5.bungee.api.ChatColor;

public class CitizenBindManager implements Listener {
	
	private YMLConfig data = new YMLConfig(Quests.get(),"CitizenGuiBinds");
	private HashMap<NPC,Quest> map = new HashMap<>();
	private final static String PATH_NPC_ID = "npc-id";
	private final static String PATH_QUEST_ID = "quest-id";
	
	public void openEditor(Player player) {
		player.openInventory(new CitizenBindEditor(player).getInventory());
	}
	public void reload() {
		data.reload();
		loadMap();
	}
	private void loadMap() {
		map.clear();
		ConfigSection section = (ConfigSection) data.get(PATH_NPC_ID);
		if (section==null) {
			data.createSection(PATH_NPC_ID);
			return;
		}
		for(String key: section.getKeys(false)) {
			NPCRegistry registry = CitizensAPI.getNPCRegistry();
			try {
				int id = Integer.valueOf(key);
				Quest quest = Quests.get().getQuestManager().getQuestByID(
						section.getString(key+"."+PATH_QUEST_ID));
				if (quest==null) 
					throw new IllegalArgumentException("Invalid quest id "+section.getString(key+"."+PATH_QUEST_ID));
				NPC npc = registry.getById(id);
				if (npc==null)
					throw new IllegalArgumentException("Citizen with id "+id+" not found");
				map.put(npc,quest);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
	private void event(NPCRightClickEvent evt) {
		if (map.containsKey(evt.getNPC())) {
			evt.getClicker().openInventory(new MissionsMenu(evt.getClicker(),null,
					map.get(evt.getNPC())).getInventory());
			evt.getClicker().playSound(evt.getClicker().getLocation(), Sound.ENTITY_VILLAGER_TRADE, 1, 1);
		}
	}
	
	public void addBind(NPC npc,Quest quest) {
		if (npc==null || quest == null)
			throw new NullPointerException();
		data.set(PATH_NPC_ID+"."+npc.getId()+"."+PATH_QUEST_ID,quest.getID());
		map.put(npc,quest);
		data.save();
	}
	public void removeBind(NPC npc) {
		if (npc == null)
			throw new NullPointerException();
		data.set(PATH_NPC_ID+"."+npc.getId(),null);
		map.remove(npc);
		data.save();
	}
	private class CitizenBindEditor extends CustomLinkedGui<CustomButton> {
		public CitizenBindEditor(Player p) {
			super(p, null, 6);
			this.setTitle(null,ChatColor.BLUE+"Citizen Binds");
			this.addButton(11,new AddBindButton());
			this.addButton(15,new DeleteBindButton());
			reloadInventory();
		}
		
		private class AddBindButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.GLOWSTONE);
			public AddBindButton() {
				super(CitizenBindEditor.this);
				ArrayList<String> desc = new ArrayList<String>();
				desc.add(ChatColor.GOLD+"Add a new Bind NPC - Quest");
				StringUtils.setDescription(item,desc);
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new AddBindGui(clicker).getInventory());
			}
			private class AddBindGui extends CustomMultiPageGui<CustomButton> {
				public AddBindGui(Player p) {
					super(p, CitizenBindEditor.this, 6, 1);
					this.setTitle(null,ChatColor.BLUE+"Add a new Bind");
					for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
						if(!map.containsKey(npc))
							this.addButton(new NPCButton(npc));
					}
					reloadInventory();
				}
				private class NPCButton extends CustomButton {
					private ItemStack item = new ItemStack(Material.WHITE_WOOL);
					private final NPC npc;
					public NPCButton(NPC npc) {
						super(AddBindGui.this);
						this.npc = npc;
						ArrayList<String> desc = new ArrayList<String>();
						desc.add(ChatColor.GOLD+"NPC "+npc.getId());
						desc.add(ChatColor.GOLD+"Name "+npc.getFullName());
						StringUtils.setDescription(item,desc);
					}
					@Override
					public ItemStack getItem() {
						return item;
					}
					@Override
					public void onClick(Player clicker, ClickType click) {
						clicker.openInventory(new QuestSelectorGui(clicker).getInventory());
					}
					private class QuestSelectorGui extends CustomMultiPageGui<CustomButton> {
						public QuestSelectorGui(Player p) {
							super(p, AddBindGui.this, 6, 1);
							for (Quest quest : Quests.get().getQuestManager().getQuests()) {
								this.addButton(new QuestButton(quest));
							}
							reloadInventory();
						}
						
						private class QuestButton extends CustomButton {
							private ItemStack item = new ItemStack(Material.WHITE_WOOL);
							private final Quest quest;
							public QuestButton(Quest quest) {
								super(AddBindGui.this);
								this.quest = quest;
								ArrayList<String> desc = new ArrayList<String>();
								desc.add(ChatColor.GOLD+"Quest "+quest.getDisplayName());
								StringUtils.setDescription(item,desc);
							}
							@Override
							public ItemStack getItem() {
								return item;
							}
							@Override
							public void onClick(Player clicker, ClickType click) {
								addBind(npc,quest);
								clicker.openInventory(CitizenBindEditor.this.getInventory());
							}
						}
					}
				}
			}
			
		}
		
		private class DeleteBindButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.NETHERRACK);
			public DeleteBindButton() {
				super(CitizenBindEditor.this);
				ArrayList<String> desc = new ArrayList<String>();
				desc.add(ChatColor.RED+"Delete Bind NPC - Quest");
				StringUtils.setDescription(item,desc);
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new RemoveBindGui(clicker).getInventory());
			}
			private class RemoveBindGui extends CustomMultiPageGui<CustomButton> {

				public RemoveBindGui(Player p) {
					super(p, CitizenBindEditor.this, 6, 1);
					for (NPC npc : map.keySet())
						this.addButton(new BindButton(npc));
					this.setTitle(null,ChatColor.RED+"Remove Citizen Bind Gui");
					reloadInventory();
				}
				
				private class BindButton extends CustomButton {
					private ItemStack item = new ItemStack(Material.WHITE_WOOL);
					private final NPC npc;
					public BindButton(NPC npc) {
						super(RemoveBindGui.this);
						this.npc = npc;
						ArrayList<String> desc = new ArrayList<String>();
						desc.add(ChatColor.GOLD+"NPC "+npc.getId());
						desc.add(ChatColor.GOLD+"Name "+npc.getFullName());
						desc.add(ChatColor.GOLD+"Quest "+map.get(npc).getDisplayName());
						StringUtils.setDescription(item,desc);
						reloadInventory();
					}
					@Override
					public ItemStack getItem() {
						return item;
					}
					@Override
					public void onClick(Player clicker, ClickType click) {
						removeBind(npc);
						clicker.openInventory(CitizenBindEditor.this.getInventory());
					}
				}
				
			}
			
		}
		
	}
	private class CitizenNPCBindEditor extends MapGui {

		public CitizenNPCBindEditor(Player p, Gui previusHolder) {
			super("Citizen Binds", 6, p, previusHolder);
			// TODO Auto-generated constructor stub
		}
		
	}
}
