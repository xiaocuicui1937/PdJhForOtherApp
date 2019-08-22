package com.bhkj.pdjhforotherapp.core.sfsure;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bhkj.pdjhforotherapp.R;
import com.bhkj.pdjhforotherapp.common.Contact;
import com.bhkj.pdjhforotherapp.common.bean.JhResultBean;
import com.bhkj.pdjhforotherapp.common.bean.QueueBusiness;
import com.bhkj.pdjhforotherapp.common.bean.SelectedItemBean;
import com.bhkj.pdjhforotherapp.common.parse.GsonProvider;
import com.bhkj.pdjhforotherapp.common.simpledisk.Util;
import com.bhkj.pdjhforotherapp.common.utils.Tools;
import com.bhkj.pdjhforotherapp.common.view.ShadowDrawable;
import com.bhkj.pdjhforotherapp.core.base.BaseActivity;
import com.bhkj.pdjhforotherapp.core.print.MainActivity;
import com.bhkj.pdjhforotherapp.core.print.util.Bills;
import com.bhkj.pdjhforotherapp.core.print.util.Constant;
import com.bhkj.pdjhforotherapp.core.print.util.T;
import com.bhkj.pdjhforotherapp.core.print.util.Utils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.printsdk.cmd.PrintCmd;
import com.printsdk.usbsdk.UsbDriver;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;


