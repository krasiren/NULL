package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Bitcoin UV");
        primaryStage.setScene(new Scene(root, 800, 400));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(400);
        primaryStage.setFullScreen(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        System.out.println(BcOperations.hexToString(BcOperations.decodeData("0200000001a89442ee8bc9458288dfc5cfa96844789ca23b916608d48b758dd326cc02ad57000000006a47304402207d1c1fc589a611954cb58f3555ae3e9e044f53af8354065dff1979e24b7177e9022021f9076bd16f751837041181466fd0b94374596a1ee2f303ecc1c2d582a2abf34121034a73925b1178daf6d211f8d2a1680c29e141dd53bffdbecbbbe2552b6f1eb342feffffff0200000000000000001b006a18526f6b2c323032312d30352d30372c382e30302c776f726bb8ee052a010000001976a914f78e3edb56a16d48079aa5693721581142c1c81888ac00000000")));
        //launch(args);
    }
}
