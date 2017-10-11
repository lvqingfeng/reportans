package com.juyikeji.myappjubao.utils.imagecache;

import android.view.View;

public interface MyScrollInterface {
	abstract void scrollStart(boolean direction); // true is right ,false if left
	abstract void scrollEnd(); //滑动完成
}
