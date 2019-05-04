package com.ssimo.remind;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.animation.AnimationUtils;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

//Android original CustomHideBottomViewOnScrollBehavior.java (I just set slideUp and slideDown public so I can show and hide bottom app bar)
public class CustomHideBottomViewOnScrollBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    private static final int ENTER_ANIMATION_DURATION = 225;
    private static final int EXIT_ANIMATION_DURATION = 175;

    private int height = 0;

    /** Default constructor for instantiating HideBottomViewOnScrollBehaviors. */
    CustomHideBottomViewOnScrollBehavior() {}

    /**
     * Default constructor for inflating HideBottomViewOnScrollBehaviors from layout.
     *
     * @param context The {@link Context}.
     * @param attrs The {@link AttributeSet}.
     */
    public  CustomHideBottomViewOnScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public  boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull V child, int layoutDirection) {
        height = child.getMeasuredHeight();
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    void slideUp(V child) {
        child.clearAnimation();
        child
                .animate()
                .translationY(0)
                .setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR)
                .setDuration(ENTER_ANIMATION_DURATION);
    }

    void slideDown(V child) {
        child.clearAnimation();
        child
                .animate()
                .translationY(height)
                .setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR)
                .setDuration(EXIT_ANIMATION_DURATION);
    }
}