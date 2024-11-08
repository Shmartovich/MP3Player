package studiplayer.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PlayList implements Iterable<AudioFile> {
	private LinkedList<AudioFile> listOfSongs = new LinkedList<AudioFile>();
	// iterator als attribut soll hier
	private ControllablePlayListIterator iterator;
	private String search = "";
	private SortCriterion sortCriterion = SortCriterion.DEFAULT;

	public PlayList() {
		iterator = (ControllablePlayListIterator) iterator();
	}

	public PlayList(String m3uPathName) {
		loadFromM3U(m3uPathName);
		iterator = (ControllablePlayListIterator) iterator();
	}

	public void add(AudioFile file) {
		listOfSongs.add(file); // TODO was wenn mehrere gleiche files. Ist das Problem
		iterator = (ControllablePlayListIterator) iterator();
	}

	public void remove(AudioFile file) {
		listOfSongs.remove(file);
		iterator = (ControllablePlayListIterator) iterator();
	}

	public int size() {
		return listOfSongs.size();
	}

	public SortCriterion getSortCriterion() {
		return sortCriterion;
	}

	public void setSortCriterion(SortCriterion sort) {
		this.sortCriterion = sort;
		iterator = (ControllablePlayListIterator) iterator();
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String value) {
		search = value;
		iterator = (ControllablePlayListIterator) iterator();
	}

	public AudioFile currentAudioFile() {
		try {
			return iterator.getList().get(iterator.getCounter());
		} catch (IndexOutOfBoundsException e) {
			e.getStackTrace();
			return null;
		}
	}

	public void nextSong() {
		try {
			iterator.getList().get(iterator.getCounter() + 1);
			// if no IndexOutOfBoundsExc:
			iterator.setCounter(iterator.getCounter() + 1);
		} catch (IndexOutOfBoundsException e) {
			e.getStackTrace();
			iterator.setCounter(0);
		}
	}

	public void loadFromM3U(String m3uPathname) {
		Scanner scanner = null;

		try {
			// TODO ist catch am richtigen platz?
			scanner = new Scanner(new File(m3uPathname));

			listOfSongs.clear();

//			List<String> uniquePathsList = new ArrayList<>();
			
			while (scanner.hasNextLine()) {
				String pfad = scanner.nextLine().trim();
				if (!(pfad.indexOf('#') == 0 || pfad.isBlank())) {
					try {
//						if(!uniquePathsList.contains(pfad)) {
//							uniquePathsList.add(pfad);
//						}
						listOfSongs.add(AudioFileFactory.createAudioFile(pfad));
					} catch (NotPlayableException ex) {
//						throw new RuntimeException("the filepath to AufioFile in m3u was not correct");
						ex.getStackTrace();
					}
				}
			}
			// TODO what if: es gibt m3u ABER keine der PATHS führt zu einem richtigen File,
			// soll counter auf null gesetzt werden?
			iterator = (ControllablePlayListIterator) iterator();
		} catch (Exception e) {
			throw new RuntimeException("Load m3u is not success, there is no m3u file in this path");
		} finally {
			System.out.println("File " + m3uPathname + " read!");
			try {
				scanner.close();
			} catch (Exception e) {
				e.getStackTrace();
				// ignore; probably because file could not be opened
			}
		}
	}

	public void saveAsM3U(String m3uPathname) {
		FileWriter writer = null;
		String sep = System.getProperty("line.separator");

		try {
			writer = new FileWriter(m3uPathname);
			for (AudioFile line : listOfSongs) {
				writer.write(line.getPathname() + sep);
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to write file " + m3uPathname + "!");
		} finally {
			try {
				System.out.println("File " + m3uPathname + " written!");
				// close the file writing back all buffers
				writer.close();
			} catch (Exception e) {
				// ignore exception; probably because file could not be opened
			}
		}
	}

	@Override
	public Iterator<AudioFile> iterator() {
		return new ControllablePlayListIterator(listOfSongs, search, sortCriterion);
	}

	public void jumpToAudioFile(AudioFile file) {
		if (iterator.getList().contains(file)) {
			int fileIndex = iterator.getList().indexOf(file);
			iterator.setCounter(fileIndex);
		} else {
			System.out.println("es gibt in diesem Playlist bei den Einstellungen kein File " + file.toString());
		}
	}

	public LinkedList<AudioFile> getList() {
		return listOfSongs;
	}

	@Override
	public String toString() {
		return Arrays.toString(listOfSongs.toArray());
	}
}
