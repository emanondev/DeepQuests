package emanondev.quests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;

import emanondev.quests.configuration.YMLConfig;

public class LoggerManager{
	
	
	private final YMLConfig data = new YMLConfig(Quests.get(), "loggerConfig");
	
	private String defaultDateFormat;

	public LoggerManager() {
		reload();
	}
	
	public String getDefaultDateFormat() {
		return defaultDateFormat;
	}
	
	public boolean reload() {
		boolean result = data.reload();
		if (result == true) {
			String format = data.getString("defaultDateFormat");
			if (format== null || format.isEmpty()) {
				data.set("defaultDateFormat","[dd.MM.yyyy HH:mm:ss] ");
				format = "[dd.MM.yyyy HH:mm:ss] ";
				data.save();
			}
			this.defaultDateFormat = format;
			reloadLoggers();
		}
		
		return result;
	}
	private void reloadLoggers() {
		loggers.values().forEach((logger)->logger.reload());
	}
	private WeakHashMap<String,Logger> loggers = new WeakHashMap<String,Logger>();

	private void registerLogger(Logger logger, String fileName) {
		loggers.put(fileName,logger);
	}

	/**
	 * 
	 * @param fileName - where the logger is supposed to log
	 * @return logger - from the cash else restart the logger
	 */
	public Logger getLogger(String fileName) {
		if (fileName== null)
			throw new NullPointerException();
		if (fileName.equals("")||fileName.equals(".log"))
			throw new IllegalArgumentException();
		if (!fileName.endsWith(".log"))
			fileName = fileName+ ".log";
		fileName = fileName.toLowerCase();
		Logger result = loggers.get(fileName);
		if (result == null) {
			result = new Logger(fileName);
			registerLogger(result,fileName);
		}
		return result;
	}
	
	public class Logger {
		private final File file;
		private DateFormat dateFormat;
		private final String path;

		/**
		 * 
		 * @param fileName - the name of the file/path associated to the logger <br>
		 * it may ends with ".log" else ".log" will be added<br>
		 * to choose a file in a sub folder use "/" in the name<br>example "myfolder/mysubfolder/mylogger.log"
		 */
		public Logger(String fileName) {
			
			if (Quests.get()==null||fileName==null)
				throw new NullPointerException();
			if (fileName.equals("")||fileName.equals(".log"))
				throw new IllegalArgumentException();
			if (!fileName.endsWith(".log"))
				fileName = fileName+ ".log";
			fileName = fileName.toLowerCase();
		    file = new File(Quests.get().getDataFolder() , fileName);
		    if(!file.exists()){
		    	if(!file.getParentFile().exists()) { // Create parent folders if they don't exist
	                file.getParentFile().mkdirs();
	            }
		        try {
		            file.createNewFile();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		    
		    path = "loggers."+fileName.replace(" ","_").replace(".","_").replace(":","_");
			if (data.isString(path+".dateformat")) {
				dateFormat = new SimpleDateFormat(data.getString(path+".dateformat"),Locale.ITALY);
			} else {
				dateFormat = new SimpleDateFormat(getDefaultDateFormat(),Locale.ITALY);
				data.set(path+".dateformat",getDefaultDateFormat());
				data.save();
			}
		}
		/**
		 * reload the config for this logger
		 */
		public void reload() {
			if (data.isString(path+".dateformat")) {
				dateFormat = new SimpleDateFormat(data.getString(path+".dateformat"),Locale.ITALY);
			} else {
				dateFormat = new SimpleDateFormat(data.getString("dateformat"),Locale.ITALY);
				data.set(path+".dateformat", data.getString("dateformat"));
			}
		}
		/**
		 * 
		 * @param message <br>
		 *
		 * adds this message to a new line of the file
		 */
		public void log(String message){
			String date = dateFormat.format(new Date());
			Bukkit.getScheduler().runTaskAsynchronously(Quests.get(), new Runnable() {
				@Override
				public void run() {
					try {
				        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
						bw.append(date+ message+"\n");
				        bw.close();
				    } catch (IOException e) {
				        e.printStackTrace();
				    }
				}
			});
		    
		}
		/**
		 * 
		 * @param messages <br>
		 * 
		 * adds this messages to the file
		 */
		public void log(List<String> messages){
			if (messages==null)
				return;
			String date = dateFormat.format(new Date());
			Bukkit.getScheduler().runTaskAsynchronously(Quests.get(), new Runnable() {
				@Override
				public void run() {
				    try {
				        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
				        for (int i=0; i<messages.size(); i++)
				        	bw.append(date+ messages.get(i)+"\n");
				        bw.close();
				    } catch (IOException e) {
				        e.printStackTrace();
				    }
				}
			});
		}
		/**
		 * 
		 * @param messages <br>
		 * 
		 * adds this messages to the file
		 */
		public void log(String... messages){
			if (messages==null)
				return;
			String date = dateFormat.format(new Date());
			Bukkit.getScheduler().runTaskAsynchronously(Quests.get(), new Runnable() {
				@Override
				public void run() {
				    try {
				        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
				        for (int i=0; i<messages.length; i++)
				        	bw.append(date+ messages[i]+"\n");
				        bw.close();
				    } catch (IOException e) {
				        e.printStackTrace();
				    }
				}
			});
		}
	}

}
