package com.bhkj.pdjhforotherapp.core.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bhkj.pdjhforotherapp.R;
import com.bhkj.pdjhforotherapp.common.utils.UpdateUtil;
import com.qiangxi.checkupdatelibrary.Q;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;


public abstract class BaseActivity extends AppCompatActivity {

    private ZLoadingDialog mLoadingDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
            initView();
            initData();
        } else {
            throw new RuntimeException("LayoutId must not be null");
        }


    }


    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void initData();

    /**
     * 展示动画效果
     */
    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new ZLoadingDialog(this);
        }
        mLoadingDialog.setLoadingBuilder(Z_TYPE.SNAKE_CIRCLE)//设置类型
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .setLoadingColor(Color.WHITE)//颜色
                .setHintText("正在加载业务项目...")
                .setHintTextSize(24) // 设置字体大小 dp
                .setHintTextColor(Color.WHITE)  // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(Color.parseColor("#CC111111")) // 设置背景色，默认白色
                .show();

    }

    /**
     * 关闭加载动画效果
     */
    public void dismissLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }


}
