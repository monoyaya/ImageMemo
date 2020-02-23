package com.jackg.programmers.challenges.imagememo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jackg.programmers.challenges.imagememo.R;
import com.jackg.programmers.challenges.imagememo.data.MemoEntity;
import com.jackg.programmers.challenges.imagememo.databinding.MemoListFragmentBinding;
import com.jackg.programmers.challenges.imagememo.rvutil.CustomAdapter;
import com.jackg.programmers.challenges.imagememo.viewmodel.MemoViewModel;

import java.util.List;

public class MemoListFragment extends Fragment {

    private MemoListFragmentBinding binding;
    private MemoViewModel mViewModel;

    private RecyclerView rv;
    private LinearLayout noDataShowLayout;

    private FloatingActionButton fab;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        binding = DataBindingUtil.inflate(inflater, R.layout.memo_list_fragment, container, false);

        View root = binding.getRoot();

        noDataShowLayout = root.findViewById(R.id.no_data_layout);

        rv = root.findViewById(R.id.rvList);

        fab = root.findViewById(R.id.fab);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(MemoViewModel.class);

        CustomAdapter adapter = new CustomAdapter(requireActivity());
        rv.setAdapter(adapter);

        mViewModel.getData().observe(requireActivity(), new Observer<List<MemoEntity>>() {
            @Override
            public void onChanged(List<MemoEntity> entities) {
                if (entities.size() > 0) {
                    noDataShowLayout.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    binding.setMemoList(entities);
                } else {
                    noDataShowLayout.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                }
            }
        });

        fab.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_memoListFragment_to_memoWriteFragment));
    }
}
