package in.appdoor.metroalarm;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, 
			GooglePlayServicesClient.OnConnectionFailedListener,
			LocationListener  {

	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	
	private LocationClient mLocationClient;
	// Global variable to hold the current location
    private Location mCurrentLocation;
    
    private LocationRequest mLocationRequest;
    
    private WakeLock wl;
    
    private Vibrator v;
    
    private AlertDialog alertDialog;
    
    private Uri notification;
    private Ringtone r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(StationRepo.getInstance().getStations() == null) {
			StationRepo.init(this);
		}
        
        setContentView(R.layout.activity_main);
        
        this.v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        
        mLocationClient = new LocationClient(this, this, this);
		
		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create();
		
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

		// Connect the client.
		if(!mLocationClient.isConnected()) {			
			mLocationClient.connect();
		}
		
		ListView listStations = (ListView) findViewById(R.id.listStations);

        StationListAdapter adapter = new StationListAdapter(this, android.R.layout.simple_list_item_1, StationRepo.getInstance().getStations());
        
    	// Assign adapter to ListView
        listStations.setAdapter(adapter); 
    }

    /*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		if(wl == null) {
			wl = ((PowerManager)getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HelloMap");
			wl.acquire();
		}
		
	}
	
	@SuppressLint("Wakelock")
	@Override
	protected void onDestroy() {
		wl.release();
		// If the client is connected
		if (mLocationClient.isConnected()) {
			// Remove location updates for a listener.
			// The current activity is the listener, so 
			// the argument is "this".
			mLocationClient.removeLocationUpdates(this);
		}
				
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onDestroy();
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    // Define a DialogFragment that displays the error dialog
 	public static class ErrorDialogFragment extends DialogFragment {
 		// Global field to contain the error dialog
 		private Dialog mDialog;

 		// Default constructor. Sets the dialog field to null
 		public ErrorDialogFragment() {
 			super();
 			mDialog = null;
 		}

 		// Set the dialog to display
 		public void setDialog(Dialog dialog) {
 			mDialog = dialog;
 		}

 		// Return a dialog to the DialogFragment
 		@Override
 		public Dialog onCreateDialog(Bundle savedInstanceState) {
 			return mDialog;
 		}
 	}
 	
 	/**
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			switch (resultCode) {
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			case Activity.RESULT_OK:
				/*
				 * Try the request again.
				 */
				break;
			}
		}
	}
	
	private void showErrorDialog(int errorCode) {
		// Get the error code
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
				this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {
			// Create a new DialogFragment for the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();
			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);
			// Show the error dialog in the DialogFragment
			errorFragment.show(getFragmentManager(), "Location Updates");
		}
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates.
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}
	
	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play Services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log exception
				e.printStackTrace();
			}
		} else {
			showErrorDialog(connectionResult.getErrorCode());
		}
	}
	
	// Define the callback method that receives location updates
	@Override
	public void onLocationChanged(Location location) {
		
		mCurrentLocation = location;
		
		List<Station> activeStations = StationRepo.getInstance().getStationsWithAlarm();
		float[] results = new float[1];
		for(Station s : activeStations) {
			final Station station = s;
			Location.distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), station.getLat(), station.getLng(), results);
			if(results[0] < 1000) {
//				Toast.makeText(this, "You are close to " + s.getName(), Toast.LENGTH_LONG).show();
				// Vibrate for 500 milliseconds
				
				if(alertDialog == null) {
					// 1. Instantiate an AlertDialog.Builder with its constructor
					AlertDialog.Builder builder = new AlertDialog.Builder(this);

					// 2. Chain together various setter methods to set the dialog characteristics
					builder.setMessage("You are close to " + station.getName() + " station.");

					builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               alertDialog.dismiss();
				           }
				    });
					
					// 3. Get the AlertDialog from create()
					this.alertDialog = builder.create();
					this.alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							r.stop();
				            StationRepo.getInstance().deactivateStation(station.getName());
				            alertDialog = null;
						}
					});
				}
				
				if(!this.alertDialog.isShowing()) {
					notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
					r = RingtoneManager.getRingtone(getApplicationContext(), notification);
					r.play();
					this.alertDialog.show();
				}
				
			    
				v.vibrate(new long[]{200, 800, 200, 800}, -1);
				break;
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		if(StationRepo.getInstance().getStationsWithAlarm().size() > 0) {
			Toast.makeText(this, "Remove all alarms to exit. To hide this application and let it run in the background, please press the home button.", Toast.LENGTH_SHORT).show();
			return;
		} else {
			super.onBackPressed();
		}
	}

}
