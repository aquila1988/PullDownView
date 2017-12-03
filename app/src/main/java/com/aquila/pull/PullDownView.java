package com.aquila.pull;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;


public class PullDownView extends FrameLayout {

    private static int timeInterval = 400;

    private GestureDetector mGestureDetector;
    private Scroller mScroller;
    private int scrollType;

    private int bottomViewInitializeVisibility = View.INVISIBLE;
    private int topViewInitializeVisibility = View.INVISIBLE;

    private boolean isInit = false;

    private OnRefreshAdapterDataListener mOnRefreshAdapterDataListener;
    private int topViewHeight;
    private int bottomViewHeight;

    private boolean isScrollToTop = false;
    private boolean isScrollFarTop = false;
    private boolean isMoveTop = false;
    private boolean isMoveDown = false;
    private boolean isScrollStoped = false;
    private boolean isFristTouch = true;
    private boolean isHideTopView = false;
    private boolean isCloseTopAllowRefersh = true;
    private boolean hasbottomViewWithoutscroll = true;

    private OnListViewBottomListener mOnListViewBottomListener;
    private OnListViewTopListener mOnListViewTopListener;

    private View topView;
    private View bottomView;
    private Context context;

    private boolean isRefreshData = false;
    private final static int MSG_TYPE_0 = 0;
    private final static int MSG_TYPE_1 = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (scrollType) {
                case MSG_TYPE_0:
                    if (mOnRefreshAdapterDataListener != null) {
                        isRefreshData = true;
                        mOnRefreshAdapterDataListener.refreshData();
                    }
                    if (topView.getVisibility() != View.VISIBLE) {
                        break;
                    }
                    scrollTo(0, topViewHeight);
                    break;
                case 1:
                    if (bottomView.getVisibility() != View.VISIBLE) {
                        break;
                    }
                    scrollTo(0, bottomViewHeight);
                    break;

            }
            startScroll();
        }
    };

    public PullDownView(Context context) {
        this(context, null);
    }

    public PullDownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullDownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.mScroller = new Scroller(context, new AccelerateInterpolator());
        this.mGestureDetector = new GestureDetector(onGestureListener);
    }


    public final void startTopScroll() {
        if (!this.isCloseTopAllowRefersh) {
            if (this.topView.getVisibility() == View.INVISIBLE) {
                this.mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + this.topViewHeight, 200);
            }
            if (this.topView.getVisibility() == View.VISIBLE) {
                this.mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 200);
            }
            this.scrollType = 0;
            this.isScrollStoped = true;
            this.isFristTouch = false;
        } else {
            this.mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + this.topViewHeight, 200);
        }
        postInvalidate();
    }

    public final void setOnRefreshAdapterDataListener(OnRefreshAdapterDataListener paramcs) {
        this.mOnRefreshAdapterDataListener = paramcs;
    }

    public final void setOnListViewTopListener(OnListViewTopListener paramei) {
        this.mOnListViewTopListener = paramei;
    }

    public final void setOnListViewBottomListener(OnListViewBottomListener parames) {
        this.mOnListViewBottomListener = parames;
    }

    public final void setIsCloseTopAllowRefresh(boolean paramBoolean) {
        this.isCloseTopAllowRefersh = paramBoolean;
    }

    public final void setHasBottomViewWithoutscroll(boolean paramBoolean) {
        this.hasbottomViewWithoutscroll = paramBoolean;
    }

    public final void setTopViewInitialize(boolean paramBoolean) {
        this.topViewInitializeVisibility = paramBoolean ? VISIBLE : INVISIBLE;
        if (this.topView != null) {
            this.topView.setVisibility(this.topViewInitializeVisibility);
        }
    }

    OnGestureListener onGestureListener = new OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            isMoveTop = true;
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {
            int scrollHeight = -1;
            int j = 1;
            isMoveDown = distanceY > 0.0F;
            int halfDistanceY;

            if ((!isMoveDown || !isScrollFarTop) && (isMoveDown || (getScrollY() - topViewHeight <= 0) || !isScrollFarTop)) {

                if (((isMoveDown) || (!isScrollToTop)) && ((!isMoveDown) || (getScrollY() - topViewHeight >= 0) || (!isScrollToTop))) {
                    j = 0;
                } else {
                    halfDistanceY = (int) (0.5D * distanceY);
                    if (halfDistanceY != 0) {
                        scrollHeight = halfDistanceY;
                    } else if (distanceY > 0.0F) {
                        scrollHeight = j;
                    }
                    if (scrollHeight > topViewHeight - getScrollY()) {
                        scrollHeight = topViewHeight - getScrollY();
                    }
                    scrollBy(0, scrollHeight);
                    return true;
                }

            } else {
                halfDistanceY = (int) (0.5D * distanceY);
                if (halfDistanceY != 0) {
                    scrollHeight = halfDistanceY;
                } else if (distanceY > 0.0F) {
                    scrollHeight = j;
                }
                if ((scrollHeight + getScrollY() < topViewHeight) && (!isMoveDown)) {
                    scrollHeight = topViewHeight - getScrollY();
                }
                scrollBy(0, scrollHeight);
                return true;
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {}

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

    };

    private void startScroll() {
        if (getScrollY() - this.topViewHeight < 0) {
            if (!this.isCloseTopAllowRefersh) {
                if (this.topView.getVisibility() == View.INVISIBLE) {
                    this.mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + this.topViewHeight, 200);
                } else if (this.topView.getVisibility() == View.VISIBLE) {
                    this.mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 200);
                }
                this.scrollType = 0;
                this.isScrollStoped = true;
                this.isFristTouch = false;
            } else {
                this.mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + this.topViewHeight, 200);
            }
            postInvalidate();
        }
        if (getScrollY() > this.bottomViewHeight) {
            if (!this.hasbottomViewWithoutscroll) {
                if (this.bottomView.getVisibility() == View.INVISIBLE) {
                    this.mScroller.startScroll(0, getScrollY(), 0, this.bottomViewHeight - getScrollY(), 200);
                }
                if (this.bottomView.getVisibility() == View.VISIBLE) {
                    this.mScroller.startScroll(0, getScrollY(), 0, this.bottomViewHeight - getScrollY() + this.bottomViewHeight, 200);
                }
                this.scrollType = 1;
                this.isScrollStoped = true;
                this.isFristTouch = false;
            } else {
                this.mScroller.startScroll(0, getScrollY(), 0, this.bottomViewHeight - getScrollY(), 200);
            }
            postInvalidate();
        }
        this.isMoveTop = false;
        this.isMoveDown = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
        boolean flag = true;
        if (this.isFristTouch) {
            if (this.mOnListViewTopListener != null) {
                this.isScrollToTop = mOnListViewTopListener.getIsListViewToTop();
            } else {
                this.isScrollToTop = false;
            }

            if (this.mOnListViewBottomListener != null) {
                this.isScrollFarTop = this.mOnListViewBottomListener.getIsListViewToBottom();
            } else {
                this.isScrollFarTop = false;
            }

            if (this.topViewInitializeVisibility == View.VISIBLE) {
                if (!this.isCloseTopAllowRefersh) {
                    this.topView.setVisibility(View.VISIBLE);
                } else {
                    this.topView.setVisibility(View.INVISIBLE);
                }
            }

            if (this.bottomViewInitializeVisibility == View.VISIBLE) {
                if (!this.hasbottomViewWithoutscroll) {
                    this.bottomView.setVisibility(VISIBLE);
                } else {
                    this.bottomView.setVisibility(INVISIBLE);
                }
            }

            if (paramMotionEvent.getAction() != MotionEvent.ACTION_UP) {
                if (paramMotionEvent.getAction() != MotionEvent.ACTION_CANCEL) {
                    if (!this.mGestureDetector.onTouchEvent(paramMotionEvent)) {
                        flag = super.dispatchTouchEvent(paramMotionEvent);
                    } else {
                        paramMotionEvent.setAction(MotionEvent.ACTION_CANCEL);
                        flag = super.dispatchTouchEvent(paramMotionEvent);
                    }
                } else {
                    startScroll();
                }
            } else {
                startScroll();
                flag = super.dispatchTouchEvent(paramMotionEvent);
            }
        }
        return flag;

    }

    public void setRefreshOver() {
        scrollTo(0, topViewHeight);
        isRefreshData = false;
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (!this.mScroller.computeScrollOffset()) {
            if (this.isScrollStoped) {
                this.isScrollStoped = false;
                if (!isRefreshData) {
                    isRefreshData = true;
                    if (mHandler.hasMessages(MSG_TYPE_0)) {
                        mHandler.removeMessages(MSG_TYPE_0);
                    }
                    this.mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_TYPE_0), timeInterval);
                }
            }
        } else {
            scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            postInvalidate();
        }
        isFristTouch = this.mScroller.isFinished();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        if (!isInit) {
            View localView2 = inflate(this.context, R.layout.loading_view, null);
            View localView1 = inflate(this.context, R.layout.loading_view, null);
            addView(localView2, 0, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            addView(localView1, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            isInit = true;
        }
        int childCount = getChildCount();
        int childViewIndex = 0;
        int height = 0;
        while (true) {
            if (childViewIndex >= childCount) {
                this.topView = getChildAt(0);
                this.bottomView = getChildAt(getChildCount() - 1);
                this.topView.setVisibility(View.INVISIBLE);
                this.bottomView.setVisibility(View.INVISIBLE);
                this.topViewHeight = this.topView.getHeight();
                this.bottomViewHeight = this.bottomView.getHeight();
                if ((!this.isHideTopView) && (this.topViewHeight != 0)) {
                    this.isHideTopView = true;
                    scrollTo(0, this.topViewHeight);
                }
                return;
            }
            View localView3 = getChildAt(childViewIndex);
            int localView3MeasuredHeight = localView3.getMeasuredHeight();
            if (localView3.getVisibility() != View.GONE) {
                localView3.layout(0, height, localView3.getMeasuredWidth(), height + localView3MeasuredHeight);
                height += localView3MeasuredHeight;
            }
            childViewIndex++;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP){
            if (getScrollY() - this.topViewHeight < 0) {
                this.isScrollToTop = true;
            }
            if (getScrollY() > this.bottomViewHeight) {
                this.isScrollFarTop = true;
            }
            startScroll();
        }
        return true;
    }
}
