//** Owned by Jeffrey Kraskouskas
// Peer Reviewed By Tiziano and Souma


import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.util.Duration;

// TODO: Auto-generated Javadoc
public class ElevatorSimulation extends Application {
	
	// Instantiated controller as well as an elevator simulation.
	private ElevatorSimController controller;
	private int NUM_FLOORS;
	private int NUM_ELEVATORS;
	private int currFloor;
	private int passengers;
	private int time;

	// Elevator states
	private final int floorSpacing = 68;
	private final double floorLabelSpacing = 3.48;
	private final int STOP = Elevator.STOP;
	private final int MVTOFLR = Elevator.MVTOFLR;
	private final int OPENDR = Elevator.OPENDR;
	private final int OFFLD = Elevator.OFFLD;
	private final int BOARD = Elevator.BOARD;
	private final int CLOSEDR = Elevator.CLOSEDR;
	private final int MV1FLR = Elevator.MV1FLR;
	
	
	// Necessary stylistic objects
	BorderPane mainBorder = new BorderPane();
	GridPane main = new GridPane();
	StackPane stack = new StackPane();
	Rectangle closedDoors = new Rectangle();
	Rectangle openDoors = new Rectangle();
	Rectangle loadingDoors = new Rectangle();
	Rectangle unloadingDoors = new Rectangle();
	Rectangle movingUp = new Rectangle();
	Polygon moveUpTriangle = new Polygon();
	Rectangle movingDown = new Rectangle();
	Polygon moveDownTriangle = new Polygon();
	GridPane gridPane = new GridPane();
	GridPane topGridPane = new GridPane();
	TilePane tile = new TilePane();
	Line heightLine = new Line(0, 0, 0, (floorSpacing * 5));
	Line displacementLine = new Line(0, 0, 200, 0);
	Line labelSplit = new Line(0, 0, 0, 50);
	Label passengerAmount = new Label();
	Label timeCount = new Label();
	int permanentCount = -1;
	int floorElementCount = 18;
	Button stepButton;
	VBox floors = new VBox(floorLabelSpacing);
	boolean firstCountChange = false;
	Timeline oneSecond = new Timeline( 
			new KeyFrame(Duration.millis(5),
					e -> controller.stepSim())
			);
	
	Label[] labelsFloor = new Label[18];
	
	/**
	 * Instantiates a new elevator simulation.
	 */
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
		
