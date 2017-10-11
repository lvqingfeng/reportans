package com.juyikeji.myappjubao.utils.imagecache;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 
 * @author zx.
 * 实现三个view无限循环
 */

public class ThreeGroup extends ViewGroup{
	
	private static final int MOVE_MIN_DISTANCE = 20;
	private int screenWidth;
	//private int screenHight;
	
	private int oldX;
	private Scroller mScroller;
	private int move_direction;
	private boolean scrolling; //是否正在滚动

	private MyScrollInterface mMyScrollInterface; //滑动监听事件
	
	public ThreeGroup(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ThreeGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public ThreeGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context){
		screenWidth = this.getResources().getDisplayMetrics().widthPixels;
		mScroller = new Scroller(getContext());	
	}
	
	public void setListener(MyScrollInterface listener){
		mMyScrollInterface = listener;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int childLeft = 0;
		int count = getChildCount();
		for(int i=0;i<count;i++){		
			View child =  getChildAt(i);
			final int childWidth = child.getMeasuredWidth();			
			getChildAt(i).layout(i*screenWidth, 0, childLeft+childWidth, child.getMeasuredHeight());			
			childLeft += childWidth;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);		
		int count = getChildCount();
		for(int i=0;i<count;i++){
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.EXACTLY);
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(screenWidth, 0);//jump to page 1

	}

	
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			invalidate();			// invalidate the View from a non-UI thread
		} 
		if(mScroller.isFinished()&&scrolling){
			scrolling = false;
			if(mMyScrollInterface!=null){
				mMyScrollInterface.scrollEnd();
			}
		}	
	}



	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					move_direction = 0;
					oldX = (int)event.getX();
					break;
				case MotionEvent.ACTION_MOVE:

					break;
				case MotionEvent.ACTION_UP:

					break;
			}
			return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:		
			move_direction = 0;
			oldX = (int)event.getX();
			break;			
		case MotionEvent.ACTION_MOVE:		
			if(!scrolling){
			    int x = (int)event.getX();
			    int step = x-oldX;
			    if(step>=MOVE_MIN_DISTANCE){// right
			    	scrollTo(getScrollX()-step, 0);
			    	oldX = (int)event.getX();
			    	move_direction =1;
			    	return true; 
			    }
			    else if(step<=-MOVE_MIN_DISTANCE){//left
			    	scrollTo(getScrollX()-step, 0);
			    	oldX = (int)event.getX();
			    	move_direction= 2;
			    	return true;
			    }
		    }
			break;			
		case MotionEvent.ACTION_UP:
			autoScroll(move_direction,getScrollX());
			break;	
		}
		return true;
	}
	
	private void autoScroll(int move_direction,int scrollx){		
		if(move_direction==1)
		{
			mScroller.startScroll(scrollx, 0, -scrollx, 0,1000);// auto first page
			scrolling = true;
			invalidate();
			if(mMyScrollInterface!=null){
				//向左滑动
				mMyScrollInterface.scrollStart(false);
			}
		}		
		else if(move_direction==2){		
			mScroller.startScroll(scrollx, 0, screenWidth*2-scrollx, 0,1000); //auto end page
			scrolling = true;
			invalidate();
			if(mMyScrollInterface!=null){
				//向右滑动
				mMyScrollInterface.scrollStart(true);
			}
		}			
	}
}
