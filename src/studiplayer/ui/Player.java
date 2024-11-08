package studiplayer.ui;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import studiplayer.audio.AudioFile;
import studiplayer.audio.NotPlayableException;
import studiplayer.audio.PlayList;
import studiplayer.audio.SampledFile;
import studiplayer.audio.SortCriterion;

public class Player extends Application {
	private static final String PLAYLIST_DIRECTORY = "playlists";
	public static final String DEFAULT_PLAYLIST = "playlists/DefaultPlayList.m3u";
	private static final String INITIAL_PLAY_TIME_LABEL = "00:00";
	private static final String NO_CURRENT_SONG = "-";

	private boolean useCertPlayList = false;
	private PlayList playList;

	private Button playButton = createButton("play.jpg");
	private Button pauseButton = createButton("pause.jpg");
	private Button stopButton = createButton("stop.jpg");
	private Button nextButton = createButton("next.jpg");

	private Button filterButton;
	private Label playListLabel = new Label(PLAYLIST_DIRECTORY);
	private Label playTimeLabel = new Label(INITIAL_PLAY_TIME_LABEL);
	private Label currentSongLabel = new Label(NO_CURRENT_SONG);
	private ChoiceBox<SortCriterion> sortChoiceBox;
	private TextField searchTextField;

	private SongTable songTable;

	private PlayerThread playerThread;
	private TimerThread timerThread;

	private long abspielzeit = 0; // 1 millionstel

	private static String m3uFilePath = "";

	class PlayerThread extends Thread {
		private boolean stopped = false;

		public void terminate() {
			stopped = true;
		}

