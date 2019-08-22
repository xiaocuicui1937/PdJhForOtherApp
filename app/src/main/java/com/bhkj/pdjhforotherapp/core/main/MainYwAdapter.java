package com.bhkj.pdjhforotherapp.core.main;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.anupcowkur.reservoir.Reservoir;
import com.bhkj.pdjhforotherapp.R;
import com.bhkj.pdjhforotherapp.common.Contact;
import com.bhkj.pdjhforotherapp.common.bean.MainYwAllBean;
import com.bhkj.pdjhforotherapp.common.bean.MainYwBean;
import com.bhkj.pdjhforotherapp.common.bean.SelectedItemBean;
import com.bhkj.pdjhforotherapp.common.callback.CallbackManager;
import com.bhkj.pdjhforotherapp.common.callback.IGlabolCallback;
import com.bhkj.pdjhforotherapp.common.parse.GsonProvider;
import com.bhkj.pdjhforotherapp.common.reservoir.ReservoirUtils;
import com.bhkj.pdjhforotherapp.common.simpledisk.DiskLruCache;
import com.bhkj.pdjhforotherapp.common.simpledisk.DiskLruCacheHelper;
import com.bhkj.pdjhforotherapp.common.utils.StaticDataIntent;
import com.bhkj.pdjhforotherapp.common.view.ShadowDrawable;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import q.rorbin.badgeview.QBadgeView;

@SuppressWarnings({"unchecked", "RedundantCollectionOperation"})
public class MainYwAdapter extends BaseQuickAdapter<MainYwAllBean.DatasBean, BaseViewHolder> {
    RequestOptions requestOptions = new RequestOptions()
            .fitCenter()
            .override(60, 60);
    //    private List<SelectedItemBean> mPagesList = new ArrayList<>();//保存每页选中业务，以及业务数
    private HashMap<String, SelectedItemBean> mPagesMap = new HashMap<>();
    private String mYwlx;

    //string ->id ,integer->选中业务数
    public MainYwAdapter(int layoutResId, @Nullable List<MainYwAllBean.DatasBean> data) {
        super(layoutResId, data);
    }

