package studiplayer.audio;

public abstract class AudioFile {
	private String pathname;
	private String filename;
	private String author;
	private String title;

	public String toString() {
		if (this.author == "") {
			return title;
		} else {
			return author + " - " + title;
		}
	}

	public AudioFile(String path) throws NotPlayableException {
		parsePathname(path);
		parseFilename(filename);
	}

	public AudioFile() throws NotPlayableException {
		this("");
	}

	public abstract void play() throws NotPlayableException;

	public abstract void togglePause();

	public abstract void stop();

	public abstract String formatDuration();

	public abstract String formatPosition();

	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
	}

	public void parsePathname(String inputPath) {
		inputPath = inputPath.trim();

		// windows
		if (isWindows()) {
			pathname = inputPath.replaceAll("/*/", "\\\\"); // removes wrong slashes!
			pathname = pathname.replaceAll("\\\\*\\\\", "\\\\"); // removes duplicates! not elegant, ändert auch
																	// richtige slashes was nicht nötig ist

			// find filename
			int indexOfLastSlash = pathname.lastIndexOf('\\');
			if (indexOfLastSlash != -1) {
				filename = pathname.substring(indexOfLastSlash + 1);
			} else {
				filename = pathname;
			}
		}
		// LINUX
		else {
			String correctPathName = inputPath.replaceAll("\\\\*\\\\", "/").replaceAll("/*//", "/"); // removes

			// muss WINDOWS spezifisches Ding umwandeln
			int doppeltPunktIndex = correctPathName.indexOf(":");
			int firtSlashIndex = correctPathName.indexOf("/");
			if (doppeltPunktIndex != -1) {
				if (doppeltPunktIndex < firtSlashIndex) {
					// gehe davon aus d:fdasf/fsd/fsd/
					pathname = "/" + correctPathName.charAt(doppeltPunktIndex - 1) + "/"; // vorischt
				}
				pathname += correctPathName.substring(3); // LOL
			} else {
				pathname = correctPathName;
			}

			// find filename
			int indexOfLastSlash = pathname.lastIndexOf('/');

			if (indexOfLastSlash != -1) {
				if (indexOfLastSlash + 1 >= pathname.length()) { // out of bounds check
					filename = "";
				} else {
					filename = pathname.substring(indexOfLastSlash + 1).trim();
				}
			} else {
				filename = correctPathName; // also nicht path, ein filename
			}

		}

	}

	public void parseFilename(String filename) {
		int strichIndex = filename.indexOf(" - ");
		int punktIndex = filename.lastIndexOf('.');

		if (strichIndex != -1) {
			author = filename.substring(0, strichIndex).trim();
			if (punktIndex != -1) {
				title = filename.substring(strichIndex + 3, punktIndex).trim();
			} else {
				title = filename.substring(strichIndex + 3).trim();
			}
		} else {
			// no strich
			author = "";
			if (punktIndex != -1) {
				title = filename.substring(0, punktIndex).trim();
			} else {
				title = filename.substring(0).trim();
			}
		}
	}

	public String getPathname() {
		return pathname;
	}

	public String getFilename() {
		return filename;
	}

	public String getAuthor() {
		return author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

}
