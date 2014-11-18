package contagious.apps.mediaremote.phone;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lge.qpair.api.r1.IPeerContext;
import com.lge.qpair.api.r1.IPeerIntent;
import com.lge.qpair.api.r1.QPairConstants;


public class MainActivity extends Activity {

    public class QPairServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            IPeerContext peerContext = IPeerContext.Stub.asInterface(service);

            try {
                IPeerIntent peerIntent = peerContext.newPeerIntent();

                peerIntent.setAction("contagious.apps.mediaremote.tablet.MediaRemoteBroadcastReceiver");
                peerIntent.putStringExtra("ACTION", toggle ? "PLAY" : "PAUSE");

                IPeerIntent callback = peerContext.newPeerIntent();

                callback.setAction(CALLBACK_ACTION);
                peerContext.sendBroadcastOnPeer(peerIntent,callback);


            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // unbind the QPair service.
            unbindService(this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    }

    private Boolean toggle;
    private String CALLBACK_ACTION = getClass().getPackage().getName() + "ACTION_CALLBACK";
    private BroadcastReceiver callbackReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callbackReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String error = "Error: " + intent.getStringExtra(QPairConstants.EXTRA_CAUSE);
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
        };
        registerReceiver(callbackReceiver, new IntentFilter(CALLBACK_ACTION));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(callbackReceiver);
        super.onDestroy();
    }

    public void onPlayPauseClick(View view) {

        if (((ToggleButton) view).isChecked())
            toggle = true;
        else
            toggle = false;
        Intent intent = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
        bindService(intent, new QPairServiceConnection(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
