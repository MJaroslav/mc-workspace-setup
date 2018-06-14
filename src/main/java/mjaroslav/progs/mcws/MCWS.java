package mjaroslav.progs.mcws;

import static mjaroslav.progs.mcws.Info.LOG;

import java.io.File;

public class MCWS {
	public static void main(String... args) {
		if (args.length > 1) {
			File currentFolder = new File(".");
			if (currentFolder.exists() && currentFolder.isDirectory()) {
				if (VersionManager.init()) {
					String arg = args[1];
					switch (arg) {
					case "help":
						help();
						break;
					case "list":
						list();
						break;
					case "init":
						init(currentFolder, args);
						break;
					default:
						LOG.error("Command not found! Use 'help' for command list.");
						break;
					}
				} else
					LOG.error("There are no forge devs, put them in the 'forge' folder.");
			} else
				LOG.error("Current directory does not exist or is invalid.");
		} else
			help();
	}

	private static void help() {
		LOG.info(Info.NAME);
		LOG.info("Utility for quick installation Minecraft working environment.");
		LOG.info("======");
		LOG.info("Allowed commands:");
		LOG.info("help - call this menu.");
		LOG.info("list - list the versions of forge.");
		LOG.info("init %VERSION% - install the selected version of forge.");
	}

	private static void list() {
		LOG.info("Allowed versions:");
		for (String version : VersionManager.list())
			LOG.info(version);
	}

	private static void init(File folder, String... args) {
		if (args.length > 2) {
			String version = args[2];
			if (VersionManager.hasVersion(version)) {
				try {
					LOG.info("Copy the version to the current folder...");
					VersionManager.clone(folder, version);
					LOG.info("Start 'gradlew.bat' with 'setupDevWorkspace eclipse'...");
					Runtime.getRuntime().exec("cmd /c start \"" + Info.NAME + "\" \""
							+ folder.toPath().resolve("gradlew.bat").toString() + "\" setupDevWorkspace eclipse", null,
							folder);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
				LOG.error("Version not found! Use 'list' to view the version list.");
		} else
			LOG.error("Specify the version! Use 'list' to view the version list.");
	}
}
