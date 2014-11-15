package contagious.apps.mediaremote.phone;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import com.lge.qpair.api.r1.IPeerContext;
import com.lge.qpair.api.r1.IPeerIntent;
import com.lge.qpair.api.r1.QPairConstants;


public class MainActivity extends Activity {

    private Boolean toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onToggleClicked(View view){

        if(((ToggleButton)view).isChecked())
            toggle=true;
        else
            toggle=false;
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
    public class QPairServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            IPeerContext iPeerContext = IPeerContext.Stub.asInterface(service);
            try {
                    IPeerIntent peerIntent = iPeerContext.newPeerIntent();
                    peerIntent.setPackage("contagious.apps.mediaremote.tablet.PhoneService");

                    if(toggle)
                    peerIntent.putStringExtra("ACTION", "PLAY");
                    else
                    peerIntent.putStringExtra("ACTION","PAUSE");

                    IPeerIntent callback = iPeerContext.newPeerIntent();
                    callback.setAction("contagious.apps.mediaremote.tablet.ACTION_CALLBACK");
                    iPeerContext.startServiceOnPeer(peerIntent, callback);
                      }
                catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(this);

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    }
}