		@Override
		public void run() {
			while (!stopped) {
				try {
					playList.currentAudioFile().play();			
				} catch (NotPlayableException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class TimerThread extends Thread {
		private boolean stopped = false;

		public void terminate() {
			stopped = true;
		}

		@Override
		public void run() {
			while (!stopped) {
				try {
					Thread.sleep(10);
					abspielzeit += 10000;
					if (SampledFile.timeFormatter(abspielzeit).equals(
							SampledFile.timeFormatter(((SampledFile)playList.currentAudioFile()).getDuration()+ 1_000_000))) {
						nextButton.fire();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Platform.runLater(() -> {
					playTimeLabel.setText(SampledFile.timeFormatter(abspielzeit));
				});
			}
		}
	}

	public Player() {

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("APA Player");
		setButtonStates(false, true, true, false);

		if (!useCertPlayList) {
			//
			// FileChooser
			//

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Playlist");
			File m3uFile = fileChooser.showOpenDialog(primaryStage); 
			// TODO handle file excpetions
			// TODO if path not blank check if it is m3ufile:
			try {
				m3uFilePath = m3uFile.getPath();
				loadPlayList(m3uFile.getPath());
			} catch (NullPointerException ex) {
				loadPlayList(null);
			}
			songTable = new SongTable(playList);
			//
			// choose the song from table
			//
			songTable.setRowSelectionHandler(event -> {
				playList.jumpToAudioFile(songTable.getSelectionModel().getSelectedItem().getAudioFile());
				System.out.println(playList.currentAudioFile().toString());
			});
		}

		// Sortierung TitledPane
		TitledPane titledPaneFilter = new TitledPane();
		titledPaneFilter.setText("Filter");
		GridPane gridPlayListSettings = new GridPane();
		gridPlayListSettings.setVgap(4);
		gridPlayListSettings.setHgap(4);
		gridPlayListSettings.setPadding(new Insets(5, 5, 5, 5));
		gridPlayListSettings.add(new Label("Suchtext"), 0, 0);
		searchTextField = new TextField();
		gridPlayListSettings.add(searchTextField, 1, 0);
		gridPlayListSettings.add(new Label("Sortierung"), 0, 1);
		TitledPane titledPaneSortierung = new TitledPane();
		ObservableList<SortCriterion> sortierungsOptions = FXCollections.observableArrayList();
		sortierungsOptions.addAll(SortCriterion.values());
		sortChoiceBox = new ChoiceBox<>(sortierungsOptions);
		sortChoiceBox.getSelectionModel().select(0);
		sortChoiceBox.setPrefWidth(800);
		gridPlayListSettings.add(sortChoiceBox, 1, 1);
		filterButton = new Button("Anzeigen");
		gridPlayListSettings.add(filterButton, 2, 1);
		titledPaneFilter.setContent(gridPlayListSettings);

		//
		// VISUAL
		//
		VBox boxPlayListInfos = new VBox();
		boxPlayListInfos.getChildren().addAll(playListLabel, currentSongLabel, playTimeLabel);
		// playlist buttons
		HBox boxPlayListButtons = new HBox();
		boxPlayListButtons.getChildren().addAll(playButton, pauseButton, stopButton, nextButton);
		boxPlayListButtons.setAlignment(Pos.CENTER);
		// Make up the Scene
		BorderPane mainPane = new BorderPane();
		mainPane.setCenter(songTable);
		BorderPane playListButtonsPane = new BorderPane();
		playListButtonsPane.setCenter(boxPlayListButtons);
		VBox mainBox = new VBox(titledPaneFilter, mainPane, boxPlayListInfos, playListButtonsPane);
		Scene scene = new Scene(mainBox, 600, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
		//
		// actions on sort button
		//
		filterButton.setOnAction(event -> {
			// sort and search in playlist
			playList.setSortCriterion(sortChoiceBox.getValue());
			playList.setSearch(searchTextField.getText());
			songTable.refreshSongs();
		});
		//
		//
		// play Buttons event Behandlung
		//
		//
		playButton.setOnAction(e -> {
			setButtonStates(true, false, false, false);
			Platform.runLater(() -> {
				currentSongLabel.setText("Aktueller Song\t " + playList.currentAudioFile().toString());
				playListLabel.setText("Playlist\t " + m3uFilePath);
			});
			if (playerThread == null) {
				startThreads(false);
			}

			/*
			 * brauche nciht oder? if (playerThread != null) { terminateThreads(false);
			 * startThreads(false); } else
			 */
		});

		pauseButton.setOnAction(e -> {
			// music spielt gerade
			if (playerThread != null) {
				terminateThreads(false);
				playList.currentAudioFile().togglePause();
				setButtonStates(true, false, false, false);
				System.out.println(playList.currentAudioFile() + "wird gepaust");
			}
			// let music go on
			else if (playerThread == null) {
				startThreads(false);
				playList.currentAudioFile().togglePause();
				setButtonStates(true, false, false, false);
				System.out.println(playList.currentAudioFile() + "wird fortgesetzt");
			}
		});
		stopButton.setOnAction(e -> {
			terminateThreads(false);
			playList.currentAudioFile().stop();
			setButtonStates(false, true, true, false);
			updateSongInfo(playList.currentAudioFile());
			System.out.println(playList.currentAudioFile() + " wird gestoppt");

		});

		nextButton.setOnAction(e -> {
			setButtonStates(true, false, false, false);
			// another music is being played is to be stopped
			if (playerThread != null) {
				terminateThreads(false);
				playList.currentAudioFile().stop();
			}
			System.out.println(playList.currentAudioFile() + " wird jetzt gespielt");
			playList.nextSong();
			startThreads(false);
			updateSongInfo(playList.currentAudioFile());
		});

	}

	public boolean loadPlayList(String pathname) {
		if (pathname == null || pathname.isBlank()) {
			playList = new PlayList(DEFAULT_PLAYLIST);
		} else {
			playList = new PlayList(pathname);
		}
		return true;
	}

	public void setUseCertPlayList(boolean value) {
		useCertPlayList = value;
	}

	public void setPlayList(String pathname) {
		playList = new PlayList(pathname); // TODO handle exceptions
		// only if songTable exists?
		songTable.refreshSongs();
	}

	private Button createButton(String iconfile) {
		Button button = null;
		try {
			URL url = getClass().getResource("/icons/" + iconfile);
			Image icon = new Image(url.toString());
			ImageView imageView = new ImageView(icon);
			imageView.setFitHeight(20);
			imageView.setFitWidth(20);
			button = new Button("", imageView);
			button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			button.setStyle("-fx-background-color: #fff;");
		} catch (Exception e) {
			System.out.println("Image " + "icons/" + iconfile + " not found!");
			System.exit(-1);
		}
		return button;
	}

	private void setButtonStates(boolean playButtonState, boolean pauseButtonState, boolean stopButtonState,
			boolean nextButtonState) {
		playButton.setDisable(playButtonState);
		pauseButton.setDisable(pauseButtonState);
		stopButton.setDisable(stopButtonState);
		nextButton.setDisable(nextButtonState);
	}

	// TODO wird er benutzt?
	private void updateSongInfo(AudioFile audioFile) {
		Platform.runLater(() -> {
			if (audioFile != null) {
				// hier currentSongLabel und playTimeLabel belegen
				currentSongLabel.setText("Aktueller Song\t " + audioFile.toString());
				abspielzeit = 0;
				playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL); // TODO muss es nicht immmer 0 sein?
			} else {
				// hier currentSongLabel und playTimeLabel belegen
				System.out.println("something wrong with the aufdiofile");
				// TODO
			}
		});
	}

	private void startThreads(boolean onlyTimer) {
		timerThread = new TimerThread();
		timerThread.start();
		if (!onlyTimer) {
			playerThread = new PlayerThread();
			playerThread.start();
		}
	}

	private void terminateThreads(boolean onlyTimer) {
		if (timerThread != null) {
			timerThread.terminate();
			timerThread = null;
		}
		if (!onlyTimer) {
			if (playerThread != null) {
				playerThread.terminate();
				playerThread = null;
			}
		}
	}

	public static void main(String[] args) {
		launch();
	}
}
