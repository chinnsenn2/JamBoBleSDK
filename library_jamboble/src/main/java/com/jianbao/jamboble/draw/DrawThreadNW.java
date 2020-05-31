package com.jianbao.jamboble.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import com.creative.base.BaseDate.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 绘制血氧波形
 * draw pod wave by view
 */
public class DrawThreadNW extends BaseDraw {

	/** 血氧搏动标记 */
	public static final byte MSG_DATA_PULSE = 0x03;
	/** 取消搏动标记 */
	public static final byte RECEIVEMSG_PULSE_OFF = 0x04;

	/** 血氧波形高度的最大值 可控制波形高度 */
	private final int ySpo2Max = 200; //130
	/** 当前波形增益 */
	protected int gain = 2;
	/** 血氧波形高度缩放比例 */
	private float zoomSpo2 = 0.0f;
	private String msg;

	private int mLineColor = Color.RED;

	Path path = new Path();

	private final List<Wave> SPO_WAVE = new ArrayList<>();

	/** 绘制波形的线程 */
	private Thread mDrawThread = null;

	public DrawThreadNW(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public DrawThreadNW(Context context) {
		super(context);
	}

	public DrawThreadNW(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public synchronized void Continue() {
		super.Continue();
		cleanWaveData();
	}

	@Override
	public void cleanWaveData() {
		SPO_WAVE.clear();
		super.cleanWaveData();
	}

	public void addWaveData(List<Wave> datas){
		if (datas != null) {
			SPO_WAVE.addAll(datas);
		}
	}

	@Override
	public void run() {
		super.run();
		synchronized (this) {
			while (!stop) {
				try {
					if (pause) {
						this.wait();
					}
					if (SPO_WAVE.size() > 0) {
						Wave data = SPO_WAVE.remove(0);
						addData(data.data);
						if (data.flag == 1) { //发送搏动标记
							mHandler.sendEmptyMessage(MSG_DATA_PULSE);
						}
						
						//设置参数，调整波形，adjust wave
						if (SPO_WAVE.size() > 20) {
							Thread.sleep(12); //18
						} else {
							Thread.sleep(25); //25
						}							
					} else {
						Thread.sleep(500);
					}					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			cleanWaveData();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//如果是eclipse、androidstudio的编辑模式下,跳过以下代码
		if(isInEditMode()){
			return;
		}

		if (msg != null && !msg.isEmpty())
			drawMsg(canvas);


		paint.setStrokeWidth(dm.density*2);
		paint.setColor(mLineColor);
		path.reset();
		path.moveTo(0, gethPx(data2draw[0]));
		for (int i = 0; i < data2draw.length; i++) {
			if (data2draw[i] != -1) {
				path.lineTo(i * stepx, gethPx(data2draw[i]));
			}
		}
		canvas.drawPath(path, paint);
	}

	private void drawMsg(Canvas canvas) {
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(dm.density * 2);
		mPaint.setColor(Color.BLACK);
		mPaint.setTextSize(dm.density * 20);
		canvas.drawText(msg, (weight - mPaint.measureText(msg)) / 2,
				height / 2, mPaint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		zoomSpo2 = height / ySpo2Max;
	}

	/**
	 * 获取该点在Y轴上的像素坐标
	 */
	private float gethPx(int data) {
		return height - zoomSpo2 * data;
	}

	/**
	 * 设置波形增益
	 * 
	 * @param gain
	 */
	public void setGain(int gain) {
		this.gain = gain == 0 ? 2 : gain;
	}

	public void drawMsg(String msg) {
		this.msg = msg;
		postInvalidate();
	}

	public void setLineColor(int lineColor) {
		mLineColor = lineColor;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stopDraw();
	}

	/** 开始绘图 */
	public void startDraw() {
		if (mDrawThread == null) {
			mDrawThread = new Thread(this, "DrawPOD_Thread");
			mDrawThread.start();
		} else if (isPause()) {
			Continue();
		}

	}

	/** 暂停绘图 */
	public void pauseDraw() {
		if (mDrawThread != null && !isPause()) {
			Pause();
		}
	}

	/** 停止绘图  */
	public void stopDraw() {
		if (!isStop()) {
			Stop();
		}
		mDrawThread = null;
	}
}
