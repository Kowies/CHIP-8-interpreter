package chip8.app;

import chip8.CPU;
import chip8.Keyboard;
import chip8.Memory;
import chip8.Screen;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class CHIP8App extends Application {

    CPU cpu;
    Keyboard keyboard;
    Memory memory;
    Screen screen;

    ScreenApp screenApp;

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 400;

    private MenuBar menuBar;

    private MenuBar createMenuBar() {

        MenuBar menuBar = new MenuBar();

        Menu menuFile = new Menu("File");
        MenuItem itemOpenRom = new MenuItem("Open ROM");
        MenuItem itemExit = new MenuItem("Exit");
        menuFile.getItems().add(itemOpenRom);
        menuFile.getItems().add(itemExit);
        menuBar.getMenus().add(menuFile);

        Menu menuSaveLoad = new Menu("Save/Load");
        MenuItem itemSave = new MenuItem("Save...");
        MenuItem itemLoad = new MenuItem("Load...");
        menuSaveLoad.getItems().add(itemSave);
        menuSaveLoad.getItems().add(itemLoad);
        menuBar.getMenus().add(menuSaveLoad);

        return menuBar;
    }

    private int convert(KeyEvent e) {

        switch (e.getCode()) {
            case DIGIT0:
                return 0;
            case DIGIT1:
                return 1;
            case DIGIT2:
                return 2;
            case DIGIT3:
                return 3;
            case DIGIT4:
                return 4;
            case DIGIT5:
                return 5;
            case DIGIT6:
                return 6;
            case DIGIT7:
                return 7;
            case DIGIT8:
                return 8;
            case DIGIT9:
                return 9;
            case A:
                return 10;
            case B:
                return 11;
            case C:
                return 12;
            case D:
                return 13;
            case E:
                return 14;
            case F:
                return 15;
            default:
                return -1;
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("CHIP-8 emulator");

        Memory memory = new Memory();
        Keyboard keyboard = new Keyboard();
        Screen screen = new Screen();

        CPU cpu = new CPU(memory, keyboard, screen);



        VBox layout = new VBox();

        menuBar = createMenuBar();
        layout.getChildren().add(menuBar);

        screenApp = new ScreenApp(screen, SCREEN_WIDTH, SCREEN_HEIGHT);
        layout.getChildren().add(screenApp);


        // screenApp.render();
        Button button = new Button();
        button.setText("Open a New Window");

        DebugInfoApp debugInfoApp = new DebugInfoApp(cpu);

        button.setOnAction(e -> debugInfoApp.show() );

        layout.getChildren().add(button);



        Scene scene = new Scene(layout);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}