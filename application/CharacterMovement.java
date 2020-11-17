package application;



import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Hold down an arrow key to have your car drive around the screen. Make sure to avoid the other car!
 * Hold down the shift key to have the driver step on the gas.
 */
public class CharacterMovement extends Application {

    private static final double W = 956, H = 740;

    private static final String CAR_IMAGE_LOC =
            "https://i.imgur.com/V4G07Q8.png";
    
    private static final String CAR_IMAGE_LOC_2 =
    		"https://i.imgur.com/MrFg7OU.png";
//    blue car - https://i.imgur.com/9bLztIl.png
    
    private static final String SCENE_IMAGE_LOC = 
    		"https://i.imgur.com/zaP4Fe7.png";
    
//    private static final String PAUSE_LOC = 
//    		"https://i.imgur.com/n7HbHdB.png";
    
    private Image carImage;
    private playerCar car;
    
    private Image car2Image;
    private enemyCar car2;
    
    private Image raceImage;
    private Node race;

//    private Image pauseImage;
//    private Node pause;
    
    int counter = 0;
    
    boolean running, goNorth, goSouth, goEast, goWest;

    @Override
    public void start(Stage stage) throws Exception {
    	
        carImage = new Image(CAR_IMAGE_LOC);
        car = new playerCar(carImage);
        
        car2Image = new Image(CAR_IMAGE_LOC_2);
        car2 = new enemyCar(car2Image);
        
        raceImage = new Image(SCENE_IMAGE_LOC);
        race = new ImageView(raceImage);
        
//        pauseImage = new Image(PAUSE_LOC);
//        pause = new ImageView(pauseImage);
        
        Group game = new Group(race, car, car2);
      
        moveCarTo(W / 1.3, H / 2);
        car2.relocate(W / 6, H / 2);
        Scene scene2 = new Scene(game, W, H);
        
        //creating the title screen and play button
    	stage.setTitle("DRIFT STAGE");
        Pane root = new Pane();
        ImageView backgroundImageView = new ImageView(getClass().getResource("titlescreen.png").toExternalForm());
        root.getChildren().add(backgroundImageView);
        
        //creating the play button and adding it into the title screen
		Button startButton = new Button("PLAY");
		startButton.setMinSize(200, 100);
		startButton.setLayoutX(335);
		startButton.setLayoutY(550);
		startButton.setStyle("-fx-background-color: #ee2364;" + "-fx-font-size: 40;" + "-fx-text-fill: white;");
		startButton.setOnAction(e -> stage.setScene(scene2));       
		root.getChildren().add(startButton);
		Scene titleScreen= new Scene(root, 956, 740);
		
		//creating the game over screen
        Pane root2 = new Pane();
        ImageView backgroundImageView2 = new ImageView(getClass().getResource("gameoverscreen.jpg").toExternalForm());
        root2.getChildren().add(backgroundImageView2);
        
        //creating the try again button and adding it into the game over screen
		Button gameOverButton = new Button("TRY AGAIN");
		gameOverButton.setMinSize(200, 100);
		gameOverButton.setLayoutX(345);
		gameOverButton.setLayoutY(550);
		gameOverButton.setStyle("-fx-background-color: white;" + "-fx-font-size: 40;" + "-fx-text-fill: blue;");
		gameOverButton.setOnAction(e -> stage.setScene(titleScreen));       
		root2.getChildren().add(gameOverButton);
		Scene gameOverScreen= new Scene(root2, 956, 740);
		
        scene2.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:    goNorth = true; break;
                    case DOWN:  goSouth = true; break;
                    case LEFT:  goWest  = true; break;
                    case RIGHT: goEast  = true; break;
                    case SHIFT: running = true; break;
                }
            }
        });

        scene2.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:    goNorth = false; break;
                    case DOWN:  goSouth = false; break;
                    case LEFT:  goWest  = false; break;
                    case RIGHT: goEast  = false; break;
                    case SHIFT: running = false; break;
                }
            }
        });

        //started with the game over screen to see what it looks like
        stage.setScene(gameOverScreen);
        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                int dx = 0, dy = 0;
                
                if (goNorth) dy -= 1;
                if (goSouth) dy += 1;
                if (goEast)  dx += 1;
                if (goWest)  dx -= 1;
                if (running) { dx *= 3; dy *= 3; }

                moveCarBy(dx, dy);
                if(!car.getHacked()) {
                	double distance = giveChase();
                		if (distance <= 75.0){
                			counter++;
                		}
                		if (counter >= 5000) {
                			(car2).hack(car);
                		}
                } else {
                	crash(car);
                	reCenter(car2);
                	if(car.getLayoutX() == 0 || car.getLayoutX() == W) {
                		car.relocate(W / 1.3, H / 2);
                		car.setHacked(false);
                	}
                }