    public void setCurrentPageCountAndYwlx(String ywlx) {
        this.mYwlx = ywlx;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void convert(BaseViewHolder helper, MainYwAllBean.DatasBean item) {
        ShadowDrawable.setShadowDrawable(helper.itemView, new int[]{
                        Color.parseColor("#536DFE"),
                        Color.parseColor("#7C4DFF")},
                SizeUtils.dp2px(2),
                Color.parseColor("#997C4DFF"),
                SizeUtils.dp2px(1), SizeUtils.dp2px(2), SizeUtils.dp2px(2));

        ImageView iv = helper.getView(R.id.iv_item_yw);
        TextView tv = helper.getView(R.id.tv_item_yw);
        String imgUrl = Contact.BASE_URL + item.getImg();
        String ywName = item.getName();
        final ImageView ivFlag = helper.getView(R.id.iv_item_select_flag);
        ivFlag.setTag(R.id.ids, item.getId());
        TextView tvCount = helper.getView(R.id.tv_item_count);
        Glide.with(iv.getContext()).applyDefaultRequestOptions(requestOptions).load(imgUrl).into(iv);
        tv.setText(ywName);
        //展示已经选中的业务情况
        showSelectedStatus(ivFlag, helper, item, tvCount);
    }

    private void showSelectedStatus(ImageView ivFlag, BaseViewHolder helper, MainYwAllBean.DatasBean item, TextView tvCount) {
        String selectedItemBeansStr = DiskLruCacheHelper.getInstance().getAsString(mYwlx);
        String jsrSelectedYw = DiskLruCacheHelper.getInstance().getAsString(Contact.JSR_YW_PARAM);
        String jdcSelectedYw = DiskLruCacheHelper.getInstance().getAsString(Contact.JDC_YW_PARAM);
        //如果缓存中没有数据说明数据在确认页面被清除了或者是第一次加载数据
        if (TextUtils.isEmpty(jsrSelectedYw) && TextUtils.isEmpty(jdcSelectedYw)) {
            mPagesMap.clear();
        }
//        ToastUtils.showShort(selectedItemBeansStr + "", selectedItemBeansStr + "数据");
        HashMap<String, SelectedItemBean> selectedItemBeans = GsonProvider.getInstance().getGson().fromJson(selectedItemBeansStr,
                new TypeToken<HashMap<String, SelectedItemBean>>() {
                }.getType());

        if (selectedItemBeans != null && selectedItemBeans.size() != 0) {
            String id = item.getId();

            if (selectedItemBeans.containsKey(id) && ObjectUtils.isNotEmpty(selectedItemBeans.get(id))) {
                Log.i("数据位置", "position:" + helper.getAdapterPosition() + "--" + "\r\n" + "--to" + id + "\r\n" +
                        selectedItemBeans.toString());
                ivFlag.setVisibility(View.VISIBLE);
                tvCount.setVisibility(View.VISIBLE);
                tvCount.setText(String.valueOf(selectedItemBeans.get(id).getCount()));
                selectedItem(helper, selectedItemBeans.get(id).getCount());
            } else {
                defalutItemShow(ivFlag, helper, tvCount);
            }
        } else {
            defalutItemShow(ivFlag, helper, tvCount);
        }
    }

    /**
     * 默认显示item
     *
     * @param ivFlag
     * @param helper
     * @param tvCount
     */
    private void defalutItemShow(ImageView ivFlag, BaseViewHolder helper, TextView tvCount) {
        ivFlag.setVisibility(View.GONE);
        tvCount.setVisibility(View.GONE);
        selectedItem(helper, 0);

    }

    /**
     * 选中业务item右上角显示对号选中效果
     * 右下角显示选中当前业务的个数
     *
     * @param holder Adapter中的复用实例
     */
    private void selectedItem(final BaseViewHolder holder, int count) {
        final ImageView ivFlag = holder.getView(R.id.iv_item_select_flag);
        final TextView tvCount = holder.getView(R.id.tv_item_count);
        final int[] countClick = {count};//有效点击次数
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                countClick[0]++;
                ivFlag.setVisibility(View.VISIBLE);
                tvCount.setVisibility(View.VISIBLE);
                tvCount.setText(String.valueOf(countClick[0]));
                saveSelectedParam(holder, countClick[0]);
            }
        });

        ivFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countClick[0] > 0) {
                    countClick[0]--;
                }
                if (countClick[0] == 0) {
                    ivFlag.setVisibility(View.GONE);
                    tvCount.setVisibility(View.GONE);
                } else {
                    tvCount.setText(String.valueOf(countClick[0]));
                }
                saveSelectedParam(holder, countClick[0]);
            }
        });
    }

    /**
     * 保存每页选中业务信息和业务叠加数量
     *
     * @param holder   BaseViewHolder
     * @param subCount 叠加的业务量
     */
    private void saveSelectedParam(BaseViewHolder holder, int subCount) {
        final int index = holder.getAdapterPosition();
        MainYwAllBean.DatasBean recordsBean = getItem(index);
        if (recordsBean == null) {
            return;
        }

        if (subCount == 0) {
            if (mPagesMap.containsKey(recordsBean.getId())) {
                Log.i("new-3", "清除以保存的实例" + recordsBean.getId() + "-索引-" + index);
                mPagesMap.remove(recordsBean.getId());
            }
        } else {
            SelectedItemBean selectedItemBean;
            if (mPagesMap.containsKey(recordsBean.getId())) {
                selectedItemBean = mPagesMap.get(recordsBean.getId());
                if (selectedItemBean != null) {
                    Log.i("new-2", "获取已保存的实例" + recordsBean.getId() + "-索引-" + index);
                    selectedItemBean.setId(recordsBean.getId());
                    selectedItemBean.setName(recordsBean.getName());
                    selectedItemBean.setImg(recordsBean.getImg());
                    selectedItemBean.setCount(subCount);
                    selectedItemBean.setYwlx(mYwlx);
                    mPagesMap.put(recordsBean.getId(), selectedItemBean);
                }
            } else {
                Log.i("new-1", "创建新的实例" + recordsBean.getId() + "-索引-" + index);
                selectedItemBean = new SelectedItemBean();
                selectedItemBean.setId(recordsBean.getId());
                selectedItemBean.setName(recordsBean.getName());
                selectedItemBean.setImg(recordsBean.getImg());
                selectedItemBean.setCount(subCount);
                selectedItemBean.setYwlx(mYwlx);
                mPagesMap.put(recordsBean.getId(), selectedItemBean);
            }
        }

        String pages = GsonProvider.getInstance().getGson().toJson(mPagesMap);
        DiskLruCacheHelper.getInstance().put(mYwlx, pages);
        ToastUtils.showShort(pages);
        Log.i("选中的数据", "选中的数据:" + pages);
    }


}

