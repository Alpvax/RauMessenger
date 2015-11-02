package alpvax.rau.login;

import android.graphics.Color;

/**
 * Created by Nick on 16/10/15.
 */
public class User
{
    private String name;
    private int colour;

    public User(String name)
    {
        this.name = name;
    }

    public CharSequence getName()
    {
        return name;
    }

    public User setColour(int r, int g, int b)
    {
        colour = Color.argb(255, r, g, b);
        return this;
    }

    public int getColour()
    {
        return colour;
    }
}
