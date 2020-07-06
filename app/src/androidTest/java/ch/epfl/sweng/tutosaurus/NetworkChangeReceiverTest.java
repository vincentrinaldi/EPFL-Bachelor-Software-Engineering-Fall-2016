package ch.epfl.sweng.tutosaurus;

import android.widget.Button;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.sweng.tutosaurus.network.NetworkChangeReceiver;

/**
 * Created by Stephane on 12/11/2016.
 */

public class NetworkChangeReceiverTest {

    private NetworkChangeReceiver receiver;
    private Button button1;
    private Button button2;
    private ArrayList<Button> buttons;
    @Before
    public void setUp() throws Exception {
        receiver = new NetworkChangeReceiver();
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkNullTextViewThrowsException() {
        receiver.setNetStatusTextView(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkNullButtonsThrowsException() {
        receiver.setButtonsToManage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkNullActivityThrowsException() {
        receiver.setActivity(null);
    }


}
