package studiplayer.audio;
import java.util.Comparator;

public class AuthorComparator implements Comparator<AudioFile>{

	@Override
	public int compare(AudioFile file1, AudioFile file2) {
		if(file1 == null || file2 == null) {
			throw new RuntimeException();
		}
		return (file1.getAuthor().compareTo(file2.getAuthor()));
	}

}
