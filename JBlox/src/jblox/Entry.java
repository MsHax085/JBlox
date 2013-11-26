package jblox;

/**
 *
 * @author Richard
 * @since 2013-nov-25
 * @version 1.0
 */
public class Entry {
    
    private final ClientDisplay clientDisplay;
    private final ClientInput clientInput;

    public Entry() {
        clientDisplay = new ClientDisplay(this);
        clientDisplay.start();
        
        clientInput = new ClientInput();
    }
    
    public static void main(final String[] args) {
        final Entry entry = new Entry();
    }
    
    // -------------------------------------------------------------------------
    
    public ClientInput getClientInput() {
        return clientInput;
    }
}

