package alpvax.rau.application;

import android.app.Application;
import android.content.res.Configuration;

import com.facebook.FacebookSdk;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 13/10/15.
 */
public class RauApplication extends Application
{

    private static RauApplication instance;
    private Firebase rootRef;

    public static RauApplication instance()
    {
        return instance;
    }

    public Firebase getFirebaseRef()
    {
        return rootRef;
    }
    public Firebase getFirebaseRef(String... childPath)
    {
        StringBuilder s = new StringBuilder();
        for(String child : childPath)
        {
            s.append("/").append(child);
        }
        return getFirebaseRef().child(s.toString());
    }

    public String getUser()
    {
        return rootRef.getAuth().getUid();
    }

    public void sendMessage(CharSequence text, String conversation)
    {
        //TODO:Modify any input to allow for rau
        String user = getUser();
        Firebase ref = rootRef.child("messaging/" + conversation).push();
        ref.child("user").setValue(user);
        ref.child("time").setValue(ServerValue.TIMESTAMP);
        ref.child("text").setValue(text.toString());
        ref.child("read/" + user).setValue("true");
        ref.setPriority(ServerValue.TIMESTAMP);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Firebase.setAndroidContext(this);
        rootRef = new Firebase("https://rau.firebaseio.com");
        rootRef.addAuthStateListener(new AuthChangedListener());
        FacebookSdk.sdkInitialize(this);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
    }

    class AuthChangedListener implements Firebase.AuthStateListener
    {
        @Override
        public void onAuthStateChanged(AuthData authData)
        {
            if(authData!=null)
            {
                Firebase ref=RauApplication.instance().getFirebaseRef("users",authData.getUid());
                ref.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(!dataSnapshot.exists())
                        {
                            /*ref.set({provider:authData.provider,
                            name:function(authData)
                            {
                                var n=authData[authData.provider].displayName;
                                return prompt("Enter your name",n)||n;
                            }(authData),
                                access:"basic",
                                colour:{r:r,g:g,b:b}
                            });*/
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError)
                    {

                    }
                });
            }
        }
    }
}
