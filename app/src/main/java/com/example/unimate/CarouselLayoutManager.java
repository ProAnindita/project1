package com.example.unimate;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CarouselLayoutManager extends LinearLayoutManager {

    // How much to shrink cards away from center
    private final float scaleDownBy = 0.20f;
    // The minimum alpha for non-center cards
    private final float minAlpha = 0.7f;

    public CarouselLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(@NonNull RecyclerView.Recycler recycler,
                                 @NonNull RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        scaleAndAlphaChildren();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler,
                                    RecyclerView.State state) {
        int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
        scaleAndAlphaChildren();
        return scrolled;
    }

    private void scaleAndAlphaChildren() {
        int center = getWidth() / 2;
        float midpoint = getWidth() / 2f;

        // 1) Find the child closest to the center so we can make it fully opaque and on top
        float closestDist = Float.MAX_VALUE;
        View centerChild = null;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == null) continue;

            float childCenter = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2f;
            float distance = Math.abs(center - childCenter);
            if (distance < closestDist) {
                closestDist = distance;
                centerChild = child;
            }
        }

        // 2) Now set scale, alpha, and Z for each child
        if (centerChild != null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child == null) continue;

                float childCenter = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2f;
                float distance = Math.abs(center - childCenter);
                float d1 = center;  // max distance we consider
                float ratio = distance / d1;

                // Scale: from 1 (center) down to (1-scaleDownBy) at edges
                float scale = 1.0f - (scaleDownBy * ratio);
                // Alpha: from 1 (center) down to minAlpha at edges
                float alpha = 1.0f - ((1.0f - minAlpha) * ratio);

                // If it's the center child, give it full alpha & higher Z
                if (child == centerChild) {
                    scale = 1.0f;
                    alpha = 1.0f;
                    child.setTranslationZ(10f); // put center card on top
                } else {
                    child.setTranslationZ(0f);  // send others behind
                }

                // Apply
                child.setScaleX(scale);
                child.setScaleY(scale);
                child.setAlpha(alpha);
            }
        }
    }
}
