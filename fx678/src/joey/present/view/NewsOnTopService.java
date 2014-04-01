package joey.present.view;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import joey.present.util.Const;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

public class NewsOnTopService extends Service {
	private DatagramSocket client;
	private DatagramPacket recpacket;
	private DatagramPacket sendpacket;
	// 自动更新线程启动标志
	private boolean running = true;
	// 自动更新线程
	public AutoUpdateThread autoUpdateThread;
	private String lastStr = "";
	private int reload = 0;
	private MyBind myBind = new MyBind();

	@Override
	public IBinder onBind(Intent arg0) {
		
		return myBind;
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String message = msg.getData().getString("message");
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		}
	};

	@Override
	public void onDestroy() {
		 client.close();
		 running = false;
	}

	// 自动更新线程
	/** Nested class that performs progress calculations (counting) */
	public class AutoUpdateThread extends Thread {
		Handler mHandler;
		private long waitTime = 10000;

		AutoUpdateThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			while (running) {
				// setForeground(false);
				// setForeground(true);
				// Message msg = mHandler.obtainMessage();
				// Bundle b = new Bundle();
				// b.putString("message", "test"+new Date().toLocaleString());
				// msg.setData(b);
				//
				// mHandler.sendMessage(msg);
				try {
					client.receive(recpacket);
					reload--;
					String str = new String(recpacket.getData(), 0, 1000,
							"UTF-8");
					if (str.indexOf("[") >= 0 && str.indexOf("]") >= 0) {
						str = str.substring(str.indexOf("[") + 1,
								str.indexOf("]"));
						if (!str.equals(lastStr)) {
							Message msg = mHandler.obtainMessage();
							Bundle b = new Bundle();
							b.putString("message", str);
							msg.setData(b);

							mHandler.sendMessage(msg);
						}
						lastStr = str;
					}

					// try {
					// AutoUpdateThread.sleep(500);
					// } catch (InterruptedException e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// }
//					if (reload < 0) {
//						try {
//							client = new DatagramSocket();
//						} catch (SocketException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						String sendStr = String.format("login,message");
//						byte[] sendBuf = sendStr.getBytes();
//						sendBuf = sendStr.getBytes();
//						InetAddress address = null;
//						try {
//							address = InetAddress.getByName("218.1.64.146");
//						} catch (UnknownHostException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						int port = 21136;
//						sendpacket = new DatagramPacket(sendBuf,
//								sendBuf.length, address, port);
//						try {
//							client.send(sendpacket);
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//						byte[] recBuf = new byte[1000];
//						recpacket = new DatagramPacket(recBuf, recBuf.length);
//
//						reload = 10;
//					}
				} catch (Exception e) {
//					try {
//						client = new DatagramSocket();
//					} catch (SocketException ex) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					String sendStr = String.format("login,message");
//					byte[] sendBuf = sendStr.getBytes();
//					sendBuf = sendStr.getBytes();
//					InetAddress address = null;
//					try {
//						address = InetAddress.getByName("218.1.64.146");
//					} catch (UnknownHostException ex) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					int port = 21136;
//					sendpacket = new DatagramPacket(sendBuf, sendBuf.length,
//							address, port);
//					try {
//						client.send(sendpacket);
//					} catch (IOException ex) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//					byte[] recBuf = new byte[1000];
//					recpacket = new DatagramPacket(recBuf, recBuf.length);
				}
			}
		}

	}

	public class MyBind extends Binder implements IService {
		public String getName() {
			
			return "NEWSTOPPORT";
		}
	}

	public int onStartCommand(Intent intent, int flags, int startid) {
		super.onStartCommand(intent, flags, startid);
		// setForeground(true);
		running = true;
		try {
			client = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sendStr = String.format("login,message");
		byte[] sendBuf = sendStr.getBytes();
		sendBuf = sendStr.getBytes();
		InetAddress address = null;
		try {
			address = InetAddress.getByName(Const.UDP_IP);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int port = 21135;
		sendpacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
		try {
			client.send(sendpacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] recBuf = new byte[1000];
		recpacket = new DatagramPacket(recBuf, recBuf.length);
		Thread t = new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(8000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String sendStr = String.format("active,active");
					byte[] sendBuf = sendStr.getBytes();
					sendBuf = sendStr.getBytes();
					try {

						sendpacket.setData(sendBuf);
						client.send(sendpacket);
					} catch (Exception e) {

					}

				}
			}
		};
		t.start();
		autoUpdateThread = new AutoUpdateThread(handler);
		running = true;
		autoUpdateThread.start();
		reload = 10;
		return START_STICKY;
	}

	@Override
	public void onStart(Intent intent, int startid) {
		super.onStart(intent, startid);

	}

	public interface IService {
		String getName();
	}
}
