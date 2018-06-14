package mjaroslav.progs.mcws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class VersionManager {
	private static File folder;
	private static Map<String, File> versions = new HashMap<String, File>();

	public static boolean init() {
		try {
			folder = new File(VersionManager.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParentFile().toPath().resolve(Info.FORGEFOLDER).toFile();
			if (folder.exists() && folder.isDirectory()) {
				for (File file : folder.listFiles())
					if (file.getName().toLowerCase().endsWith(".zip"))
						versions.put(file.getName().substring(0, file.getName().length() - 4), file);
				return true;
			}
		} catch (URISyntaxException e) {
		}
		return false;
	}

	public static List<String> list() {
		return new ArrayList<String>(versions.keySet());
	}

	public static boolean hasVersion(String version) {
		return versions.containsKey(version);
	}

	public static void clone(File folder, String version) throws IOException {
		unZip(versions.get(version), folder);
	}

	private static final int BUFFER_SIZE = 1024;

	private static void unZip(File zip, File dstDirectory) {
		byte[] buffer = new byte[BUFFER_SIZE];
		try {
			final ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));
			ZipEntry ze = zis.getNextEntry();
			String nextFileName;
			while (ze != null) {
				nextFileName = ze.getName();
				File nextFile = new File(dstDirectory + File.separator + nextFileName);
				Info.LOG.info("UnZip: " + nextFile.getAbsolutePath());
				if (ze.isDirectory())
					nextFile.mkdir();
				else {
					new File(nextFile.getParent()).mkdirs();
					try (FileOutputStream fos = new FileOutputStream(nextFile)) {
						int length;
						while ((length = zis.read(buffer)) > 0)
							fos.write(buffer, 0, length);
					}
				}
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		} catch (IOException ex) {
			Info.LOG.fatal(ex);
		}
	}
}
