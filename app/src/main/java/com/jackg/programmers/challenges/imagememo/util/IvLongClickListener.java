package com.jackg.programmers.challenges.imagememo.util;

import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class IvLongClickListener implements View.OnLongClickListener {

    private List<String> items;

    public IvLongClickListener(List<String> items) {
        this.items = items;
    }

    @Override
    public boolean onLongClick(View v) {
        new MaterialAlertDialogBuilder(v.getContext())
                .setTitle("이미지 삭제")
                .setMessage("이미지는 목록에서만 삭제됩니다. 파일을 삭제하시려면 겔러리에서 삭제하시거나 앱을 지워주세요.")
                .setPositiveButton("삭제", (dialog, which) -> {
                    String url = String.valueOf(v.getTag());

                    if (items.remove(url)) ((LinearLayout) v.getParent()).removeView(v);
                })
                .setNegativeButton("취소", (dialog, which) -> dialog.cancel()).show();
        return true;
    }
}
