package emanondev.quests;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;


public class YMLConfig extends YamlConfiguration
{
    private JavaPlugin pl;
    private File file;
 
    public YMLConfig(JavaPlugin pl, String name) {
        this.pl = pl;
        if(name == null || name.isEmpty()) throw new IllegalArgumentException("YAML file must have a name!");
        if(!name.endsWith(".yml")) 
        	name += ".yml";
        file = new File(pl.getDataFolder(), name);
        reload();
    }
 
    /** Reload config object in RAM to that of the file - 
     * useful if developer wants changes made to config yml to be brought in 
     * 
     * @return true if reload was successfull -
     * false if configuration errors occurred
    */
    public boolean reload() {
        //boolean existed = file.exists();
        if(!file.exists()) {

            if(!file.getParentFile().exists()) { // Create parent folders if they don't exist
                file.getParentFile().mkdirs();
            }
            if(pl.getResource(file.getName()) != null) {
                pl.saveResource(file.getName(), true); // Save the one from the JAR if possible
            }
            else {
                try { file.createNewFile(); } // Create a blank file if there's not one to copy from the JAR
                	catch (IOException e) { e.printStackTrace(); 
                }
            }
        }
        try { this.load(file); }
        catch (InvalidConfigurationException e) {
        	e.printStackTrace();
        	return false;
        }
        catch (Exception e) { e.printStackTrace(); }
        if(pl.getResource(file.getName()) != null) { // Set up defaults in case their config is broked.
            InputStreamReader defConfigStream = null;
            try {
                defConfigStream = new InputStreamReader(pl.getResource(file.getName()), "UTF-8");
            } catch (UnsupportedEncodingException e) { e.printStackTrace(); }
            this.setDefaults(YamlConfiguration.loadConfiguration(defConfigStream));
        }
        return true;
    }
 
    /** Save the config object in RAM to the file 
     * - overwrites any changes that the configurator has made to the file unless reload()
     * has been called since
     */
    public void save() {
        try    { this.save(file); }
        catch (Exception e)    { e.printStackTrace(); }
    }
 
    public File getFile() {
        return file;
    }
    public BaseComponent[] getBaseComponents(String path){
    	List<String> texts = getStringList(path);
		if (texts==null||texts.isEmpty())
			return null;
    	return translateComponent(texts);
    }
	public static BaseComponent[] translateComponent(List<String> texts){
		if (texts==null||texts.isEmpty())
			return null;
		ComponentBuilder base = null;
		for (int i = 0; i < texts.size(); i++) {
			String current = color(texts.get(i));
			int end = getNextComponent(current);
			if (base==null)
				base = new ComponentBuilder(current.substring(0,end));
			else
				base.append(current.substring(0,end));
			if (end==current.length())
				continue;
			while (end<current.length()){
				end = end + addNextComponent(base,current.substring(end));
			}
		}
		

		return base.create();
	}
	private static int addNextComponent(ComponentBuilder base, String current) {
		if (current.startsWith(HOVER_TEXT)){
			int stop = HOVER_TEXT.length()+getNextComponent(current
					.substring(HOVER_TEXT.length()));
			base.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder(current.substring(HOVER_TEXT.length(),stop)).create()));
			return stop;
		}
		if (current.startsWith(OPEN_URL)){
			int stop = OPEN_URL.length()+getNextComponent(current
					.substring(OPEN_URL.length()));
			base.event(new ClickEvent(ClickEvent.Action.OPEN_URL,
					current.substring(OPEN_URL.length(),stop)));
			return stop;
		}
		if (current.startsWith(RUN_COMMAND)){
			int stop = RUN_COMMAND.length()+getNextComponent(current
					.substring(RUN_COMMAND.length()));
			base.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
					current.substring(RUN_COMMAND.length(),stop)));
			return stop;
		}
		if (current.startsWith(SUGGEST_COMMAND)){
			int stop = SUGGEST_COMMAND.length()+getNextComponent(current
					.substring(SUGGEST_COMMAND.length()));
			base.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
					current.substring(SUGGEST_COMMAND.length(),stop)));
			return stop;
		}
		return 0;
	}


	private static int getNextComponent(String text){
		int min = text.length();
		if (text.contains(HOVER_TEXT)){
			min = Math.min(min, text.indexOf(HOVER_TEXT));
		}
		if (text.contains(OPEN_URL)){
			min = Math.min(min, text.indexOf(OPEN_URL));
		}
		if (text.contains(RUN_COMMAND)){
			min = Math.min(min, text.indexOf(RUN_COMMAND));
		}
		if (text.contains(SUGGEST_COMMAND)){
			min = Math.min(min, text.indexOf(SUGGEST_COMMAND));
		}
		return min;
	}
	private static String color(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	private final static String HOVER_TEXT = " %HOVER_TEXT%";
	private final static String OPEN_URL = " %OPEN_URL%";
	private final static String RUN_COMMAND = " %RUN_COMMAND%";
	private final static String SUGGEST_COMMAND = " %SUGGEST_COMMAND%";
}