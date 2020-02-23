package com.jackg.programmers.challenges.imagememo.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jackg.programmers.challenges.imagememo.R;
import com.jackg.programmers.challenges.imagememo.data.MemoEntity;
import com.jackg.programmers.challenges.imagememo.databinding.FragmentMemoDetailBinding;
import com.jackg.programmers.challenges.imagememo.viewmodel.MemoViewModel;

public class MemoDetailFragment extends Fragment implements View.OnClickListener {

    private FragmentMemoDetailBinding binding;
    private long memoId;

    private MemoViewModel mvm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_memo_detail, container, false);

        memoId = MemoDetailFragmentArgs.fromBundle(getArguments()).getMemoId();

        View root = binding.getRoot();

        MaterialButton bntList, bntModi, bntDel;

        bntList = root.findViewById(R.id.list_button);
        bntModi = root.findViewById(R.id.modify_button);
        bntDel = root.findViewById(R.id.delete_button);

        bntList.setOnClickListener(this);
        bntModi.setOnClickListener(this);
        bntDel.setOnClickListener(this);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mvm = new ViewModelProvider(requireActivity()).get(MemoViewModel.class);

        if (memoId > 0L) {
            mvm.getMemo(memoId).observe(requireActivity(), new Observer<MemoEntity>() {
                @Override
                public void onChanged(MemoEntity entity) {
                    binding.setEntity(entity);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list_button:
                NavHostFragment.findNavController(this).navigateUp();
                break;
            case R.id.modify_button:
                MemoDetailFragmentDirections.ActionMemoDetailFragmentToMemoModifyFragment action =
                        MemoDetailFragmentDirections.actionMemoDetailFragmentToMemoModifyFragment();

                action.setMemoId(memoId);

                NavHostFragment.findNavController(this).navigate(action);
                break;
            case R.id.delete_button:
                isDelete();
                break;
        }
    }

    private void isDelete() {
        new MaterialAlertDialogBuilder(requireContext()).setTitle("메모 삭제").setMessage("정말 삭제하시겠습니까?")
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("삭제", (dialog, which) -> {
                                mvm.deleteItem(binding.getEntity());

                                NavHostFragment.findNavController(this).navigateUp();

                                Toast.makeText(requireContext(), memoId + "번 메모 삭제 완료", Toast.LENGTH_SHORT).show();
                            }).show();
    }
}
