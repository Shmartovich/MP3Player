package studiplayer.audio;

public class AudioFileFactory {
	public static AudioFile createAudioFile(String path) throws NotPlayableException {
		// M3U format hat path und dementsprechend extension
		int indexBeginnExtension = path.lastIndexOf(".") + 1;
		String extension;
		if (indexBeginnExtension != -1) {
			extension = path.substring(indexBeginnExtension).toLowerCase();
		} else {
			extension = "";
		}
		switch (extension) {
		case "wav":
			return new WavFile(path);
		case "ogg":
			return new TaggedFile(path);
		case "mp3":
			return new TaggedFile(path);
		default:
			throw new NotPlayableException(path, "album Tag couldnt be read");
		}
	}
}
