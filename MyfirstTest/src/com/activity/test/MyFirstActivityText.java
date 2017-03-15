package com.activity.test;

import com.activity.MyFirstActivity;
import static android.test.ViewAsserts.assertOnScreen;
import static android.test.ViewAsserts.assertRightAligned;
import static android.test.MoreAsserts.assertNotContainsRegex;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.UiThreadTest;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 描述：安卓测试demo
 * @author PJY
 * 时间:2017.3.15
 *
 */
public class MyFirstActivityText extends ActivityInstrumentationTestCase2<MyFirstActivity> {
	//构建对象
	MyFirstActivity mActivity;
	private EditText mMessage;
	private Button mOK;
	private TextView mLink;
	private Instrumentation mInstrumentation;
	
	
	/**
	 * 无参数构造方法
	 * 在测试运行时被有参数的构造方法调用，通常也被用于应用于序列化
	 */
	public MyFirstActivityText() {
		this("MyFirstActivityText");
	}
	/**
	 * 有参数构造方法
	 * @param name
	 * 它将会出现在测试报告中，并帮助我们识别失败的测试用例
	 * 
	 * 有一些扩展TestCase的类，不提供一个带参数的构造方法，这时可使用setName()方法代替
	 */
	public MyFirstActivityText(String name) {
		super(MyFirstActivity.class);
		setName(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		/**
		 * 设置为false时，指明可以使用sengding key events。
		 * setActivityInitialTouchMode()方法必须在getActivity()方法调用之前被调用
		 */
		setActivityInitialTouchMode(false);
		mActivity=getActivity();
		//Instrumentation类是用于监控系统和应用程序或Activity之间的交互。
		mInstrumentation=getInstrumentation();
		//获取组件，findViewById()方法中参数应为被测程序的组件
		mLink=(TextView)mActivity.findViewById(com.activity.R.id.link);
		mMessage=(EditText)mActivity.findViewById(com.activity.R.id.message);
		mOK=(Button)mActivity.findViewById(com.activity.R.id.capitalize);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	/**
	 * 获取容器方法一：（获取父容器）
	 * mActivity.getWindow().getDecorView();
	 * 
	 * 判断mMessage和mOK按钮是否在容器上
	 */
	public void testUserInterfaceLayout(){
		final int margin=0;
		//寻找mActivity的父容器
		final View origin=mActivity.getWindow().getDecorView();
		//判断mMessage文字是否在容器上
		assertOnScreen(origin,mMessage);
		//判断mOK按钮是否在容器上
		assertOnScreen(origin, mOK);
		//判断mMessage和mOK按钮是否右对齐
		assertRightAligned(mMessage, mOK,margin);
	}
	/**
	 * 获取容器方法二：（获取根容器）
	 * mMessage.getRootView();
	 * 
	 * mMessage在跟容器上，则两种获取容器的结果是一致的
	 * 
	 * 判断mMessage和mOK按钮是否在容器上
	 */
	public void testUserInterfaceLayoutWithOtherOrigin(){
		final int margin=0;
		//寻找mMessage的根容器
		View origin=mMessage.getRootView();
		assertOnScreen(origin, mMessage);
		origin=mOK.getRootView();
		assertOnScreen(origin, mOK);
		assertRightAligned(mMessage, mOK, margin);
	}
	/**
	 * @UiThreadTest 通过线程改变组件
	 * 
	 * 测试输入框
	 */
	@UiThreadTest
	public void testNoErrorInCapitalization(){
		final String msg="this is a sample";
		//给文本框设置字符串
		mMessage.setText(msg);
		//自动点击按钮
		mOK.performClick();
		final String actual=mMessage.getText().toString();
		//大小写
		final String notExpectedRegexp="(?i:ERROR)";
		//判断notExpectedRegexp是否是actual的子集
		assertNotContainsRegex("OK found error:", notExpectedRegexp,actual);
	}
	public void requestMessageFocus(){
		try {
			//启动线程
			runTestOnUiThread(new Runnable() {
				public void run() {
					//设置文本框获取焦点
					mMessage.requestFocus();
				}
			});
		} catch (Throwable e) {
			fail("Couldn't set focus");
		}
		//waitForIdleSync()方法表示同步等待应用程序被闲置
		mInstrumentation.waitForIdleSync();	
	}
	/**
	 * 测试键盘，对键盘的操作，方法一：
	 * sendkeys
	 */
	public void testSendKeys(){
		requestMessageFocus();
		//给键盘发送命令使其操作
		sendKeys(KeyEvent.KEYCODE_H,
				KeyEvent.KEYCODE_E,
				KeyEvent.KEYCODE_E,
				KeyEvent.KEYCODE_E,
				KeyEvent.KEYCODE_Y,
				KeyEvent.KEYCODE_ALT_LEFT,
				KeyEvent.KEYCODE_1,
				KeyEvent.KEYCODE_DPAD_DOWN,
				KeyEvent.KEYCODE_ENTER
				);
		//定义期望值
		final String expected="HEEEY1";
		//获取实际值
		final String actual=mMessage.getText().toString();
		//判断期望值与实际值是否相等
		assertEquals(expected, actual);
	}
	/**
	 * 测试键盘，对键盘的操作，方法二：
	 * sendkeys
	 */
	public void testSendKeyString(){
		requestMessageFocus();
		//给键盘发送命令使其操作
		sendKeys("H 3*E Y ALT_LEFT 1 DPAD_DOWN ENTER");
		//定义期望值
		final String expected="HEEEY1";
		//获取实际值
		final String actual=mMessage.getText().toString();
		//判断期望值与实际值是否相等
		assertEquals(expected, actual);
	}
	/**
	 * 测试键盘，对键盘的操作，方法二：
	 * sendRepeatedkeys
	 */
	public void testSendRepeatedKeys(){
		requestMessageFocus();
		//给键盘发送命令使其操作
		sendRepeatedKeys(1,KeyEvent.KEYCODE_H,
				3,KeyEvent.KEYCODE_E,
				1,KeyEvent.KEYCODE_Y,
				1,KeyEvent.KEYCODE_ALT_LEFT,
				1,KeyEvent.KEYCODE_1,
				1,KeyEvent.KEYCODE_DPAD_DOWN,
				1,KeyEvent.KEYCODE_ENTER
				);
		//定义期望值
		final String expected="HEEEY1";
		//获取实际值
		final String actual=mMessage.getText().toString();
		//判断期望值与实际值是否相等
		assertEquals(expected, actual);
	}
	/**
	 * 对链接进行测试
	 * 步骤：
	 * 1、获得Instrumentation对象
	 * 2、添加一个IntentFilter监测器
	 * 3、等待activity响应
	 * 4、确认被监测的点击增加情况
	 * 5、移除监测
	 */
	public void testFollowLink(){
		//获得一个Instrumentation对象
		//用来监视整个应用程序或activity和安卓系统交互的所有过程
		final Instrumentation inst=getInstrumentation();
		//添加一个IntentFilter监测器，Intent.ACTION_VIEW用于显示用户数据
		IntentFilter intentFilter=new IntentFilter(Intent.ACTION_VIEW);
		//添加“http”协议
		intentFilter.addDataScheme("http");
		//指定用浏览器打开
		intentFilter.addCategory(Intent.CATEGORY_BROWSABLE);
		//实例ActivityMonitor
		//ActivityMonitor用来监视应用中单个活动，可用来监视指定的意图
		/**
		 * 通过Instrumentation.addMonitor来添加实例
		 * 当活动启动后，系统会匹配Instrumentation中的ActivityMonitor实例列表
		 * 如果匹配，就会累加计数器
		 */
		ActivityMonitor monitor=inst.addMonitor(intentFilter, null, false);
		//获取点击的次数
		assertEquals(0, monitor.getHits());
		//TouchUtils类提供了大量模拟操作，此处为模拟点击
		TouchUtils.clickView(this, mLink);
		//设置等待5秒
		monitor.waitForActivityWithTimeout(5000);
		//再次获取点击次数
		assertEquals(1, monitor.getHits());
		//清理操作
		inst.removeMonitor(monitor);
	}

}
