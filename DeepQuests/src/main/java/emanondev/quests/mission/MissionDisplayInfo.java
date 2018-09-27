package emanondev.quests.mission;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.Defaults;
import emanondev.quests.H;
import emanondev.quests.Quests;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.player.QuestPlayer;
import emanondev.quests.task.Task;
import emanondev.quests.utils.DisplayState;
import emanondev.quests.utils.DisplayStateInfo;
import emanondev.quests.utils.StringUtils;

public class MissionDisplayInfo extends DisplayStateInfo {

	public MissionDisplayInfo(ConfigSection m, Mission mission) {
		super(m, mission);
		reloadDisplay();
		progressHolders = setupHolders();
	}

	public Mission getParent() {
		return (Mission) super.getParent();
	}

	@Override
	protected boolean shouldHideAutogen(DisplayState state) {
		return Defaults.MissionDef.shouldHideAutogen(state);
	}

	@Override
	protected boolean shouldItemAutogen(DisplayState state) {
		return Defaults.MissionDef.shouldItemAutogen(state);
	}

	@Override
	protected boolean shouldDescriptionAutogen(DisplayState state) {
		return Defaults.MissionDef.shouldDescriptionAutogen(state);
	}

	@Override
	protected ItemStack getDefaultItem(DisplayState state) {
		return Defaults.MissionDef.getDefaultItem(state);
	}

	@Override
	protected List<String> getDefaultDescription(DisplayState state) {
		List<String> lore = Defaults.MissionDef.getDefaultDescription(state);
		return lore;
	}

	@Override
	protected boolean getDefaultHide(DisplayState state) {
		return Defaults.MissionDef.getDefaultHide(state);
	}

	@Override
	public ItemStack getGuiItem(Player p, DisplayState state) {
		ItemStack item = getItem(state);
		QuestPlayer qPlayer = Quests.get().getPlayerManager().getQuestPlayer(p);
		StringUtils.setDescription(item, p, getDescription(state), getHolders(p, state));

		if (!qPlayer.getMissionData(getParent()).isPaused())
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

		return item;
	}

	public String[] getHolders(Player p, DisplayState state) {
		String[] s;
		if (state != DisplayState.COOLDOWN)
			s = new String[progressHolders.size() * 2];
		else {
			s = new String[progressHolders.size() * 2 + 2];
			s[s.length - 2] = H.MISSION_COOLDOWN_LEFT;
			s[s.length - 1] = StringUtils.getStringCooldown(
					Quests.get().getPlayerManager().getQuestPlayer(p).getCooldown(getParent()));
		}
		for (int i = 0; i < progressHolders.size(); i++) {
			s[i * 2] = progressHolders.get(i).getHolder();
			s[i * 2 + 1] = progressHolders.get(i).getReplacer(p);
		}
		return s;
	}

	private EnumMap<DisplayState, ArrayList<String>> finalDescriptions = new EnumMap<DisplayState, ArrayList<String>>(
			DisplayState.class);

	public ArrayList<String> getDescription(DisplayState state) {
		return new ArrayList<String>(finalDescriptions.get(state));
	}

	public ArrayList<String> getRawDescription(DisplayState state) {
		return super.getDescription(state);
	}
	
	/*public void setDescription(DisplayState state, List<String> desc) {
		
		super.setDescription(state, desc);
		infos.get(state).desc = desc;
		getSection().set(state.toString()+PATH_DESC, desc);
		setDirty(true);
	}*/
	
	public void reloadDisplay() {
		String[] holders = new String[getParent().getTasks().size() * 5 * 2 + 2];
		int k = 0;
		for (Task task : getParent().getTasks()) {
			holders[k * 10] = H.MISSION_GENERIC_TASK_PROGRESS_DESCRIPTION.replace("<task>", task.getID());
			holders[k * 10 + 1] = task.getProgressDescription();
			holders[k * 10 + 2] = H.MISSION_GENERIC_TASK_UNSTARTED_DESCRIPTION.replace("<task>", task.getID());
			holders[k * 10 + 3] = task.getUnstartedDescription();
			holders[k * 10 + 4] = H.MISSION_GENERIC_TASK_NAME.replace("<task>", task.getID());
			holders[k * 10 + 5] = task.getDisplayName();
			holders[k * 10 + 6] = H.MISSION_GENERIC_TASK_TYPE.replace("<task>", task.getID());
			holders[k * 10 + 7] = task.getTaskType().getKey();
			holders[k * 10 + 8] = H.MISSION_GENERIC_TASK_MAX_PROGRESS.replace("<task>", task.getID());
			holders[k * 10 + 9] = task.getMaxProgress() + "";
			k++;
		}
		holders[holders.length - 2] = H.MISSION_NAME;
		holders[holders.length - 1] = getParent().getDisplayName();
		for (DisplayState state : DisplayState.values()) {
			ArrayList<String> desc = super.getDescription(state);
			for (int i = 0; i < desc.size(); i++) {
				if (desc.get(i).startsWith(H.MISSION_FOREACH_TASK)) {
					String text = desc.get(i).replace(H.MISSION_FOREACH_TASK, "");
					desc.remove(i);
					int j = 0;
					for (Task task : getParent().getTasks()) {
						desc.add(i + j, text.replace("<task>", task.getID()));
						j++;
					}
					i = i + j - 1;
				}
			}
			finalDescriptions.put(state, StringUtils.fixColorsAndHolders(desc, holders));
		}
	}
	
	public void setDescription(DisplayState state, List<String> desc) {
		reloadDisplay();
		super.setDescription(state, desc);
	}

	protected ArrayList<ProgressHolder> setupHolders() {
		ArrayList<ProgressHolder> holders = new ArrayList<ProgressHolder>();
		for (Task task : getParent().getTasks())
			holders.add(new ProgressHolder(task));
		return holders;
	}

	private ArrayList<ProgressHolder> progressHolders;

	public class ProgressHolder {
		Task t;

		public ProgressHolder(Task task) {
			this.t = task;
		}

		public String getHolder() {
			return H.MISSION_GENERIC_TASK_PROGRESS.replace("<task>", t.getID());
		}

		public String getReplacer(Player p) {
			return "" + Quests.get().getPlayerManager().getQuestPlayer(p).getTaskProgress(t);
		}
	}

}
