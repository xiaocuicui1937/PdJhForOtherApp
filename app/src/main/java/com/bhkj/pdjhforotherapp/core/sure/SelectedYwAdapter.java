package com.bhkj.pdjhforotherapp.core.sure;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhkj.pdjhforotherapp.R;
import com.bhkj.pdjhforotherapp.app.App;
import com.bhkj.pdjhforotherapp.common.Contact;
import com.bhkj.pdjhforotherapp.common.bean.MainYwBean;
import com.bhkj.pdjhforotherapp.common.bean.SelectedItemBean;
import com.bhkj.pdjhforotherapp.common.callback.CallbackManager;
import com.bhkj.pdjhforotherapp.common.callback.IGlabolCallback;
import com.bhkj.pdjhforotherapp.common.parse.GsonProvider;
import com.bhkj.pdjhforotherapp.common.reservoir.ReservoirUtils;
import com.bhkj.pdjhforotherapp.common.simpledisk.DiskLruCacheHelper;
import com.bhkj.pdjhforotherapp.common.utils.StaticDataIntent;
import com.bhkj.pdjhforotherapp.common.view.ShadowDrawable;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import q.rorbin.badgeview.QBadgeView;

@SuppressWarnings({"unchecked", "RedundantCollectionOperation"})
public class SelectedYwAdapter extends BaseQuickAdapter<SelectedItemBean, BaseViewHolder> {
    private HashMap<String, SelectedItemBean> pageMapForTime;
    RequestOptions requestOptions = new RequestOptions()
            .fitCenter()
            .override(60, 60);


    //string ->id ,integer->选中业务数
    public SelectedYwAdapter(int layoutResId, @Nullable List<SelectedItemBean> data) {
        super(layoutResId, data);

    }


    @Override
    protected void convert(BaseViewHolder helper, SelectedItemBean item) {
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
        ivFlag.setTag(item.getId());
        TextView tvCount = helper.getView(R.id.tv_item_count);
        Glide.with(iv.getContext()).applyDefaultRequestOptions(requestOptions).load(imgUrl).into(iv);
        tv.setText(ywName);
        //根据业务类型，设置不同颜色的标签
        showMarkForYw(helper, item, iv);
        //展示已经选中的业务情况
        showYwDetail(helper, item, ivFlag, tvCount);
    }

    /**
     * 显示标签针对于驾驶人业务和激动车业务
     *
     * @param helper
     * @param item
     * @param iv
     */
    private void showMarkForYw(BaseViewHolder helper, SelectedItemBean item, ImageView iv) {
        if (item.getYwlx().equals(Contact.JSR_YW_PARAM)) {
            new QBadgeView(iv.getContext()).setBadgeText(App.getCtx().getString(R.string.verhicle_man_yw))
                    .setBadgeBackgroundColor(ContextCompat.getColor(App.getCtx(), R.color.fuchsia)).setBadgeGravity(Gravity.START | Gravity.TOP).bindTarget(helper.getView(R.id.cl_item_root));
        } else if (Contact.JDC_YW_PARAM.equals(item.getYwlx())) {
            new QBadgeView(iv.getContext()).setBadgeText(App.getCtx().getString(R.string.verhicle_yw))
                    .setBadgeBackgroundColor(ContextCompat.getColor(App.getCtx(), R.color.chocolate)).setBadgeGravity(Gravity.START | Gravity.TOP).bindTarget(helper.getView(R.id.cl_item_root));
        }
    }


    /**
     * 显示选中的业务，以及增删业务执行的逻辑
     *
     * @param helper
     * @param item
     * @param ivFlag
     * @param tvCount
     */
    private void showYwDetail(BaseViewHolder helper, SelectedItemBean item, ImageView ivFlag, TextView tvCount) {
        String res = DiskLruCacheHelper.getInstance().getAsString(item.getYwlx());
        ToastUtils.showShort(res);
        pageMapForTime = GsonProvider.getInstance().getGson().fromJson(res,
                new TypeToken<HashMap<String, SelectedItemBean>>() {
                }.getType());

        if (pageMapForTime != null && pageMapForTime.size() != 0) {
            String id = item.getId();
            if (pageMapForTime.containsKey(item.getId()) && ObjectUtils.isNotEmpty(pageMapForTime.get(id))) {
                ivFlag.setVisibility(View.VISIBLE);
                tvCount.setVisibility(View.VISIBLE);
                tvCount.setText(String.valueOf(pageMapForTime.get(id).getCount()));
                ToastUtils.showShort(pageMapForTime.toString());
                selectedItem(helper, pageMapForTime.get(id).getCount());
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
        //增加选中业务个数
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
        //减少选中业务个数
        ivFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countClick[0] > 0) {
                    countClick[0]--;
                }
                saveSelectedParam(holder, countClick[0]);
                if (countClick[0] == 0) {
                    ivFlag.setVisibility(View.GONE);
                    tvCount.setVisibility(View.GONE);
                    holder.itemView.setVisibility(View.GONE);
                } else {
                    tvCount.setText(String.valueOf(countClick[0]));
                }

                if ("".equals(pageMapForTime.toString()) || "{}".equals(pageMapForTime.toString())) {
                    ActivityUtils.finishActivity((SelectedYwSureActivity) holder.itemView.getContext());
                    if (pageMapForTime != null) {
                        pageMapForTime.clear();
                    }
                    //清除所有选中的业务
                    DiskLruCacheHelper.getInstance().remove(Contact.JSR_YW_PARAM);
                    DiskLruCacheHelper.getInstance().remove(Contact.JDC_YW_PARAM);
                }
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
        SelectedItemBean recordsBean = getItem(index);
        if (recordsBean == null) {
            return;
        }
        if (subCount == 0) {
            Iterator iterator = pageMapForTime.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                if (key.equals(recordsBean.getId())) {
                    iterator.remove();
                    pageMapForTime.remove(key);
                }
            }
        } else if (subCount > 0) {
            SelectedItemBean selectedItemBean;
            if (pageMapForTime.containsKey(recordsBean.getId())) {
                selectedItemBean = pageMapForTime.get(recordsBean.getId());
                if (selectedItemBean != null) {
                    selectedItemBean.setId(recordsBean.getId());
                    selectedItemBean.setName(recordsBean.getName());
                    selectedItemBean.setImg(recordsBean.getImg());
                    selectedItemBean.setCount(subCount);
                    selectedItemBean.setYwlx(recordsBean.getYwlx());
                    selectedItemBean.setPageCount(recordsBean.getPageCount());
                    pageMapForTime.put(recordsBean.getId(), selectedItemBean);
                }
            } else {
                selectedItemBean = new SelectedItemBean();
                selectedItemBean.setId(recordsBean.getId());
                selectedItemBean.setName(recordsBean.getName());
                selectedItemBean.setImg(recordsBean.getImg());
                selectedItemBean.setCount(subCount);
                selectedItemBean.setYwlx(recordsBean.getYwlx());
                selectedItemBean.setPageCount(recordsBean.getPageCount());
                pageMapForTime.put(recordsBean.getId(), selectedItemBean);
            }

        }

        ToastUtils.showShort(index + "\r\n" + recordsBean.getId() + "\r\n" + pageMapForTime.toString());

        String s = GsonProvider.getInstance().getGson().toJson(pageMapForTime);
        DiskLruCacheHelper.getInstance().put(recordsBean.getYwlx(), s);
    }


}

