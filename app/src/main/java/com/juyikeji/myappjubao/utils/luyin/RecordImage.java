package com.juyikeji.myappjubao.utils.luyin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;


public class RecordImage extends ImageView {
	public int vplay=1;

	private static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制
	private static final int RECORD_OFF = 0; // 不在录音
	private static final int RECORD_ON = 1; // 正在录音

	private Dialog mRecordDialog;
	private RecordImp mAudioRecorder;
	private Thread mRecordThread;
	private RecordListener listener;

	private int recordState = 0; // 录音状态
	public float recodeTime = 0.0f; // 录音时长，如果录音时间太短则录音失败
	private double voiceValue = 0.0; // 录音的音量值
	private boolean isCanceled = false; // 是否取消录音
	private float downY;

	private TextView dialogTextView;
	private ImageView dialogImg,iv_back;
	private Context mContext;

	public RecordImage(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public RecordImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public RecordImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context) {
		mContext = context;
//		this.setText("按住 说话");
	}

	public void setAudioRecord(RecordImp record) {
		this.mAudioRecorder = record;
	}

	public void setRecordListener(RecordListener listener) {
		this.listener = listener;
	}

	// 录音时显示Dialog
	private void showVoiceDialog(int flag) {
		if (mRecordDialog == null) {
			mRecordDialog = new Dialog(mContext, R.style.Dialogstyle);
			mRecordDialog.setContentView(R.layout.fragment_one_utils_dialog_record);
			dialogImg = (ImageView) mRecordDialog
					.findViewById(R.id.record_dialog_img);
			dialogTextView = (TextView) mRecordDialog
					.findViewById(R.id.record_dialog_txt);
		}
		switch (flag) {
		case 1:
			dialogImg.setImageResource(R.mipmap.record_cancel);
			dialogTextView.setText("松开手指可取消录音");
//			this.setText("松开手指 取消录音");
			break;

		default:
			dialogImg.setImageResource(R.mipmap.record_animate_01);
			dialogTextView.setText("向上滑动可取消录音");
//			this.setText("松开手指 完成录音");
			break;
		}
		dialogTextView.setTextSize(14);
		mRecordDialog.show();
	}

	// 录音时间太短时Toast显示
	private void showWarnToast(String toastText) {
		Toast toast = new Toast(mContext);
		View warnView = LayoutInflater.from(mContext).inflate(
				R.layout.fragment_one_utils_toast_warn, null);
		toast.setView(warnView);
		toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间
		toast.show();
	}

	// 开启录音计时线程
	private void callRecordTimeThread() {
		mRecordThread = new Thread(recordThread);
		mRecordThread.start();
	}

	// 录音Dialog图片随录音音量大小切换
	private void setDialogImage() {
		if (voiceValue < 600.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_01);
		} else if (voiceValue > 600.0 && voiceValue < 1000.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_02);
		} else if (voiceValue > 1000.0 && voiceValue < 1200.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_03);
		} else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_04);
		} else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_05);
		} else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_06);
		} else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_07);
		} else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_08);
		} else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_09);
		} else if (voiceValue > 4000.0 && voiceValue < 6000.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_10);
		} else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_11);
		} else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_12);
		} else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_13);
		} else if (voiceValue > 12000.0) {
			dialogImg.setImageResource(R.mipmap.record_animate_14);
		}
	}

	// 录音线程
	private Runnable recordThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (recordState == RECORD_ON) {
				
					try {
						Thread.sleep(100);
						recodeTime += 0.1;
						// 获取音量，更新dialog
						if (!isCanceled) {
							voiceValue = mAudioRecorder.getAmplitude();
							recordHandler.sendEmptyMessage(1);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				
			}
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler recordHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setDialogImage();
		}
	};

	@Override
	public void stopNestedScroll() {
		super.stopNestedScroll();
	}

	@Override
	public void setCropToPadding(boolean cropToPadding) {
		super.setCropToPadding(cropToPadding);
	}

	//定义帧动画效果的布局
	private AnimationDrawable animationDrawable;
	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		Log.i("qwerr",event.getAction()+"");
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 按下按钮
			//录音
//			iv_back.setBackgroundResource(R.color.lucency);
			iv_back.setVisibility(View.VISIBLE);
			iv_back.setImageResource(R.drawable.animationluyin);
			animationDrawable = (AnimationDrawable) iv_back.getDrawable();
			animationDrawable.start();
			this.setBackgroundResource(R.mipmap.icon_luyinanxia);
			if (recordState != RECORD_ON) {
				showVoiceDialog(0);
				downY = event.getY();
				if (mAudioRecorder != null) {
					mAudioRecorder.ready();
					recordState = RECORD_ON;
					mAudioRecorder.start();
					callRecordTimeThread();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE: // 滑动手指
			vplay=1;
			float moveY = event.getY();
			if (downY - moveY > 50) {
				isCanceled = true;
				showVoiceDialog(1);
			}
			if (downY - moveY < 20) {
				isCanceled = false;
				showVoiceDialog(0);
			}
			break;
		case MotionEvent.ACTION_UP: // 松开手指
			animationDrawable.stop();
			animationDrawable.selectDrawable(0);
			iv_back.setVisibility(View.GONE);
			this.setBackgroundResource(R.mipmap.icon_lyanniu);
//			RecordImage.this.setBackgroundResource(R.mipmap.icon_luyin);
			if (recordState == RECORD_ON) {
				recordState = RECORD_OFF;
				if (mRecordDialog.isShowing()) {
					mRecordDialog.dismiss();
				}
				mAudioRecorder.stop();
				mRecordThread.interrupt();
				voiceValue = 0.0;
				if (isCanceled) {//录音失败
					mAudioRecorder.deleteOldFile();
				} else {
					if (recodeTime < MIN_RECORD_TIME) {
						showWarnToast("时间太短  录音失败");
						mAudioRecorder.deleteOldFile();
					} else {
						if (listener != null) {
							listener.recordEnd();
						}
					}
					vplay=0;//外部类初始化标识
				}
				isCanceled = false;
//				this.setText("按住 说话");
			}
			break;

			case MotionEvent.ACTION_CANCEL:
				this.setBackgroundResource(R.mipmap.icon_lyanniu);
				animationDrawable.stop();
				iv_back.setVisibility(View.GONE);
				mAudioRecorder.stop();
				mRecordThread.interrupt();
				voiceValue = 0.0;
				mAudioRecorder.deleteOldFile();
				mRecordDialog.dismiss();
				break;

		}
		return true;
	}
	
	public interface RecordListener {
		public void recordEnd();
	}

	public void setImage(ImageView iv_back){
		this.iv_back=iv_back;
	}
}
