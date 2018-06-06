package cs310.fidgetspinners.grubmate.utility;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

/**
 * Created by anish on 11/4/17.
 * https://github.com/bumptech/glide/issues/1475
 * https://github.com/bumptech/glide/issues/938
 * https://github.com/bumptech/glide/wiki/Custom-targets
 */

public abstract class ViewBackgroundTarget<Z> extends ViewTarget<View, Z> implements GlideAnimation.ViewAdapter {
    public ViewBackgroundTarget(View view) { super(view); }
    @Override public void onLoadCleared(Drawable placeholder) { setBackground(placeholder); }
    @Override public void onLoadStarted(Drawable placeholder) { setBackground(placeholder); }
    @Override public void onLoadFailed(Exception e, Drawable errorDrawable) { setBackground(errorDrawable); }
    @Override public void onResourceReady(Z resource, GlideAnimation<? super Z> glideAnimation) {
        if (glideAnimation == null || !glideAnimation.animate(resource, this)) {
            setResource(resource);
        }
    }
    @Override public void setDrawable(Drawable drawable) { setBackground(drawable); }
    @Override public Drawable getCurrentDrawable() { return view.getBackground(); }

    @SuppressWarnings("deprecation")
    protected void setBackground(Drawable drawable) {
//        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
//        } else {
            view.setBackgroundDrawable(drawable);
//        }
    }

    protected abstract void setResource(Z resource);
}

