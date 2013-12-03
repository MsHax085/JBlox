package jblox;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-nov-25
 * @version 1.0
 */
public class Entry {
    
    private final ClientDisplay clientDisplay;

    public Entry() {
        clientDisplay = new ClientDisplay();
        clientDisplay.start();
    }
    
    public static void main(final String[] args) {
        final Entry entry = new Entry();
    }
}

