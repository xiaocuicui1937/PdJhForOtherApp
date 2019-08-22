package com.bhkj.pdjhforotherapp.core.main;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bhkj.pdjhforotherapp.MainActivity;
import com.bhkj.pdjhforotherapp.R;
import com.bhkj.pdjhforotherapp.common.Contact;
import com.bhkj.pdjhforotherapp.common.bean.MainYwAllBean;
import com.bhkj.pdjhforotherapp.common.bean.MainYwBean;
import com.bhkj.pdjhforotherapp.common.parse.GsonProvider;
import com.bhkj.pdjhforotherapp.common.reservoir.ReservoirUtils;
import com.bhkj.pdjhforotherapp.common.simpledisk.DiskLruCacheHelper;
import com.bhkj.pdjhforotherapp.common.utils.Tools;
import com.bhkj.pdjhforotherapp.common.utils.UpdateUtil;
import com.bhkj.pdjhforotherapp.common.view.ShadowDrawable;
import com.bhkj.pdjhforotherapp.core.base.BaseActivity;
import com.bhkj.pdjhforotherapp.core.sure.SelectedYwSureActivity;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;
import com.zyao89.view.zloading.circle.SnakeCircleBuilder;

import net.lemonsoft.lemonhello.LemonHello;
import net.lemonsoft.lemonhello.LemonHelloAction;
import net.lemonsoft.lemonhello.LemonHelloInfo;
import net.lemonsoft.lemonhello.LemonHelloView;
import net.lemonsoft.lemonhello.interfaces.LemonHelloActionDelegate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainYwActivity extends BaseActivity implements View.OnClickListener {
    RecyclerView mRv;
    private MainYwAdapter mainYwAdapter;
    private String mYwId = "1";
    private String mYwParam = Contact.JDC_YW_PARAM;
    private TextView mTvTime;
    private TextView mTvVehicleYw;
    private TextView mTvVehicleMan;
    private Badge mBadgeVehicleYw;
    private Badge mBadgeVehicleMan;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main_yw;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mainYwAdapter != null) {
            mainYwAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void initView() {
        mRv = findViewById(R.id.rv_main_yw_list);
        mTvVehicleYw = findViewById(R.id.tv_main_vehicle_yw);
        mTvVehicleYw.setOnClickListener(this);
        mTvVehicleMan = findViewById(R.id.tv_main_vehicle_man_yw);
        mTvVehicleMan.setOnClickListener(this);
        findViewById(R.id.iv_main_yw_logo).setOnClickListener(this);
        findViewById(R.id.bt_main_yw_sure).setOnClickListener(this);
        //机动车业务选中状态
        mBadgeVehicleYw = new QBadgeView(this).setBadgeText(getString(R.string.selected_title)).bindTarget(mTvVehicleYw);
        //驾驶人业务选中状态
        mBadgeVehicleMan = new QBadgeView(this).setBadgeText(getString(R.string.selected_title)).bindTarget(mTvVehicleMan);
        mBadgeVehicleMan.hide(true);

        //顶部渐变颜色
        View viewTop = findViewById(R.id.divider_top);
        ShadowDrawable.setShadowDrawable(viewTop, new int[]{
                        Color.parseColor("#536DFE"), Color.parseColor("#7C4DFF")}, SizeUtils.dp2px(1),
                Color.parseColor("#997C4DFF"), SizeUtils.dp2px(1), SizeUtils.dp2px(1), SizeUtils.dp2px(1));
        //确定按钮渐变色
        //#000000->#0f9b0f
        TextView btSure = findViewById(R.id.bt_main_yw_sure);
        ShadowDrawable.setShadowDrawable(btSure, new int[]{
                        Color.parseColor("#536DFE"), Color.parseColor("#7C4DFF")},
                SizeUtils.dp2px(6),
                Color.parseColor("#997C4DFF"),
                SizeUtils.dp2px(6), SizeUtils.dp2px(5), SizeUtils.dp2px(2));
        mTvTime = findViewById(R.id.tv_main_yw_time);
    }

    @Override
    public void initData() {
        setButtonWithJBColor(true);
        initTime();
        initRecyclerView();
    }

    /**
     * 设置机动车业务和驾驶人业务按钮通过渐变色显示
     * 未选中渐变色[#da4453,#89216b]
     * 选中的渐变色[#636363,#a2ab58]
     */
    private void setButtonWithJBColor(boolean isVehicleYw) {
        if (isVehicleYw) {
            mTvVehicleYw.setSelected(true);
            mTvVehicleMan.setSelected(false);
        } else {
            mTvVehicleYw.setSelected(false);
            mTvVehicleMan.setSelected(true);
        }
        if (mTvVehicleYw.isSelected()) {
            showShadowTv(mTvVehicleMan, new String[]{"#636363", "#a2ab58", "#99636363"});
            showShadowTv(mTvVehicleYw, new String[]{"#da4453", "#89216b", "#99da4453"});
        } else {
            showShadowTv(mTvVehicleYw, new String[]{"#636363", "#a2ab58", "#99636363"});
            showShadowTv(mTvVehicleMan, new String[]{"#da4453", "#89216b", "#99da4453"});
        }

        if (mTvVehicleMan.isSelected()) {
            showShadowTv(mTvVehicleYw, new String[]{"#636363", "#a2ab58", "#99636363"});
            showShadowTv(mTvVehicleMan, new String[]{"#da4453", "#89216b", "#99da4453"});
        } else {
            showShadowTv(mTvVehicleMan, new String[]{"#636363", "#a2ab58", "#99636363"});
            showShadowTv(mTvVehicleYw, new String[]{"#da4453", "#89216b", "#99da4453"});
        }
    }

    /*
     * 显示带阴影的按钮
     * @param tv 控件
     * @param colors 针对于两个渐变色的情况，第三个是阴影的颜色
     */
    private void showShadowTv(TextView tv, String[] colors) {
        ShadowDrawable.setShadowDrawable(tv, new int[]{
                        Color.parseColor(colors[0]),
                        Color.parseColor(colors[1])},
                SizeUtils.dp2px(4),
                Color.parseColor(colors[2]),
                SizeUtils.dp2px(3),
                SizeUtils.dp2px(4), SizeUtils.dp2px(4));
    }

    /**
     * 初始化日期展示 年月日 周 时分秒
     * yyyy-MM-dd HH:mm:ss
     */
    private void initTime() {
        //每秒更新一次时间
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Date date = new Date();
                        String year = TimeUtils.date2String(date, "yyyy");
                        String month = TimeUtils.date2String(date, "MM");
                        String day = TimeUtils.date2String(date, "dd");
                        String week = TimeUtils.getChineseWeek(date);
                        String time = TimeUtils.date2String(date, "HH:mm:ss");
                        final String finalTime = year + "年" + month + "月" + day + "日 " + week + " " + time;
                        mTvTime.setText(finalTime);
                    }
                });
            }
        };

        timer.schedule(timerTask, 0, 1000);
    }

    /**
     * 初始化RecyclerView
     */
    @SuppressLint("NewApi")
    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        mRv.setLayoutManager(layoutManager);
        mainYwAdapter = new MainYwAdapter(R.layout.item_yw, null);
        mainYwAdapter.setCurrentPageCountAndYwlx(mYwId);
        mRv.setAdapter(mainYwAdapter);
        mainYwAdapter.bindToRecyclerView(mRv);
        getAllYw(mYwId);

    }

    /**
     * 获取业务信息列表
     * <p>
     * //     * @param pageCount 当前页面展示的业务数
     * //     * @param current   当前页码
     *
     * @param ywId 机动车业务还是驾驶人业务
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAllYw(String ywId) {
        mainYwAdapter.setNewData(null);
        if (!TextUtils.isEmpty(getYwDataFromCache())) {
            MainYwAllBean s = GsonProvider.getInstance().getGson().fromJson(getYwDataFromCache(), MainYwAllBean.class);
            mainYwAdapter.setNewData(s.getDatas());
            return;
        }
        showLoading();
        mainYwAdapter.setCurrentPageCountAndYwlx(mYwParam);
        String deviceId = Tools.getDeviceId();
        EasyHttp.get(Contact.GET_ALL_YW)
                .params("key", deviceId)
                .params("rsa", Tools.encodeRsa(deviceId))
                .params("ownerid", ywId)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        e.printStackTrace();
                        dismissLoading();
                        showTip("访问服务器失败", "错误信息" + e.getMessage());
                    }

                    @Override
                    public void onSuccess(String res) {
                        dismissLoading();
                        Log.i("业务信息", res);
                        MainYwAllBean s = GsonProvider.getInstance().getGson().fromJson(res, MainYwAllBean.class);
                        if (s.isSuccess()) {
                            if (s.getDatas() != null && !s.getDatas().isEmpty()) {
                                //返回总共数据页码数
                                mainYwAdapter.setNewData(s.getDatas());
                                if (mYwParam.equals(Contact.JSR_YW_PARAM)) {
                                    DiskLruCacheHelper.getInstance().put(Contact.JSR_YW_DATA, res);
                                } else if (mYwParam.equals(Contact.JDC_YW_PARAM)) {
                                    DiskLruCacheHelper.getInstance().put(Contact.JDC_YW_DATA, res);
                                }
                            } else {
                                showTip("空信息", "暂无有效的信息");
                            }
                        } else {
                            showTip("错误信息", s.getMessage());
                        }

                    }

                });
    }

    private String getYwDataFromCache() {
        if (mYwId.equals("1")) {
            return DiskLruCacheHelper.getInstance().getAsString(Contact.JDC_YW_DATA);
        } else if (mYwId.equals("2")) {
            return DiskLruCacheHelper.getInstance().getAsString(Contact.JSR_YW_DATA);
        }
        return "";
    }

    private void showTip(String title, String msg) {
        LemonHello.getWarningHello(title, msg)
                .addAction(new LemonHelloAction(getString(R.string.sure), new LemonHelloActionDelegate() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(LemonHelloView lemonHelloView, LemonHelloInfo lemonHelloInfo, LemonHelloAction lemonHelloAction) {
                        lemonHelloView.hide();
                        getAllYw(mYwId);
                    }
                }))
                .show(this);

    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        switch (viewId) {
            //机动车业务
            case R.id.tv_main_vehicle_yw:
                handleYw(true, false, "1", Contact.JDC_YW_PARAM);
                break;
            //驾驶人业务
            case R.id.tv_main_vehicle_man_yw:
                handleYw(false, true, "2", Contact.JSR_YW_PARAM);
                break;
            //确认
            case R.id.bt_main_yw_sure:
                toSureActivity();
                break;
            //检查版本更新
            case R.id.iv_main_yw_logo:
                UpdateUtil.checkUpdate(this, true);
                break;
            default:
                break;
        }
    }

    /**
     * 跳转到业务确认页面
     * 确认
     */
    private void toSureActivity() {
        //判断是否有选中的业务
        //机动车业务就是一组
        String resJdc = DiskLruCacheHelper.getInstance().getAsString(Contact.JDC_YW_PARAM);
        //添加驾驶人业务
        String resJsr = DiskLruCacheHelper.getInstance().getAsString(Contact.JSR_YW_PARAM);
        if (ObjectUtils.isNotEmpty(resJdc) && !resJdc.equals("{}") || ObjectUtils.isNotEmpty(resJsr) && !resJsr.equals("{}")) {
            startActivity(new Intent(MainYwActivity.this, SelectedYwSureActivity.class));
        } else {
            showTip(getString(R.string.empty_selected_tip));
        }
    }

    /**
     * 处理机动车或者驾驶人业务
     *
     * @param b          true 选中机动车 false驾驶人
     * @param b2
     * @param s          业务id（根据切换的场景设置）
     * @param jdcYwParam 保存业务类型key
     */
    @SuppressLint("NewApi")
    private void handleYw(boolean b, boolean b2, String s, String jdcYwParam) {
        if (b2) {
            if (mBadgeVehicleYw != null) {
                mBadgeVehicleYw.hide(b2);
            }
            if (mBadgeVehicleMan != null) {
                mBadgeVehicleMan.setBadgeText(getString(R.string.selected_title));
            }
        } else if (b) {
            if (mBadgeVehicleYw != null) {
                mBadgeVehicleYw.setBadgeText(getString(R.string.selected_title));
            }
            if (mBadgeVehicleMan != null) {
                mBadgeVehicleMan.hide(b);
            }
        }

        setButtonWithJBColor(b);
        mYwId = s;
        mYwParam = jdcYwParam;
        getAllYw(mYwId);
    }

    private void showTip(String tip) {
        LemonHello.getErrorHello(tip, "")
                .addAction(new LemonHelloAction(getString(R.string.sure), new LemonHelloActionDelegate() {
                    @Override
                    public void onClick(LemonHelloView lemonHelloView, LemonHelloInfo lemonHelloInfo, LemonHelloAction lemonHelloAction) {
                        lemonHelloView.hide();
                    }
                })).show(this);

    }
}

