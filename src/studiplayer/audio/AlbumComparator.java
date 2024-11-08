package studiplayer.audio;

import java.util.Comparator;

public class AlbumComparator implements Comparator<AudioFile> {

	public int compare(AudioFile file1, AudioFile file2) {
		if (file1 instanceof TaggedFile) {
			if (file2 instanceof TaggedFile) {
				String album1 = ((TaggedFile) file1).getAlbum();
				String album2 = ((TaggedFile) file2).getAlbum();
				return album1.compareTo(album2);
			} else {
				return 1;
			}
		} else {
			if (file2 instanceof TaggedFile) {
				return -1;
			} else {
				return 0;
			}
		}
//		if (file1 instanceof TaggedFile && file2 instanceof TaggedFile) {
//			
//		} else if (file1 == null || file2 == null) {
//			throw new RuntimeException();
//		//TODO what should be first if 2 files are both NOT TaggedFile
//		} else if (!(file1 instanceof TaggedFile)) { // file1 > file2 has no album, muss am anfang stehen
//			return 1;
//		} else if (!(file2 instanceof TaggedFile)) { // file1 < file2 muss am anfang
//			return -1;
//		} else {
//			return 0;
//		}
	}
}
