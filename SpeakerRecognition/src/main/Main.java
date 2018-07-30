package main;




import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
 
 
public class Main extends Application {

	public static void main(String[] args) throws Exception {
		launch(args);

   
  	
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
	

		// TODO Auto-generated method stub
		//pracownicy.wczytaj();
		// TODO Auto-generated method stub
  FXMLLoader loader=new FXMLLoader();
 loader.setLocation(this.getClass().getResource("../controllers/Main_fx.fxml"));
 AnchorPane stack=loader.load();
 MainController Controller = loader.getController();
 Scene scena=new Scene(stack);
 primaryStage.setScene(scena);
	primaryStage.setTitle("Speaker Recognizer");
		primaryStage.show();
		Controller.setStage(primaryStage);
	}
}