//                if (isTouching(car, car2)) {
//                	car.relocate((W / 1.3),  (H / 2));
//                }
                
            }
        };
        
        timer.start();
        
    }

    private void moveCarBy(int dx, int dy) {
        if (dx == 0 && dy == 0) return;

        final double cx = car.getBoundsInLocal().getWidth()  / 2;
        final double cy = car.getBoundsInLocal().getHeight() / 2;

        double x = cx + car.getLayoutX() + dx;
        double y = cy + car.getLayoutY() + dy;

        moveCarTo(x, y);
    }
    


    private void moveCarTo(double x, double y) {
        final double cx = car.getBoundsInLocal().getWidth()  / 2;
        final double cy = car.getBoundsInLocal().getHeight() / 2;

        if (x - cx >= 0 &&
            x + cx <= W &&
            y - cy >= 0 &&
            y + cy <= H) {
            car.relocate(x - cx, y - cy);
        }
    }
    //Method designed to crash the player car as a result of being hacked. May be changed as other hacking condtions are designed/implemented
    private void crash(playerCar car) {
    	double xPosition = car.getLayoutX();
    	double half = W / 2;
    	double goal;
    	if(xPosition <= half) {
    		goal = 0;
    	} else {
    		goal = 956;
    	}
    	double borderDistance = goal - xPosition;
    	car.relocate(xPosition + (borderDistance / 100), car.getLayoutY());
    }
    
    private void reCenter(enemyCar car) {
    	double xPosition = car.getLayoutX();
    	double yPosition = car.getLayoutY();
    	double xGoal = W / 6;
    	double yGoal = H / 2;
    	double xDistance = xGoal - xPosition;
    	double yDistance = yGoal - yPosition; 
    	car.relocate(xPosition + (xDistance / 100), yPosition + (yDistance / 100));
    }
    
    //Method used to calculate how far the second car needs to move to chase the player
    //Method then relocates second car accordingly. Returns the direct distance between the center points as well
    private double giveChase() {
    	double C2x = car2.getLayoutX();
    	double C2y = car2.getLayoutY();
    	double C1x = car.getLayoutX();
    	double C1y = car.getLayoutY();
    	double xDistance = C1x - C2x;
    	double yDistance = C1y - C2y;
    	double compDistance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
    	car2.relocate(C2x + (xDistance/ 250), C2y + (yDistance / 250));
    	return compDistance;
    }
    //Helper method for isTouching(), which determines the minX, minY, maxX, and maxY
    //of the specified car, and inserts them in an array for easy use by isTouching()
    private double[] getBounds(Node car) {
    	double[] boundsArray = new double[3];
    	double halfX = car.getBoundsInLocal().getWidth() / 2;
    	double halfY = car.getBoundsInLocal().getHeight() / 2;
    	boundsArray[0] = car.getLayoutX() - halfX;
    	boundsArray[1] = car.getLayoutY() - halfY;
    	boundsArray[2] = car.getLayoutX() + halfX;
    	boundsArray[3] = car.getLayoutY() + halfY;
    	return boundsArray;
    	
    }
    //Method designed to determine if the two cars are touching. 
    //if touching return true, else return false
     private boolean isTouching(Node car, Node car2) {
    	 double[] carBounds = getBounds(car);
    	 double[] car2Bounds = getBounds(car2);
    	 if(carBounds[3] < car2Bounds[0] || carBounds[0] > car2Bounds[3]) {
    		 if(carBounds[4] < car2Bounds[1] || carBounds[1] >  car2Bounds[4]) {
    			 return false;
    		 }
    	 }
    	 return true;
     }

    public static void main(String[] args) { launch(args); }
}