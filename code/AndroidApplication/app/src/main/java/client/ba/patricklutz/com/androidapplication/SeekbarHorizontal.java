package client.ba.patricklutz.com.androidapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;


/**
 * Own Implementation of Seekbar
 *
 * @author privat-patrick
 */
public class SeekbarHorizontal extends SeekBar {

    public SeekbarHorizontal(Context context) {
        super(context);
    }

    public SeekbarHorizontal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SeekbarHorizontal(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setProgress((int) (getMax() * event.getX() / getWidth()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_UP:
                setProgress(getMax() / 2);
                onSizeChanged(getWidth(), getHeight(), 0, 0);
        }
        return true;
    }
}