package com.jackg.programmers.challenges.imagememo.rvutil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.jackg.programmers.challenges.imagememo.R;
import com.jackg.programmers.challenges.imagememo.data.MemoEntity;
import com.jackg.programmers.challenges.imagememo.databinding.MemoListLayoutBinding;
import com.jackg.programmers.challenges.imagememo.ui.MemoListFragmentDirections;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<MemoEntity> items;

    public CustomAdapter() {
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MemoListLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.memo_list_layout, parent, false);

        return new CustomViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        if (items != null) {
            holder.binding.setEntity(items.get(position));
            holder.binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        if (items != null) return items.size();
        return 0;
    }

    public void setItems(List<MemoEntity> items) {
        this.items = (ArrayList<MemoEntity>) items;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        MemoListLayoutBinding binding;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.binding = DataBindingUtil.bind(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    NavController controller = Navigation.findNavController(v);

                    MemoListFragmentDirections.ActionMemoListFragmentToMemoDetailFragment action =
                            MemoListFragmentDirections.actionMemoListFragmentToMemoDetailFragment();
                    action.setMemoId(items.get(getAdapterPosition()).getMemoId());

                    controller.navigate(action);
                }
            });
        }
    }
}
