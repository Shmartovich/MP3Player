package studiplayer.audio;
import java.io.File;
import java.util.Map;

import javax.sound.sampled.AudioSystem;

import studiplayer.basic.TagReader;

public class TaggedFile extends SampledFile {
	private String album = "";

	public TaggedFile() throws NotPlayableException{
		this("");
	}

	public TaggedFile(String path) throws NotPlayableException{
		super(path);
		readAndStoreTags();
		File file = new File(path);
		if (!file.exists()) {
			throw new NotPlayableException(path, "check the filepath, there is no such file");
		}
	}

	public void readAndStoreTags() throws NotPlayableException {

		try{
			Map<String, Object> tagMap = TagReader.readTags(getPathname());
			
			if (tagMap.get("album") != null) {
				album = ((String) tagMap.get("album")).trim();
			}
//			else {
//				throw new NotPlayableException(getPathname(),"album Tag couldnt be read");
//			}
			if (tagMap.get("title") != null) {
				setTitle(((String) tagMap.get("title")).trim());
			}
//			else {
//				throw new NotPlayableException(getPathname(),"title Tag couldnt be read");
//			}
			if (tagMap.get("author") != null) {
				setAuthor(((String) tagMap.get("author")).trim());
			}
//			else {
//				throw new NotPlayableException(getPathname(),"author Tag couldnt be read");
//			}
			if (tagMap.get("duration") != null) {
				duration = (Long) tagMap.get("duration");
			}
//			else {
//				throw new NotPlayableException(getPathname(),"duration Tag couldnt be read");
//			}
		}
		catch(RuntimeException e) {
			throw new NotPlayableException(getPathname(),"AudioSystem.getAudioFileFormat() wasnt successfull");
		}
	}

	public String toString() {
		if (!album.isBlank()) {
			return super.toString() + " - " + album + " - " + formatDuration();
		} else {
			return super.toString() + " - " + formatDuration();
		}
	}

	public String getAlbum() {
		return album;
	}

}
