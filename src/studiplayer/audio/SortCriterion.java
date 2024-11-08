package studiplayer.audio;


public enum SortCriterion {
	DEFAULT,
	AUTHOR,
	TITLE,
	ALBUM,
	DURATION;
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}