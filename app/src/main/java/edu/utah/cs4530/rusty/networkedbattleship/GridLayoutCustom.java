package edu.utah.cs4530.rusty.networkedbattleship;

import android.content.Context;
import android.graphics.Rect;
import android.view.ViewGroup;

/**
 * Created by Rusty on 10/24/2016.
 */
public class GridLayoutCustom extends ViewGroup implements GridSpaceView.OnGridSpaceTouchedListener {
    /**
     * _gridWidth should represent the number of columns and rows.
     * These should always be the same.
     */
    int _gridWidth;

    public GridLayoutCustom(Context context) {
        super(context);
    }

    public interface OnMissileFiredListener {
        void onMissileFired(int index);
    }

    OnMissileFiredListener _onMissileFiredListener = null;

    public void setOnMissileFiredListener(OnMissileFiredListener listener) {
        _onMissileFiredListener = listener;
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int childCount = getChildCount();
        _gridWidth = (int) Math.sqrt((double) childCount);

        int childWidth = getWidth() / _gridWidth;
        int childHeight  = getHeight() / _gridWidth;

        for (int childIndex = 0; childIndex < childCount; childIndex++) {

            Rect childRect = new Rect();
            childRect.left = (childIndex % _gridWidth) * childWidth;
            childRect.right = childRect.left + childWidth;
            childRect.top = (childIndex / _gridWidth) * childHeight;
            childRect.bottom = childRect.top + childHeight;

            GridSpaceView childView;
            childView = (GridSpaceView) getChildAt(childIndex);
            childView.setOnGridSpaceTouchedListener(this);
            childView.setId(childIndex);
            childView.layout(childRect.left, childRect.top, childRect.right, childRect.bottom);
        }
    }

    @Override
    public void onGridSpaceTouched(GridSpaceView gridSpaceView) {
        _onMissileFiredListener.onMissileFired(gridSpaceView.getId());
    }

    public void setSpaceColor (int space, int color) {
        GridSpaceView gsv = (GridSpaceView) getChildAt(space);
        gsv.setColor(color);
    }
}
