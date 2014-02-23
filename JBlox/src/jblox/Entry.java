package jblox;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-nov-25
 * @version 1.0
 * 
 * -Djava.library.path=C:\Users\nti\Documents\GitHub\JBlox\lwjgl-2.9.0\native\windows
 * -Djava.library.path=F:\GitHub\JBlox\lwjgl-2.9.0\native\windows
 * 
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

