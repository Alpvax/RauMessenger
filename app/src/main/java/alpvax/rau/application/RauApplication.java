package alpvax.rau.application;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import alpvax.rau.login.User;

/**
 * Created by Nick on 13/10/15.
 */
public class RauApplication extends Application
{
    private static final String TAG = RauApplication.class.getSimpleName();

    private static RauApplication instance;
    private Firebase rootRef;
    private Map<String, User> users = new HashMap<>();

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

    public String currentUId()
    {
        return rootRef.getAuth().getUid();
    }

    public User getUser(String uid)
    {
        return users.get(uid);
    }

    public void sendMessage(CharSequence text, String conversation)
    {
        //TODO:Modify any input to allow for rau
        String user = currentUId();
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("time", ServerValue.TIMESTAMP);
        map.put("text", text.toString());
        Map<String, Object> read = new HashMap<>();
        read.put(user, true);
        map.put("read", read);
        rootRef.child("messaging/" + conversation).push().setValue(map, ServerValue.TIMESTAMP);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
        Firebase.setAndroidContext(this);
        rootRef = new Firebase("https://rau.firebaseio.com");
        rootRef.addAuthStateListener(new AuthChangedListener());
        FacebookSdk.sdkInitialize(this);
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
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
                Log.d(TAG, "Logged in");
                RauApplication.instance().getFirebaseRef("users").addChildEventListener(new ChildEventListener()
                {
                    private void setUser(DataSnapshot dataSnapshot)
                    {
                        Map<String, Object> data = (Map<String, Object>)dataSnapshot.getValue();
                        User user = new User((String)data.get("name"));
                        Map<String, Long> c = (Map<String, Long>)data.get("colour");
                        user.setColour(c.get("r").intValue(), c.get("g").intValue(), c.get("b").intValue());
                        users.put(dataSnapshot.getKey(), user);
                    }

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        setUser(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {
                        setUser(dataSnapshot);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot)
                    {
                        users.remove(dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s)
                    {

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
