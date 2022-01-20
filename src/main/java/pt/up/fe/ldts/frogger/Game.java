package pt.up.fe.ldts.frogger;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFrame;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class Game {
    private int width = 60, height = 30;
    private Screen screen;
    private TextGraphics graphics;
    private Arena arena;
    private State state;
    private Level level;

    public Game() throws IOException, FontFormatException, URISyntaxException {
        createScreen();
        level = new Level(this); //sets default level to 1
        screen.refresh();

        arena = new Arena(level.getLevel(), width, height);
        state = new MenuState(this);
    }


    public void createScreen() throws IOException, FontFormatException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("Frogger.ttf");
        File fontFile = new File(resource.toURI());
        Font font =  Font.createFont(Font.TRUETYPE_FONT, fontFile);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);

        DefaultTerminalFactory factory = new DefaultTerminalFactory();

        Font loadedFont = font.deriveFont(Font.PLAIN, 20);
        AWTTerminalFontConfiguration fontConfig = AWTTerminalFontConfiguration.newInstance(loadedFont);
        factory.setTerminalEmulatorFontConfiguration(fontConfig);
        factory.setForceAWTOverSwing(true);
        factory.setInitialTerminalSize(new TerminalSize(width, height));

        Terminal terminal = factory.createTerminal();
        ((AWTTerminalFrame)terminal).addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
        });

        screen = new TerminalScreen(terminal);
        screen.setCursorPosition(null);
        screen.startScreen();
        screen.doResizeIfNecessary();
        graphics = screen.newTextGraphics();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Screen getScreen() {
        return screen;
    }

    public TextGraphics getGraphics() {
        return graphics;
    }

    public State getState(){
        return state;
    }

    public int getLevel(){
        return level.getLevel();
    }

    public void setState(State newState){
        state = newState;
    }

    public void setLevel(int newLevel) {
        level.setLevel(newLevel);
        arena.setLevel(newLevel);
    }

    //for testing purposes only
    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    //for testing purposes only
    public void setArena(Arena arena) {
        this.arena = arena;
    }

    //for testing purposes only
    public void setGraphics(TextGraphics graphics) {
        this.graphics = graphics;
    }

    public void draw() throws IOException {
        screen.clear();
        arena.draw(graphics);
        screen.refresh();
    }

    public void drawText(Position position, String text, String color) {
        TextGraphics tg = screen.newTextGraphics();
        tg.setForegroundColor(TextColor.Factory.fromString(color));
        tg.putString(position.getX(), position.getY(), text);
    }

    public void closeScreen() throws IOException {
        screen.close();
        System.out.println("Screen closed!");
    }

    public void processKey(KeyStroke key) {
        if (key == null)
            return;

        switch(key.getKeyType()){
            case ArrowRight:
                arena.moveFrog(arena.getFrog().moveRight());
                break;
            case ArrowLeft:
                arena.moveFrog(arena.getFrog().moveLeft());
                break;
            case ArrowUp:
                arena.moveFrog(arena.getFrog().moveUp());
                break;
            case ArrowDown:
                arena.moveFrog(arena.getFrog().moveDown());
                break;
            default:
                break;
        }
    }

    public void playGame() throws IOException {
    //later to run with the state pattern
    //TODO: change velocity according ot the level
    int FPSGame = 5;
    int frameTimeGame = 1000/FPSGame;

    while(true) {
        long startTime = System.currentTimeMillis();

        this.draw();
        KeyStroke key = screen.pollInput();
        this.processKey(key);

        if (key != null && key.getKeyType() == KeyType.Character && key.getCharacter() == 'q' && key.getCharacter() == 'Q') {
            screen.close();
            break;
        }
        else if (key != null && key.getKeyType() == KeyType.EOF)
            break;
        arena.moveMovableElements();

        long elapsedTimeGame = System.currentTimeMillis() - startTime;
        long sleepTime = frameTimeGame - elapsedTimeGame;

        try {
            if (sleepTime > 0) Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        }
    }
}
