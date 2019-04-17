import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MyMediaPlayer extends Application{
	private Scene scene;
	private BorderPane borderPane;
	private VBox vBoxRight;
	private HBox hBoxBottom;
	
	private Button btnAddFile;
	private Button btnPrevious;
	private Button btnBack;
	private Button btnPlayPaus;
	private Button btnFastForward;
	private Button btnNext;
	
	private Slider sliderSeek;
	private Slider sliderVolume;
	
	private Media media;
	private MediaPlayer mediaPlayer;
	private MediaView mediaView;
	private String filePath = null;
	private ListView <Label> listViewLabel;
	private ObservableList<Label> observableList;
	private ArrayList <String> filePathList = new ArrayList<>();
	
	private int firstTime = 0;
	private int index = -1;
	private int currentIndex = 0;
	
	@Override
	public void start(Stage stage) {
		// Creating Menu, functionality can be added later
		Menu fileMenu = new Menu("File");
		MenuItem exitItem = new MenuItem("Exit");
		fileMenu.getItems().addAll(
                new MenuItem("New"),
                new MenuItem("Open"),
                new MenuItem("Print"),
                exitItem
        );
		
		exitItem.setOnAction(e->{
			System.exit(0);
		});
        
        Menu editMenu = new Menu("Edit");
        editMenu.getItems().addAll(
                new MenuItem("Undo"),
                new MenuItem("Copy"),
                new MenuItem("Paste")
        );
        
        Menu viewMenu = new Menu("View");
        viewMenu.getItems().addAll(
                new MenuItem("Print Preview")
        );
        
        Menu windowMenu = new Menu("Window");
        windowMenu.getItems().addAll(
                new MenuItem("New Window"),
                new MenuItem("Appearance")
        );
        
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().addAll(
                new MenuItem("Welcome"),
                new MenuItem("Search")
        );
        // Adding Menus to the MenuBar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, editMenu, windowMenu, helpMenu);
		
		// <<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>\\
        
		btnAddFile = new Button("Open");
		btnAddFile.setOnAction(e->{
			addFile();
		});
		
		btnBack = new Button("<<");
		btnBack.setOnAction(e->{
			rewind();
		});
		
		btnPlayPaus = new Button("Play");
		btnPlayPaus.setPrefWidth(40);
		btnPlayPaus.setOnAction(e->playPause());
		
		btnFastForward = new Button(">>");
		btnFastForward.setOnAction(e->{
			fastForward();
		});
		
		btnPrevious = new Button("Prev");
		btnPrevious.setOnAction(ePrev ->{
			mediaPlayer.stop();
			if(currentIndex == 0) {
				currentIndex = observableList.size() - 1;
			}
			else {
				currentIndex --;
			}
			playVideo(filePathList.get(currentIndex));
		});
		
		btnNext = new Button("Next");
		btnNext.setOnAction(eNext -> {
			currentIndex = (currentIndex + 1) % observableList.size();
			mediaPlayer.stop();
			playVideo(filePathList.get(currentIndex));
		});
		
		sliderSeek = new Slider();
		sliderSeek.setMin(0);
		sliderSeek.setMax(100);
		sliderSeek.setPrefWidth(250);
		sliderSeek.setShowTickMarks(true);
        sliderSeek.setMajorTickUnit(20);
        sliderSeek.setMinorTickCount(4);
        sliderSeek.setBlockIncrement(10);
        
		sliderVolume = new Slider();
		sliderVolume.setMin(0);
		sliderVolume.setMax(100);
		sliderVolume.setShowTickMarks(true);
		sliderVolume.setMajorTickUnit(25);
		sliderVolume.setMinorTickCount(3);
		sliderVolume.setBlockIncrement(5);
		
		listViewLabel = new ListView<>();
		
		// Creating a an HBox >>>>>>>>>>>>>\\
		hBoxBottom = new HBox(5);
		hBoxBottom.getChildren().addAll(
				btnPrevious, btnBack, btnPlayPaus, btnFastForward, btnNext,
				sliderSeek, sliderVolume				
				);
		hBoxBottom.setAlignment(Pos.CENTER);
		hBoxBottom.setPadding(new Insets(5, 5, 8, 5)); // top right, bottom, left
		
		// Creating a VBox >>>>>>>>>>>>>>>>\\
		vBoxRight = new VBox(5);
		vBoxRight.getChildren().addAll(listViewLabel, btnAddFile);
		//vBoxRight.setAlignment(Pos.CENTER);
		vBoxRight.setPadding(new Insets(5, 5, 5, 5));
		
		// Creating BorderPane >>>>>>>>>>>>>>>\\
		borderPane = new BorderPane();
		borderPane.setTop(menuBar);
		borderPane.setRight(vBoxRight);
		borderPane.setBottom(hBoxBottom);
		
		scene = new Scene(borderPane, 700, 470);
		
		stage.setTitle("Mediaplayer");
		stage.setScene(scene);
		stage.show();
	}
	
	private void addFile() {
		firstTime ++;
		index ++;
		
		FileChooser fileChooser = new FileChooser();
		// You could choose your file extension 
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select File", "*.m*");
		
		fileChooser.getExtensionFilters().add(filter);
		File file = fileChooser.showOpenDialog(null);
		String fileName = file.getName();
		filePath = file.toURI().toString();
		filePathList.add(filePath);
		
		Label newLabel = new Label(fileName);
		listViewLabel.getItems().add(newLabel);
		observableList = listViewLabel.getItems();
		
		if (filePath != null) {
			media = new Media(filePath);
		}

		if (firstTime == 1) {
			currentIndex = index;
			playVideo(filePathList.get(index));
		}
		
		newLabel.setOnMouseClicked(ev ->{
			index = observableList.indexOf(newLabel);
			
			if(ev.getClickCount() == 2) {
				currentIndex = index;
				mediaPlayer.pause();
				playVideo(filePathList.get(index));
			}
		});
	}
	
	private void playVideo(String filePath) {
		media = new Media(filePath);
        mediaPlayer = new MediaPlayer(media);
        mediaView = new MediaView(mediaPlayer);
        mediaView.setFitHeight(400);
        mediaView.setFitWidth(380);
		
		borderPane.setCenter(mediaView);
		btnPlayPaus.setText("Play");
		
		sliderVolume.setValue(50);
		mediaPlayer.volumeProperty().bind(sliderVolume.valueProperty().divide(100));
		
		mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,Duration newValue){
				sliderSeek.setValue(newValue.toSeconds());
				
			}
		});
		
		sliderSeek.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mediaPlayer.seek(Duration.seconds(sliderSeek.getValue()));
			}
		});
		
		mediaPlayer.play();
		
	}
	
	private void fastForward() {
		mediaPlayer.seek(Duration.seconds(sliderSeek.getValue() + 5));	
	}
	
	private void rewind() {
		mediaPlayer.seek(Duration.seconds(sliderSeek.getValue() - 5));	
	}
	
	private void playPause() {
		if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
			btnPlayPaus.setText("||");
			mediaPlayer.pause();
		}
		else if(mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
			btnPlayPaus.setText("Play");
			mediaPlayer.play();
		}
		if(mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED) {
			btnPlayPaus.setText("Play");
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
