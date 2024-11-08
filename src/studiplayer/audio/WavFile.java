package studiplayer.audio;
import studiplayer.basic.WavParamReader;

public class WavFile extends SampledFile {

	public static long computeDuration(long numberOfFrames, float frameRate) {
		long result = (long) (numberOfFrames * 1000000 / frameRate); 

		return result;
	}

	public WavFile() throws NotPlayableException{
		this("");
	}
	public WavFile(String path) throws NotPlayableException{
		super(path);
		readAndSetDurationFromFile();
	}

	public void readAndSetDurationFromFile() throws NotPlayableException {
		try{
			WavParamReader.readParams(getPathname());
			duration = computeDuration(WavParamReader.getNumberOfFrames(), WavParamReader.getFrameRate());
		}
		catch(RuntimeException exception) {
			throw new NotPlayableException(getPathname(),exception);
		}
	}

	public String toString() {
		return super.toString() + " - " + formatDuration();
	}

}
