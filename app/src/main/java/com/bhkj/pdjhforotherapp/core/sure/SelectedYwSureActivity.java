package com.bhkj.pdjhforotherapp.core.sure;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bhkj.pdjhforotherapp.R;
import com.bhkj.pdjhforotherapp.common.Contact;
import com.bhkj.pdjhforotherapp.common.bean.QueueBusiness;
import com.bhkj.pdjhforotherapp.common.bean.SelectedItemBean;
import com.bhkj.pdjhforotherapp.common.callback.CallbackManager;
import com.bhkj.pdjhforotherapp.common.callback.IGlabolCallback;
import com.bhkj.pdjhforotherapp.common.parse.GsonProvider;
import com.bhkj.pdjhforotherapp.common.reservoir.ReservoirUtils;
import com.bhkj.pdjhforotherapp.common.simpledisk.DiskLruCache;
import com.bhkj.pdjhforotherapp.common.simpledisk.DiskLruCacheHelper;
import com.bhkj.pdjhforotherapp.common.utils.StaticDataIntent;
import com.bhkj.pdjhforotherapp.common.utils.UpdateUtil;
import com.bhkj.pdjhforotherapp.common.view.ShadowDrawable;
import com.bhkj.pdjhforotherapp.core.base.BaseActivity;
import com.bhkj.pdjhforotherapp.core.main.MainYwAdapter;
import com.bhkj.pdjhforotherapp.core.print.util.Constant;
import com.bhkj.pdjhforotherapp.core.sfsure.JHDetailActivity;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import net.lemonsoft.lemonhello.LemonHello;
import net.lemonsoft.lemonhello.LemonHelloAction;
import net.lemonsoft.lemonhello.LemonHelloInfo;
import net.lemonsoft.lemonhello.LemonHelloView;
import net.lemonsoft.lemonhello.interfaces.LemonHelloActionDelegate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("unchecked")
public class SelectedYwSureActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView mRv;
    private SelectedYwAdapter mSelectedYwAdapter;
    private TextView mTvTime;

    @Override
    public int getLayoutId() {
        return R.layout.activity_selected_yw_sure;
    }

    @Override
    public void initView() {
        mRv = findViewById(R.id.rv_selected_yw_list);
        TextView pickNum = findViewById(R.id.bt_selected_yw_picknum);
        pickNum.setOnClickListener(this);
        TextView back = findViewById(R.id.tv_selected_yw_back);
//        findViewById(R.id.tv_selected_yw_next_page).setOnClickListener(this);
        back.setOnClickListener(this);
        findViewById(R.id.iv_selected_yw_logo).setOnClickListener(this);
        //顶部渐变颜色
        View viewTop = findViewById(R.id.divider_top_selected);
        ShadowDrawable.setShadowDrawable(viewTop, new int[]{
                        Color.parseColor("#536DFE"), Color.parseColor("#7C4DFF")}, SizeUtils.dp2px(1),
                Color.parseColor("#997C4DFF"), SizeUtils.dp2px(1), SizeUtils.dp2px(1), SizeUtils.dp2px(1));
        mTvTime = findViewById(R.id.tv_selected_yw_time);
        TextView vipTvPickup = findViewById(R.id.bt_selected_yw_vip_pick);
        vipTvPickup.setOnClickListener(this);
        TextView clearAllTv = findViewById(R.id.tv_selected_yw_clear_all);
        clearAllTv.setOnClickListener(this);
        //按钮渐变色
        ShadowDrawable.setShadowDrawable(pickNum, new int[]{
                        Color.parseColor("#CE9FFC"), Color.parseColor("#7367F0")},
                SizeUtils.dp2px(6),
                Color.parseColor("#99CE9FFC"),
                SizeUtils.dp2px(6), SizeUtils.dp2px(5), SizeUtils.dp2px(2));

        ShadowDrawable.setShadowDrawable(back, new int[]{
                        Color.parseColor("#6495ed"), Color.parseColor("#556b2f")},
                SizeUtils.dp2px(6),
                Color.parseColor("#996495ed"),
                SizeUtils.dp2px(6), SizeUtils.dp2px(5), SizeUtils.dp2px(2));

        ShadowDrawable.setShadowDrawable(clearAllTv, new int[]{
                        Color.parseColor("#FCCF31"), Color.parseColor("#F55555")},
                SizeUtils.dp2px(6),
                Color.parseColor("#99FCCF31"),
                SizeUtils.dp2px(6), SizeUtils.dp2px(5), SizeUtils.dp2px(2));

        ShadowDrawable.setShadowDrawable(vipTvPickup, new int[]{
                        Color.parseColor("#008000"), Color.parseColor("#006400")},
                SizeUtils.dp2px(6),
                Color.parseColor("#99008000"),
                SizeUtils.dp2px(6), SizeUtils.dp2px(5), SizeUtils.dp2px(2));
    }

    @Override
    public void initData() {
        initTime();
        initRecyclerView();
        showSelectedYw();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        mRv.setLayoutManager(layoutManager);
        mSelectedYwAdapter = new SelectedYwAdapter(R.layout.item_yw, null);
        mRv.setAdapter(mSelectedYwAdapter);
        mSelectedYwAdapter.bindToRecyclerView(mRv);
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

    @SuppressWarnings("unchecked")
    private void showSelectedYw() {

        List<SelectedItemBean> allSelectedItemBeans = new ArrayList<>();

        //添加机动车业务
        String resJdc = DiskLruCacheHelper.getInstance().getAsString(Contact.JDC_YW_PARAM);
        HashMap<String, SelectedItemBean> selectedItemBeansJdc = GsonProvider.getInstance().getGson().fromJson(resJdc,
                new TypeToken<HashMap<String, SelectedItemBean>>() {
                }.getType());
        if (selectedItemBeansJdc != null && !selectedItemBeansJdc.isEmpty()) {
            for (String key : selectedItemBeansJdc.keySet()) {
                if (Contact.JDC_YW_PARAM.equals(selectedItemBeansJdc.get(key).getYwlx())) {
                    allSelectedItemBeans.add(selectedItemBeansJdc.get(key));
                }
            }
        }
        //添加驾驶人业务
        String res = DiskLruCacheHelper.getInstance().getAsString(Contact.JSR_YW_PARAM);
//            Log.i("response",res);
        HashMap<String, SelectedItemBean> selectedItemBeans = GsonProvider.getInstance().getGson().fromJson(res,
                new TypeToken<HashMap<String, SelectedItemBean>>() {
                }.getType());
        if (selectedItemBeans != null && !selectedItemBeans.isEmpty()) {
            for (String key : selectedItemBeans.keySet()) {
                if (Contact.JSR_YW_PARAM.equals(selectedItemBeans.get(key).getYwlx())) {
                    allSelectedItemBeans.add(selectedItemBeans.get(key));
                }
            }
        }

        Log.i("mine", allSelectedItemBeans.toString());

        mSelectedYwAdapter.setNewData(allSelectedItemBeans);
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.bt_selected_yw_picknum:
                toJhDetail(Contact.COMMON_WINDOW);
                break;
            case R.id.tv_selected_yw_next_page:
                finish();
                break;
            case R.id.tv_selected_yw_back:
                finish();
                break;
            //清除所有选中的业务
            case R.id.tv_selected_yw_clear_all:
                clearAllYwTip();
                break;
                //检查版本更新
            case R.id.iv_selected_yw_logo:
                UpdateUtil.checkUpdate(this,true);
                break;
            case R.id.bt_selected_yw_vip_pick:
                toJhDetail(Contact.VIP_WINDOW);
                break;
            default:
                break;
        }
    }

    /**
     * 将选中的业务转换成json字符串传递到叫号详情页，刷完身份证将数据传递到服务中进行保存
     */
    private void toJhDetail(String qhType) {
        List<SelectedItemBean> data = mSelectedYwAdapter.getData();
        List<QueueBusiness> queueBusinesses = new ArrayList<>();
        for (SelectedItemBean param : data) {
            QueueBusiness queueBusiness = new QueueBusiness();
            queueBusiness.setBid(param.getId());
            queueBusiness.setBunum(param.getCount());
            queueBusinesses.add(queueBusiness);
        }
        String queueBusinessJson = GsonProvider.getInstance().getGson().toJson(queueBusinesses);
        Intent intent = new Intent(this, JHDetailActivity.class);
        intent.putExtra(Contact.SURE_YW,queueBusinessJson);
        intent.putExtra(Contact.QHTYPE,qhType);
        startActivity(intent);
    }

    private void clearAllYwTip() {
        LemonHello.getInformationHello("清除所选中的业务", "")
                .addAction(new LemonHelloAction(getString(R.string.cancel), new LemonHelloActionDelegate() {
                    @Override
                    public void onClick(LemonHelloView lemonHelloView, LemonHelloInfo lemonHelloInfo, LemonHelloAction lemonHelloAction) {
                        //隐藏提示
                        lemonHelloView.hide();
                    }
                }))
                .addAction(new LemonHelloAction(getString(R.string.sure), new LemonHelloActionDelegate() {
                    @Override
                    public void onClick(LemonHelloView lemonHelloView, LemonHelloInfo lemonHelloInfo, LemonHelloAction lemonHelloAction) {
                        lemonHelloView.hide();
                        //清除所有选中的业务
                        DiskLruCacheHelper.getInstance().remove(Contact.JDC_YW_PARAM);
                        DiskLruCacheHelper.getInstance().remove(Contact.JSR_YW_PARAM);
                        finish();
                    }
                })).show(this);
    }




}
