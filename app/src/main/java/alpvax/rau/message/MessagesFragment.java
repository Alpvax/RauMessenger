package alpvax.rau.message;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import alpvax.rau.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MessagesFragment extends ListFragment {

    public String conversation = "broadcast";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            conversation = bundle.getString("conversation", conversation);
        }
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final MessageAdapter adapter = new MessageAdapter(getActivity());
        setListAdapter(adapter);
        new Firebase("https://rau.firebaseio.com/messaging").child(conversation).addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChild) {
                adapter.addMessage(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.removeMessage(dataSnapshot.getKey());
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                adapter.modifyMessage(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO edit if own message
    }
}
