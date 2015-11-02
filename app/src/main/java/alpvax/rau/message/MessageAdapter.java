package alpvax.rau.message;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Map;

import alpvax.rau.R;
import alpvax.rau.application.RauApplication;
import alpvax.rau.login.User;
import alpvax.rau.message.MessageAdapter.Message;

/**
 * Created by Nick on 14/10/15.
 */
public class MessageAdapter extends ArrayAdapter<Message> {

    private static final String TAG = MessageAdapter.class.getSimpleName();
    Context context;

    public MessageAdapter(Context context)
    {
        super(context, R.layout.message_element);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        MessageHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(R.layout.message_element, parent, false);

            holder = new MessageHolder();
            holder.txtName = (TextView)row.findViewById(R.id.messageAuthor);
            holder.txtDate = (TextView)row.findViewById(R.id.messageTime);
            holder.txtBody = (TextView)row.findViewById(R.id.messageBody);

            row.setTag(holder);
        }
        else
        {
            holder = (MessageHolder)row.getTag();
        }

        Message message = getItem(position);
        User user = RauApplication.instance().getUser(message.author);
        holder.txtName.setText(user != null ? user.getName() : message.author);
        if(user != null)
        {
            holder.txtName.setTextColor(user.getColour());
        }
        holder.txtDate.setText(DateUtils.formatDateTime(context, message.time, DateUtils.FORMAT_SHOW_DATE & DateUtils.FORMAT_SHOW_TIME));
        holder.txtBody.setText(message.body);

        return row;
    }

    public void addMessage(String key, Map<String, Object> data)
    {
        if(!data.containsKey("time"))
        {
            Log.d(TAG, key + ": " + data.toString());
        }
        add(new Message(key, data));
    }

    public void removeMessage(String key) {
        remove(new Message(key));
    }

    public void modifyMessage(String key, Map<String, Object> data) {
        Message old = new Message(key);
        int i = getPosition(old);
        remove(old);
        insert(new Message(key, data), i);
    }

    static class MessageHolder
    {
        TextView txtName;
        TextView txtDate;
        TextView txtBody;
    }

    public class Message
    {
        private final String key;
        private String author;
        private long time;
        private String body;

        private Message(String key)
        {
            this.key = key;
            author = null;
            time = -1L;
            body = null;
        }

        private Message(String key, String author, long time, String body)
        {
            this(key);
            this.author = author;
            this.time = time;
            this.body = body;
        }

        private Message(String key, Map<String, Object> data)
        {
            this(key, (String)data.get("user"), (long)data.get("time"), (String)data.get("text"));
        }

        @Override
        public boolean equals(Object other)
        {
            return other instanceof Message && ((Message)other).key == key;
        }

        @Override
        public int hashCode()
        {
            return key.hashCode();
        }

        @Override
        public String toString()
        {
            return String.format("%s: {%s @ %s: %s}", key, author, DateUtils.formatDateTime(context, time, DateUtils.FORMAT_SHOW_DATE & DateUtils.FORMAT_SHOW_TIME), body);
        }
    }
}
