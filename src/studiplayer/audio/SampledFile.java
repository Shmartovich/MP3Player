package studiplayer.audio;

import java.io.File;

import studiplayer.basic.BasicPlayer;

public abstract class SampledFile extends AudioFile {
	protected long duration;

	public static String timeFormatter(long timeInMicroSeconds) {
		if (timeInMicroSeconds < 0) {
			throw new RuntimeException("negative duration");
		}

		int seconds = (int) ( (double)timeInMicroSeconds / 1000000);
		int minutes = (int) (seconds / 60);
		if (minutes > 99) {
			throw new RuntimeException("more than 99 minutes");
		}
		seconds = seconds % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}

	public SampledFile() throws NotPlayableException {
		this("");
		
	}

	public SampledFile(String path) throws NotPlayableException {
		super(path);
		File file = new File(path);
		if (!file.exists()) {
			throw new NotPlayableException(path, "check the filepath, there is no such file");
		}
	}

	@Override

	public void play() throws NotPlayableException{
		try {
			BasicPlayer.play(getPathname());
		} catch (RuntimeException runEx) {
			throw new NotPlayableException(getPathname(), runEx);
		}
	}

	@Override
	public void togglePause() {
		BasicPlayer.togglePause();
	}

	@Override
	public void stop() {
		BasicPlayer.stop();
	}

	@Override
	public String formatDuration() {
		return timeFormatter(getDuration());
	}

	@Override
	public String formatPosition() {
		// aktueller Zeitpunkt des Songs
		return timeFormatter(BasicPlayer.getPosition());
	}

	public long getDuration() {
		return duration;
	}
}