		NUM_FLOORS = controller.getNumFloors();
		NUM_ELEVATORS = controller.getNumElevators();
		currFloor = controller.getFloor(0);
	}

	/** Peer Reviewed By Souma and Tiziano
	 * Start. Contains GUI design.
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// You need to design the GUI. Note that the test name should
		// appear in the Title of the window!!
		
		// uncomment the getTestName part
		primaryStage.setTitle("Elevator Simulation - "+ controller.getTestName());
		Scene scene = new Scene(mainBorder,400,500);
		StackPane stack = new StackPane();
		// add methods
		elevatorStates();
		floorCreation();
		
		stack.getChildren().addAll(closedDoors, openDoors, loadingDoors, unloadingDoors, movingUp, moveUpTriangle, movingDown, moveDownTriangle);
		
		HBox runOptions = new HBox(20); // get rid of magic numbers
		Button runButton = new Button("Run");
		stepButton = new Button("Step:");
		Button logButton = new Button("Log");
		TextField stepAmount = new TextField();
		stepAmount.setText("1");
		
		stepButton.setOnAction(e -> {
			if (!stepAmount.getText().matches("^[0-9]*$")) {
				stepAmount.setText("Input a number!");
			}
			else {
				for (int i = 0; i < Integer.parseInt(stepAmount.getText()); i++) {
					controller.stepSim();
				}
				}
			}
		);
		logButton.setOnAction(e -> {
			controller.enableLogging();
		});
		runButton.setOnAction(e -> {
			oneSecond.setCycleCount(Animation.INDEFINITE);
			oneSecond.play();
		});
		runOptions.getChildren().addAll(runButton, stepButton, stepAmount, logButton);
		
		passengerAmount.setFont(new Font("Arial", 30));
		passengerAmount.setText("Passenger Count: " + controller.getNumPassengers(0));
		timeCount.setFont(new Font("Arial", 30));
		timeCount.setText("Time: " + controller.getTime());
		
		
		heightLine.setVisible(false);
		displacementLine.setVisible(false);
		
		gridPane.add(stack, 0, 5);
		gridPane.add(displacementLine, 0, 4);
		gridPane.add(heightLine, 1, 4);
		topGridPane.add(passengerAmount, 0, 1);
		topGridPane.add(timeCount, 0, 2);
		
		mainBorder.setBottom(runOptions);
		mainBorder.setRight(floors);
		mainBorder.setTop(topGridPane);
		
		mainBorder.setCenter(gridPane);
		
		primaryStage.setScene(scene);
	
		primaryStage.show();
		
		closedDoors.setVisible(true);

	}
	
	/** Peer Reviewed by Tiziano
	 * Updates the GUI after a change in elevator states has occurred.
	 */
	public void updateGUI() {
		closedDoors.setVisible(false);
		movingUp.setVisible(false);
		openDoors.setVisible(false);
		unloadingDoors.setVisible(false);
		loadingDoors.setVisible(false);
		closedDoors.setVisible(false);
		movingUp.setVisible(false);
		moveDownTriangle.setVisible(false);
		moveUpTriangle.setVisible(false);
		currFloor = controller.getFloor(0);
		timeCount.setText("Time: " + controller.getTime());
		if (permanentCount != -1) {
			timeCount.setText("Time: " + permanentCount);
		}
		switch (controller.getState(0)) {
		case STOP:
			closedDoors.setVisible(true);
			break;
		case MVTOFLR:
			movingUp.setVisible(true);
			break;
		case OPENDR:
			openDoors.setVisible(true);
			break;
		case OFFLD:
			unloadingDoors.setVisible(true);
			break;
		case BOARD:
			loadingDoors.setVisible(true);
			break;
		case CLOSEDR:
			closedDoors.setVisible(true);
			break;
		case MV1FLR: // reverse the getDirection logic
			movingUp.setVisible(true);
			if (controller.getDirection(0) == 1) {
				moveUpTriangle.setVisible(true);
			}
			else {
				moveDownTriangle.setVisible(true);
			}
			break;
		}
		int floorIndex = 5;
		for (int i = 0; i < floorElementCount; i++) { // replace magic number in loop
			labelsFloor[i].setText("Total calls: " + (controller.getUpCallsOnFloor(floorIndex, 0) + controller.getDownCallsOnFloor(floorIndex, 0)));
			labelsFloor[1 + i].setText("Up calls: " + controller.getUpCallsOnFloor(floorIndex, 0));
			labelsFloor[2 + i].setText("Down calls: " + controller.getDownCallsOnFloor(floorIndex, 0));
			i += 2;
			floorIndex -= 1;
		}
		
		passengerAmount.setText("Passenger Count: " + controller.getNumPassengers(0));
		heightLine.setEndY((floorSpacing * 5) - (currFloor * floorSpacing));
		
	}
	/** Peer Reviwed by Tiziano
	 * Initializes all of the shapes, including directional ones
	 */
	public void elevatorStates() {
		doorStates();
		
		movingUp = new Rectangle(40, 40);
		moveUpTriangle = new Polygon();
		moveUpTriangle.getPoints().addAll(new Double[]{ // triangles are cool addition
			    20.0, 0.0,
			    30.0, 20.0,
			    10.0, 20.0 });
		moveUpTriangle.setFill(Color.rgb(0, 0, 255)); // do u need to use color codes?
		movingUp.setFill(Color.rgb(255, 0, 0));
		movingUp.setStroke(Color.rgb(0, 0, 0));
		moveUpTriangle.setVisible(false);
		movingUp.setStrokeWidth(5);
		movingUp.setVisible(false);
		
		movingDown = new Rectangle(40, 40);
		moveDownTriangle = new Polygon();
		moveDownTriangle.getPoints().addAll(new Double[]{
			    20.0, 20.0,
			    30.0, 0.0,
			    10.0, 0.0 });
		moveDownTriangle.setFill(Color.rgb(0, 0, 255));
		movingDown.setFill(Color.rgb(255, 0, 0));
		movingDown.setStroke(Color.rgb(0, 0, 0));
		moveDownTriangle.setVisible(false);
		movingDown.setStrokeWidth(5);
		movingDown.setVisible(false);
	}
	
	/** Peer Reviewed by Souma
	 * Initializes the door states for the elevator.
	 */
	public void doorStates() {
		// looks good
		closedDoors = new Rectangle(40, 40);
		closedDoors.setFill(Color.rgb(255, 0, 0));
		closedDoors.setStroke(Color.rgb(0, 0, 0));
		closedDoors.setStrokeWidth(5);
		closedDoors.setVisible(false);
		
		openDoors = new Rectangle(40, 40);
		openDoors.setFill(Color.rgb(0, 255, 0));
		openDoors.setStroke(Color.rgb(0, 0, 0));
		openDoors.setStrokeWidth(5);
		openDoors.setVisible(false);
		
		loadingDoors = new Rectangle(40, 40);
		loadingDoors.setFill(Color.rgb(0, 255, 0));
		loadingDoors.setStroke(Color.rgb(0, 0, 255));
		loadingDoors.setStrokeWidth(5);
		loadingDoors.setVisible(false);
		
		unloadingDoors = new Rectangle(40, 40);
		unloadingDoors.setFill(Color.rgb(0, 255, 0));
		unloadingDoors.setStroke(Color.rgb(255, 192, 203));
		unloadingDoors.setStrokeWidth(5);
		unloadingDoors.setVisible(false);
	}
	
	/** Peer Reviewed By Souma
	 * Initializes all the floor elements of GUI.
	 * 
	 */
	public void floorCreation() {
		Line offSet = new Line(0, 0, 0, 0);
		offSet.setVisible(false);
		Line floorOne = new Line(0, 0, 300, 0); // Souma Peer Review: Good addition to GUI. Less magic numbers.
		Line floorTwo = new Line(0, 0, 300, 0);
		Line floorThree = new Line(0, 0, 300, 0);
		Line floorFour = new Line(0, 0, 300, 0);
		Line floorFive = new Line(0, 0, 300, 0);
		Line floorSix = new Line(0, 0, 300, 0);
		
		for (int i = 0; i < labelsFloor.length; i++) {
			labelsFloor[i] = new Label("Total Calls: 0");
			labelsFloor[i + 1] = new Label("Up Calls: 0");
			labelsFloor[i + 2] = new Label("Down Calls: 0");
			i += 2;
		}
		
		floors.getChildren().addAll(offSet, labelsFloor[0], labelsFloor[1], labelsFloor[2], floorOne, 
				labelsFloor[3], labelsFloor[4], labelsFloor[5], floorTwo, 
				labelsFloor[6], labelsFloor[7], labelsFloor[8], floorThree, 
				labelsFloor[9], labelsFloor[10], labelsFloor[11], floorFour, 
				labelsFloor[12], labelsFloor[13], labelsFloor[14], floorFive, 
				labelsFloor[15], labelsFloor[16], labelsFloor[17], floorSix);
	}

	/** Peer Reviewed by Tiziano
	 * Ends the current simulation once it is done using the run function.
	 * 
	 * 
	 */
	public void endSimulation() {
		oneSecond.stop();
		stepButton.setOnAction(e -> System.out.println("System Ended")); // Tiziano Peer Review: make sure overstepping stops the timeline properly
		if (!firstCountChange) permanentCount = controller.getTime();
		firstCountChange = true;
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main (String[] args) {
		Application.launch(args);
	}

}
