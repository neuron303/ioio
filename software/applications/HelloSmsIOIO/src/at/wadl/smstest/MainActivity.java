package at.wadl.smstest;

import ioio.examples.hello.R;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ToggleButton;

/**
 * This is the main activity of the HelloIOIO example application.
 * 
 * It displays a toggle button on the screen, which enables control of the
 * on-board LED. This example shows a very simple usage of the IOIO, by using
 * the {@link IOIOActivity} class. For a more advanced use case, see the
 * HelloIOIOPower example.
 */
public class MainActivity extends IOIOActivity {
	private ToggleButton m_Button;
	private BroadcastReceiver m_SmsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	MainActivity.this.receivedSms(intent);
        }
    };
    //= new SmsReceiver();

    private void receivedSms(Intent intent)
    {
    	
    }
	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		m_Button = (ToggleButton) findViewById(R.id.button);
		IntentFilter smsReceiveIntentFilter = new IntentFilter();
		smsReceiveIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		smsReceiveIntentFilter.setPriority(999);
		this.registerReceiver(m_SmsReceiver, smsReceiveIntentFilter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(m_SmsReceiver);
	}

	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class Looper extends BaseIOIOLooper {
		/** The on-board LED. */
		private DigitalOutput led_;
		private DigitalOutput relais_;
		private boolean state_;
		private float onTime_;

		/**
		 * Called every time a connection with IOIO has been established.
		 * Typically used to open pins.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
		 */
		@Override
		protected void setup() throws ConnectionLostException {
			onTime_ = 0;
			led_ = ioio_.openDigitalOutput(0, true);
			relais_ = ioio_.openDigitalOutput(16, true);
		}

		/**
		 * Called repetitively while the IOIO is connected.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
		 */
		@Override
		public void loop() throws ConnectionLostException {
			/*long time = 30;
			onTime_+=0.01;
			if(onTime_ > 1.0)
				onTime_ = 0;
			
			led_.write(true);
			try {
				Thread.sleep((long)(onTime_*time));
			} catch (InterruptedException e) {
			}
			led_.write(!button_.isChecked());
			try {
				Thread.sleep((long)((1.0-onTime_)*time));
			} catch (InterruptedException e) {
			}*/
			led_.write(!m_Button.isChecked());
			relais_.write(m_Button.isChecked());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
}