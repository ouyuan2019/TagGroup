package com.ozy.taggroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WORK on 2016/12/8.
 */

public class TagGroup extends ViewGroup {

    private Context mContext;

    //垂直间距
    private float mVerticalSpacing;

    //水平间距
    private float mHorizontalSpacing;

    private int mMaxRow = 10;

    private List tags = new ArrayList();


    private int itemLayoutId = 0;


    public TagGroup(Context context) {
        this(context, null);
    }

    public TagGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TagGroup);
        mVerticalSpacing = a.getDimension(R.styleable.TagGroup_tgp_vertical_spacing, 10);
        mHorizontalSpacing = a.getDimension(R.styleable.TagGroup_tgp_horizontal_spacing, 10);
        mMaxRow = a.getInt(R.styleable.TagGroup_tgp_max_row, 10);
        itemLayoutId = a.getResourceId(R.styleable.TagGroup_tgp_item_layout, R.layout.item_tag);
        a.recycle();
        this.mContext = context;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();
        final int parentRight = r - l - getPaddingRight();
        final int parentBottom = b - t - getPaddingBottom();

        int childLeft = parentLeft;
        int childTop = parentTop;

        int row = 0;

        int maxHeight = 0;

        final int count = getChildCount();

        for (int i = 0; i < count; i++) {

            if (row >= mMaxRow) {
                return;
            }
            final View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();
            if (child.getVisibility() != GONE) {

                //子控件宽度大于 父控件
                if (childLeft + width > parentRight) {
                    row++;
                    //换行
                    childLeft = parentLeft;
                    childTop += maxHeight + mVerticalSpacing;
                    maxHeight = height;
                } else {
                    maxHeight = Math.max(maxHeight, height);
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
                //每一行子控件总宽度
                childLeft += width + mHorizontalSpacing;
            }

        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;
        int row = 0;
        int rowWidth = 0;
        int rowMaxHeight = 0;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {

            if (row >= mMaxRow) {
                break;
            }

            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                rowWidth += childWidth + mHorizontalSpacing; //每个字控件的宽度
                //子控件的宽度大于父控件换行
                if (rowWidth > widthSize) {
                    rowWidth = childWidth;
                    height += rowMaxHeight + mVerticalSpacing;
                    rowMaxHeight = childHeight;
                    row++;
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight);
                }


            }
        }
        height += rowMaxHeight;

        height += getPaddingTop() + getPaddingBottom();


        if (row == 0) {
            width = rowWidth;
            width += getPaddingLeft() + getPaddingRight();
        } else {
            width = widthSize;
        }
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }


    public TagGroup setData(List tags) {
        this.tags = tags;
        return this;
    }

    public void show() {
        removeAllViews();
        for (int i = 0; i < tags.size(); i++) {
            final View view = LayoutInflater.from(mContext).inflate(itemLayoutId, null, false);
            TextView textView = (TextView) view.findViewById(R.id.tag);
            textView.setText(tags.get(i).toString());

            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTagItemClickListener != null) {
                        onTagItemClickListener.onClick(view, finalI, tags.get(finalI));
                    }
                }
            });
            addView(view);
        }
    }

    public List getList() {
        return tags;
    }

    public void setCheckTag(int position) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (i == position) {
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                view.setSelected(true);
                view.requestFocus();
            } else {
                view.setSelected(false);
                view.setFocusable(false);
                view.setFocusableInTouchMode(false);
                view.clearFocus();
            }
        }
    }




    public interface OnTagItemClickListener<T> {
        void onClick(View view, int position, T tag);
    }


    private OnTagItemClickListener onTagItemClickListener;

    public void setOnTagItemClickListener(OnTagItemClickListener onTagItemClickListener) {
        this.onTagItemClickListener = onTagItemClickListener;
    }


}
