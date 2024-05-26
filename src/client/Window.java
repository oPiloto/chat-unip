package client;
import javax.swing.JFrame;

public abstract class Window extends JFrame {
    
    protected Window(String title, int x, int y, int width, int height, int closing) {
        setupFrame(title, x, y, width, height, closing);
        setupComponents();
        setVisible(true);
    }

    protected Window(String title, int width, int height, int closing) {
        this(title, -1, -1, width, height, closing);
    }

    private void setupFrame(String title, int x, int y, int width, int height, int closing) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(closing);
        if (x == -1 && y == -1) {
            setLocationByPlatform(true);
        } else {
            setLocation(x, y);
        }
    }

    protected abstract void setupComponents();
}
