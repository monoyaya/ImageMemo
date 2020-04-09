package com.jackg.programmers.challenges.imagememo.bindingadapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jackg.programmers.challenges.imagememo.R;
import com.jackg.programmers.challenges.imagememo.data.MemoEntity;
import com.jackg.programmers.challenges.imagememo.rvutil.CustomAdapter;
import com.jackg.programmers.challenges.imagememo.util.CheckableIV;
import com.jackg.programmers.challenges.imagememo.util.IvLongClickListener;

import java.util.List;

public class BindingAdapter {

    @androidx.databinding.BindingAdapter("items")
    public static void items(RecyclerView rv, List<MemoEntity> list) {
        CustomAdapter adapter = (CustomAdapter) rv.getAdapter();
        if (adapter != null) {
            adapter.setItems(list);
            adapter.notifyDataSetChanged();
        }
    }

    @androidx.databinding.BindingAdapter("ivSetUrl")
    public static void setUrl(ImageView iv, List<String> items) {
        if (items.isEmpty() || items.size() == 0) {
            iv.setVisibility(View.GONE);
        } else {
            for (String url : items) {
                if (url.isEmpty()) {
                    iv.setVisibility(View.GONE);
                    break;
                } else iv.setVisibility(View.VISIBLE);

                float density = iv.getContext().getResources().getDisplayMetrics().density;
                int dp60 = Math.round(60 * density);

                Glide.with(iv.getContext()).load(url).override(dp60, dp60).circleCrop().error(R.mipmap.ic_launcher).into(iv);
                break;
            }
        }
    }

    @androidx.databinding.BindingAdapter("setDetailIv")
    public static void setDetailIv(NestedScrollView container, List<String> items) {
        LinearLayout group = (LinearLayout) container.getChildAt(0);

        if (group.getChildCount() > 0) group.removeAllViews();

        if (items == null || items.size() < 1) {
            container.setVisibility(View.GONE);
        } else {
            container.setVisibility(View.VISIBLE);

            float density = container.getContext().getResources().getDisplayMetrics().density;
            int dp10 = Math.round(10 * density);

            for (String url : items) {
                ImageView iv = new ImageView(container.getContext());

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(dp10, dp10, dp10, dp10);

                iv.setLayoutParams(params);
                iv.setTag(url);

                group.addView(iv);

                Glide.with(iv.getContext()).load(url).error(R.mipmap.ic_launcher).into(iv);
            }

            if (group.getMeasuredHeight() > (30 * dp10)) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) container.getLayoutParams();
                params.height = 30 * dp10;
                container.setLayoutParams(params);
            }
        }
    }

    @androidx.databinding.BindingAdapter("setModifyIv")
    public static void setModifyIv(LinearLayout layout, List<String> imgList) {
        if (imgList == null || imgList.size() < 1) {
            ((HorizontalScrollView) layout.getParent()).setVisibility(View.GONE);
            return;
        } else {
            ((HorizontalScrollView) layout.getParent()).setVisibility(View.VISIBLE);
        }

        float density = layout.getContext().getResources().getDisplayMetrics().density;
        int dp10 = Math.round(10 * density);

        if (imgList.size() > 0) {
            for (String url : imgList) {
                CheckableIV iv = new CheckableIV(layout.getContext());

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(dp10 * 6, dp10 * 6);
                params.setMargins(dp10, dp10, dp10, dp10);

                iv.setLayoutParams(params);
                iv.setTag(url);
                iv.setOnLongClickListener(new IvLongClickListener(imgList));

                layout.addView(iv);

                Glide.with(iv.getContext()).load(url).circleCrop().error(R.mipmap.ic_launcher).into(iv);
            }
        }
    }
}
