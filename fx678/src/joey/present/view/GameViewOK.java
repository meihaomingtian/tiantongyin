/*
 * 使用SurfaceView来做游戏的基本框架. 都可以按照这个格式来做.
 */
package joey.present.view;

import android.R.interpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * surfaceView 调试用
 * 
 * @author Administrator
 * 
 */
public class GameViewOK extends SurfaceView implements SurfaceHolder.Callback, Runnable {
	SurfaceHolder surfaceHolder;
	private boolean isThreadRunning = true;
	Canvas canvas;
	float r = 10;
	int width = 0;
	int height = 0;

	public GameViewOK(Context context, int x, int y) {
		super(context);
		width = x;
		height = y;
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);// 注册回调方法
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// 创建surfaceView时启动线程
		new Thread(this).start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// 当surfaceView销毁时, 停止线程的运行. 避免surfaceView销毁了线程还在运行而报错.
		isThreadRunning = false;
		// 第三种方法防止退出时异常. 当surfaceView销毁时让线程暂停300ms .
		// 醒来再执行run()方法时,isThreadRunning就是false了.
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将绘图的方法单独写到这个方法里面.
	 */
	private void drawVieW() {
		try {// 第一种方法防止退出时异常: 当isThreadRunning为false时, 最后还是会执行一次drawView方法,
				// 但此时surfaceView已经销毁
				// 因此才来判断surfaceHolder
			if (surfaceHolder != null) {
				// 1. 在surface创建后锁定画布
				canvas = surfaceHolder.lockCanvas();
				Paint(canvas);
				// 2. 可以在画布上进行任意的绘画操作( 下面是画一条红色 的线 )
				Paint paint = new Paint();
				paint.setColor(Color.BLUE);
				// paint.setStyle(Style.STROKE);//只有边框
				paint.setStrokeWidth(5);
				canvas.drawCircle(x, y, r++, paint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// canvas是根据surfaceHolder得到的, 最后一次surfaceView已经销毁, canvas当然也不存在了.
			if (canvas != null)
				// 3. 将画布解锁并显示在屏幕上
				surfaceHolder.unlockCanvasAndPost(canvas);
		}

	}

	public void Paint(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(0xff000000);
		canvas.drawRect(0, 0, width, height, paint);
		// 每次调用Paint方法都会把先前的清空 防治残影
	}

	public void run() {
		// 每隔100ms刷新屏幕
		while (isThreadRunning) {
			drawVieW();
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 这个是第二种方法解决退出是报错的问题. 当按下返回键时, 提前设置isThreadRunning为false, 让线程结束.
	 * 
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
	 * if(keyCode == KeyEvent.KEYCODE_BACK) { isThreadRunning = false; } return
	 * super.onKeyDown(keyCode, event); }
	 */
	float x = 0, y = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) { // 处理屏幕屏点下事件
															// 手指点击屏幕时触发
			x = event.getX();
			y = event.getY();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {// 处理屏幕屏抬起事件
																// 手指离开屏幕时触发

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {// 处理移动事件
																	// 手指在屏幕上移动时触发
			x = event.getX();
			y = event.getY();
		}
		return true; // 此处需要返回true 才可以正常处理move事件 详情见后面的 说明
	}

}
