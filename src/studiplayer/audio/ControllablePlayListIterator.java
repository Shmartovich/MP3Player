package studiplayer.audio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ControllablePlayListIterator implements Iterator<AudioFile> {
	private List<AudioFile> list;
	private int counter = 0;

	@Override
	public boolean hasNext() {
		if (counter <= list.size() - 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public AudioFile next() {
		return list.get(counter++);
	}

	public ControllablePlayListIterator(List<AudioFile> list) {
		this(list, "", SortCriterion.DEFAULT);
	}

	public ControllablePlayListIterator(List<AudioFile> list, String search, SortCriterion sort) {
		search = search.toLowerCase();
		if (search == null || search.isEmpty()) {
			this.list = new ArrayList<AudioFile>();
			this.list.addAll(list);
		} else {
			this.list = new ArrayList<AudioFile>();
			// Autor, Titel oder Album has String in ~search~
			for (AudioFile file : list) {
				if (file.getAuthor().toLowerCase().contains(search) || file.getTitle().toLowerCase().contains(search)) {
					this.list.add(file);
					continue;
				}
				if (file instanceof TaggedFile) {
					TaggedFile fileWithAlbum = ((TaggedFile) file);
					if (fileWithAlbum.getAlbum().toLowerCase().contains(search)) {
						this.list.add(file);
					}
				}
			}
		}
		// jetzt Sortieren der erstellten Liste
		switch (sort) {
		case AUTHOR:
			this.list.sort(new AuthorComparator());
			break;
		case TITLE:
			this.list.sort(new TitleComparator());
			break;
		case ALBUM:
			this.list.sort(new AlbumComparator());
			break;
		case DURATION:
			this.list.sort(new DurationComparator());
			break;
		default:
			break;
		}
	}

	public AudioFile jumpToAudioFile(AudioFile file) {
		if (list.contains(file)) {
			counter = list.indexOf(file) + 1;
			return list.get(list.indexOf(file));
		} else {
			System.out.println("no such file to jump into");
			return null;
		}
	}

	public List<AudioFile> getList() {
		return list;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}
