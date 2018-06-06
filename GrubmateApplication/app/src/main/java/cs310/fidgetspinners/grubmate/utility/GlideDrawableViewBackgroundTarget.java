package cs310.fidgetspinners.grubmate.utility;

import android.view.View;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;

/**
 * Created by anish on 11/4/17.
 */

public class GlideDrawableViewBackgroundTarget extends ViewBackgroundTarget<GlideDrawable> {
    private int maxLoopCount;
    private GlideDrawable resource;
    public GlideDrawableViewBackgroundTarget(View view) { this(view, GlideDrawable.LOOP_FOREVER); }
    public GlideDrawableViewBackgroundTarget(View view, int maxLoopCount) {
        super(view);
        this.maxLoopCount = maxLoopCount;
    }

    @Override public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
        super.onResourceReady(resource, animation);
        this.resource = resource;
        resource.setLoopCount(maxLoopCount);
        resource.start();
    }

    @Override protected void setResource(GlideDrawable resource) { setBackground(resource); }
    @Override public void onStart() { if (resource != null) { resource.start(); } }
    @Override public void onStop() { if (resource != null) { resource.stop(); } }
}