import net.lemonsoft.lemonhello.LemonHello;
import net.lemonsoft.lemonhello.LemonHelloAction;
import net.lemonsoft.lemonhello.LemonHelloInfo;
import net.lemonsoft.lemonhello.LemonHelloView;
import net.lemonsoft.lemonhello.interfaces.LemonHelloActionDelegate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class JHDetailActivity extends BaseActivity {
    private GifImageView mGiv;
    private int mCountForTime = 120;//倒计时
    private TextView mTvTimer;//倒计时文字显示
    private UsbManager mUsbManager;
    private UsbDriver mUsbDriver;
    private UsbReceiver mUsbReceiver;
    UsbDevice mUsbDev1;        //打印机1
    UsbDevice mUsbDev2;        //打印机2
    UsbDevice mUsbDev;
    private final static int PID11 = 8211;
    private final static int PID13 = 8213;
    private final static int PID15 = 8215;
    private final static int VENDORID = 1305;
    private static final String ACTION_USB_PERMISSION = "com.usb.sample.USB_PERMISSION";
    private String title;
    private String strData;
    private String num;
    private GifDrawable gifDrawable;

    @Override
    public int getLayoutId() {
        return R.layout.activity_jhdetail;
    }

    @Override
    public void initView() {
        getWindow().setLayout(1600, 1000);
        mGiv = findViewById(R.id.giv_jh_detail);
        mTvTimer = findViewById(R.id.tv_jh_detail_timer);
        findViewById(R.id.iv_jh_detail_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvTimer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                saveSelectedYwToServer();
            }
        });
    }

    @Override
    public void initData() {
        if (gifDrawable != null && !gifDrawable.isRecycled()) {
            gifDrawable.recycle();
        }
        gifDrawable = (GifDrawable) mGiv.getDrawable();
        if (gifDrawable != null) {
            gifDrawable.setLoopCount(65535);
        }

        //5分钟后自动超时返回上一页
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCountForTime--;
                        if (mCountForTime == 100) {
//                            if (gifDrawable != null) {
//                                mGiv.setImageResource(R.drawable.gif_read_data);
//                            }
                        }
                        //到0秒后自动关闭界面返回上一页
                        if (mCountForTime == 0) {
                            finish();
                        }
                        mTvTimer.setText("倒计时" + mCountForTime + "秒");
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);
        initPrint();
    }

    private void initPrint() {
        getUsbDriverService();// 创建UsbManager ，权限，广播；usb驱动
        getMsgByLanguage();   // 常规设置
        printConnStatus();    // 连接设备 Get UsbDriver(UsbManager) service
    }

    private void getUsbDriverService() {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mUsbDriver = new UsbDriver(mUsbManager, this);
        PendingIntent permissionIntent1 = PendingIntent.getBroadcast(this, 0,
                new Intent(ACTION_USB_PERMISSION), 0);
        mUsbDriver.setPermissionIntent(permissionIntent1);
        // Broadcast listen for new devices

        mUsbReceiver = new UsbReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        this.registerReceiver(mUsbReceiver, filter);
    }

    // 通过系统语言判断Message显示
    String receive = "", state = ""; // 接收提示、状态类型
    String normal = "", notConnectedOrNotPopwer = "", notMatch = "",
            printerHeadOpen = "", cutterNotReset = "", printHeadOverheated = "",
            blackMarkError = "", paperExh = "", paperWillExh = "", abnormal = "";

    private void getMsgByLanguage() {
        if (Utils.isZh(this)) {
            receive = Constant.Receive_CN;
            state = Constant.State_CN;
            normal = Constant.Normal_CN;
            notConnectedOrNotPopwer = Constant.NoConnectedOrNoOnPower_CN;
            notMatch = Constant.PrinterAndLibraryNotMatch_CN;
            printerHeadOpen = Constant.PrintHeadOpen_CN;
            cutterNotReset = Constant.CutterNotReset_CN;
            printHeadOverheated = Constant.PrintHeadOverheated_CN;
            blackMarkError = Constant.BlackMarkError_CN;
            paperExh = Constant.PaperExhausted_CN;
            paperWillExh = Constant.PaperWillExhausted_CN;
            abnormal = Constant.Abnormal_CN;
        } else {
            receive = Constant.Receive_US;
            state = Constant.State_US;
            normal = Constant.Normal_US;
            notConnectedOrNotPopwer = Constant.NoConnectedOrNoOnPower_US;
            notMatch = Constant.PrinterAndLibraryNotMatch_US;
            printerHeadOpen = Constant.PrintHeadOpen_US;
            cutterNotReset = Constant.CutterNotReset_US;
            printHeadOverheated = Constant.PrintHeadOverheated_US;
            blackMarkError = Constant.BlackMarkError_US;
            paperExh = Constant.PaperExhausted_US;
            paperWillExh = Constant.PaperWillExhausted_US;
            abnormal = Constant.Abnormal_US;
        }
    }

    // Get UsbDriver(UsbManager) service
    private boolean printConnStatus() {
        boolean blnRtn = false;
        try {
            if (!mUsbDriver.isConnected()) {
                // USB线已经连接
                for (UsbDevice device : mUsbManager.getDeviceList().values()) {
                    if ((device.getProductId() == PID11 && device.getVendorId() == VENDORID)
                            || (device.getProductId() == PID13 && device.getVendorId() == VENDORID)
                            || (device.getProductId() == PID15 && device.getVendorId() == VENDORID)) {
//						if (!mUsbManager.hasPermission(device)) {
//							break;
//						}
                        blnRtn = mUsbDriver.usbAttached(device);
                        if (blnRtn == false) {
                            break;
                        }
                        blnRtn = mUsbDriver.openUsbDevice(device);

                        // 打开设备
                        if (blnRtn) {
                            if (device.getProductId() == PID11) {
                                mUsbDev1 = device;
                                mUsbDev = mUsbDev1;
                            } else {
                                mUsbDev2 = device;
                                mUsbDev = mUsbDev2;
                            }
                            T.showShort(this, getString(R.string.USB_Driver_Success));
                            break;
                        } else {
                            T.showShort(this, getString(R.string.USB_Driver_Failed));
                            break;
                        }
                    }
                }
            } else {
                blnRtn = true;
            }
        } catch (Exception e) {
            T.showShort(this, e.getMessage());
        }
        return blnRtn;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveSelectedYwToServer() {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.tip_connect_net_content);
            return;
        }
        showLoading();
        String queueBusiness = "";
        if (getIntent() != null) {
            queueBusiness = getIntent().getStringExtra(Contact.SURE_YW);
        }
        String name = "张三";
        try {
            name = URLEncoder.encode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        net(queueBusiness, name);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void net(String queueBusiness, String name) {
        String deviceId = Tools.getDeviceId();
        String windowType = Contact.COMMON_WINDOW;
        if (getIntent() != null) {
            windowType = getIntent().getStringExtra(Contact.QHTYPE);
        }
        EasyHttp.post(Contact.SAVE_YW_TO_SERVEr)
                .params("key", deviceId)
                .params("rsa", Tools.encodeRsa(deviceId))
                .params("xm", name)
                .params("sfzhm", "13092919911112421X")
                .params("windowtype", windowType)
                .params("queueBusiness", queueBusiness)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        dismissLoading();
                        onErrorTip(getString(R.string.error_again));
                    }

                    @Override
                    public void onSuccess(String s) {
                        dismissLoading();
                        handleJhSuccessResult(s);
                    }
                });
    }

    private void handleJhSuccessResult(String s) {
        if (TextUtils.isEmpty(s)) {
            onErrorTip("空数据");
            return;
        }
        JhResultBean jhResultBean = GsonProvider.getInstance().getGson().fromJson(s, JhResultBean.class);
        if (jhResultBean != null) {
            //打印小票
            if (jhResultBean.isSuccess()) {
                //获取usb连接状态
                if (!printConnStatus()) {
                    return;
                }
                checkPrinterStatus(mUsbDev);
                getStrDataByLanguage(jhResultBean);
                Bills.printSmallTicket(mUsbDriver, title, num, jhResultBean, 0); // 排队票据

            } else {
                onErrorTip(jhResultBean.getMessage());
            }
        } else {
            onErrorTip("叫号数据返回为空");
        }
    }

    // 根据系统语言获取测试文本
    private void getStrDataByLanguage(JhResultBean jhResultBean) {
        if (Utils.isZh(this)) {
            title = Constant.TITLE_CN + jhResultBean.getDatas().getName();
        }
        num = jhResultBean.getDatas().getQunumber() + "\n\n";
    }

    /**
     * 访问保存数据接口出错后的提示对话框，可以接着访问
     * 或者是关闭页面停止访问接口
     *
     * @param tip 提示信息
     */
    private void onErrorTip(String tip) {

        LemonHello.getErrorHello(getString(R.string.error_title), tip)
                .addAction(new LemonHelloAction(getString(R.string.again), new LemonHelloActionDelegate() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(LemonHelloView lemonHelloView, LemonHelloInfo lemonHelloInfo, LemonHelloAction lemonHelloAction) {
                        saveSelectedYwToServer();
                        lemonHelloView.hide();
                    }
                }))
                .addAction(new LemonHelloAction(getString(R.string.close), new LemonHelloActionDelegate() {
                    @Override
                    public void onClick(LemonHelloView lemonHelloView, LemonHelloInfo lemonHelloInfo, LemonHelloAction lemonHelloAction) {
                        lemonHelloView.hide();
                        finish();
                    }
                })).show(this);

    }

    /*
     *  BroadcastReceiver when insert/remove the device USB plug into/from a USB port
     *  创建一个广播接收器接收USB插拔信息：当插入USB插头插到一个USB端口，或从一个USB端口，移除装置的USB插头
     */
    class UsbReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                if (mUsbDriver.usbAttached(intent)) {
                    UsbDevice device = intent
                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if ((device.getProductId() == PID11 && device.getVendorId() == VENDORID)
                            || (device.getProductId() == PID13 && device.getVendorId() == VENDORID)
                            || (device.getProductId() == PID15 && device.getVendorId() == VENDORID)) {
                        if (mUsbDriver.openUsbDevice(device)) {
                            if (device.getProductId() == PID11) {
                                mUsbDev1 = device;
                                mUsbDev = mUsbDev1;
                            } else {
                                mUsbDev2 = device;
                                mUsbDev = mUsbDev2;
                            }
                        }
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent
                        .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if ((device.getProductId() == PID11 && device.getVendorId() == VENDORID)
                        || (device.getProductId() == PID13 && device.getVendorId() == VENDORID)
                        || (device.getProductId() == PID15 && device.getVendorId() == VENDORID)) {
                    mUsbDriver.closeUsbDevice(device);
                    if (device.getProductId() == PID11)
                        mUsbDev1 = null;
                    else
                        mUsbDev2 = null;
                }
            } else if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if ((device.getProductId() == PID11 && device.getVendorId() == VENDORID)
                                || (device.getProductId() == PID13 && device.getVendorId() == VENDORID)
                                || (device.getProductId() == PID15 && device.getVendorId() == VENDORID)) {
                            if (mUsbDriver.openUsbDevice(device)) {
                                if (device.getProductId() == PID11) {
                                    mUsbDev1 = device;
                                    mUsbDev = mUsbDev1;
                                } else {
                                    mUsbDev2 = device;
                                    mUsbDev = mUsbDev2;
                                }
                            }
                        }
                    } else {
                        ToastUtils.showShort("permission denied for device");
                        //Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    }

    ;    // 状态查询

    private void checkPrinterStatus(UsbDevice usbDev) {
        int iStatus = getPrinterStatus(usbDev);
        if (checkStatus(iStatus) != 0) {
            ToastUtils.showShort("获取失败");
            return;
        }
//        ToastUtils.showShort("获取成功!");
    }

    // 检测打印机状态
    private int getPrinterStatus(UsbDevice usbDev) {
        int iRet = -1;

        byte[] bRead1 = new byte[1];
        byte[] bWrite1 = PrintCmd.GetStatus1();
        if (mUsbDriver.read(bRead1, bWrite1, usbDev) > 0) {
            iRet = PrintCmd.CheckStatus1(bRead1[0]);
        }

        if (iRet != 0)
            return iRet;

        byte[] bRead2 = new byte[1];
        byte[] bWrite2 = PrintCmd.GetStatus2();
        if (mUsbDriver.read(bRead2, bWrite2, usbDev) > 0) {
            iRet = PrintCmd.CheckStatus2(bRead2[0]);
        }

        if (iRet != 0)
            return iRet;

        byte[] bRead3 = new byte[1];
        byte[] bWrite3 = PrintCmd.GetStatus3();
        if (mUsbDriver.read(bRead3, bWrite3, usbDev) > 0) {
            iRet = PrintCmd.CheckStatus3(bRead3[0]);
        }

        if (iRet != 0)
            return iRet;

        byte[] bRead4 = new byte[1];
        byte[] bWrite4 = PrintCmd.GetStatus4();
        if (mUsbDriver.read(bRead4, bWrite4, usbDev) > 0) {
            iRet = PrintCmd.CheckStatus4(bRead4[0]);
        }
        return iRet;
    }


    private int checkStatus(int iStatus) {
        int iRet = -1;

        StringBuilder sMsg = new StringBuilder();

        //0 打印机正常 、1 打印机未连接或未上电、2 打印机和调用库不匹配
        //3 打印头打开 、4 切刀未复位 、5 打印头过热 、6 黑标错误 、7 纸尽 、8 纸将尽
        switch (iStatus) {
            case 0:
                sMsg.append(normal);       // 正常
                iRet = 0;
                break;
            case 8:
                sMsg.append(paperWillExh); // 纸将尽
                iRet = 0;
                showMessage(sMsg.toString());

                break;
            case 3:
                sMsg.append(printerHeadOpen); //打印头打开
                showMessage(sMsg.toString());

                break;
            case 4:
                sMsg.append(cutterNotReset);  //切刀未复位
                showMessage(sMsg.toString());
                break;
            case 5:
                sMsg.append(printHeadOverheated); //打印头过热
                showMessage(sMsg.toString());

                break;
            case 6:
                sMsg.append(blackMarkError);  //黑标错误
                showMessage(sMsg.toString());

                break;
            case 7:
                sMsg.append(paperExh);     // 纸尽==缺纸
                showMessage(sMsg.toString());

                break;
            case 1:
                sMsg.append(notConnectedOrNotPopwer);//打印机未连接或未上电
                showMessage(sMsg.toString());

                break;
            default:
                sMsg.append(abnormal);     // 异常
                showMessage(sMsg.toString());
                break;
        }
        return iRet;
    }

    private void showMessage(String sMsg) {
        LemonHello.getInformationHello("打印机状态", sMsg)
                .addAction(new LemonHelloAction(getString(R.string.sure), new LemonHelloActionDelegate() {
                    @Override
                    public void onClick(LemonHelloView lemonHelloView, LemonHelloInfo lemonHelloInfo, LemonHelloAction lemonHelloAction) {

                        lemonHelloView.hide();
                    }
                })).show(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gifDrawable != null) {
            gifDrawable.recycle();
        }
    }
}
