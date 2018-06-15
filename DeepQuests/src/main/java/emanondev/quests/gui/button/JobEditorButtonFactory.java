package emanondev.quests.gui.button;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;

public abstract class JobEditorButtonFactory extends AbstractSelectorButtonFactory<Job> {

	public JobEditorButtonFactory() {
		super("Job Editor");
	}

	@Override
	protected ArrayList<String> getButtonDescription() {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&6&lJob editor button");
		desc.add("&6click to edit selected job");
		if (getObject() == null)
			desc.add("&cNo job has been selected yet");
		else
			desc.add("&7Current selected job is &a"+getObject().getName());
		return desc;
	}
	private final static ItemStack DEFAULT_BUTTON = craftDefaultButton();
	private static ItemStack craftDefaultButton() {
		ItemStack item = new ItemStack(Material.IRON_PICKAXE);
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}
	@Override
	protected ItemStack getButtonItemStack() {
		Job job = getObject();
		if (job==null)
			return DEFAULT_BUTTON;
		return job.getGuiItem();
	}

	@Override
	protected ArrayList<String> getObjectButtonDescription(Job job) {
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("&6Click to select Job "+job.getName());
		return desc;
	}

	@Override
	protected ItemStack getObjectItemStack(Job job) {
		if (job==null)
			return DEFAULT_BUTTON;
		return job.getGuiItem();
	}

	@Override
	public Collection<Job> getCollection() {
		return Jobs.getJobs();
	}
}
