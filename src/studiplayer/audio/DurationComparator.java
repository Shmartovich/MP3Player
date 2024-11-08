package studiplayer.audio;

import java.util.Comparator;

public class DurationComparator implements Comparator<AudioFile> {

	@Override
	public int compare(AudioFile file1, AudioFile file2) {
		long duration1 = -1;
		long duration2 = -1;
		if (file1 instanceof SampledFile) {
			duration1 = ((SampledFile) file1).getDuration();
		}
		if (file2 instanceof SampledFile) {
			duration2 = ((SampledFile) file2).getDuration();
		}

		if(duration1 > duration2) {
			return 1;
		}
		else if(duration1 < duration2) {
			return -1;
		}
		else{
			return 0;
		}
	}

}
