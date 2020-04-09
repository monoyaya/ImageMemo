package com.jackg.programmers.challenges.imagememo.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.jackg.programmers.challenges.imagememo.R;
import com.jackg.programmers.challenges.imagememo.data.MemoEntity;
import com.jackg.programmers.challenges.imagememo.databinding.FragmentMemoModifyBinding;
import com.jackg.programmers.challenges.imagememo.util.CheckableIV;
import com.jackg.programmers.challenges.imagememo.util.IvLongClickListener;
import com.jackg.programmers.challenges.imagememo.viewmodel.MemoViewModel;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class MemoModifyFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private static final int REQUEST_MODIFY_IMG_CAMERA = 184;
    private static final int REQUEST_MODIFY_IMG_GALLERY = 194;

    private FragmentMemoModifyBinding binding;

    private TextInputEditText etSubject, etContent;
    private LinearLayout layout;

    private ArrayList<String> imgList;
    private MemoViewModel viewModel;

    private long memoId;

    private boolean hasCamera = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_memo_modify, container, false);

        View root = binding.getRoot();

        memoId = MemoDetailFragmentArgs.fromBundle(getArguments()).getMemoId();

        layout = root.findViewById(R.id.ivContainer);

        etSubject = root.findViewById(R.id.sub_edit_txt);
        etContent = root.findViewById(R.id.con_edit_txt);

        etSubject.setOnFocusChangeListener(this);
        etContent.setOnFocusChangeListener(this);

        binding.submitButton.setOnClickListener(this);
        binding.cancelButton.setOnClickListener(this);
        binding.addImgButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        hasCamera = requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);

        viewModel = new ViewModelProvider(this).get(MemoViewModel.class);

        if (memoId > 0L) {
            viewModel.getMemo(memoId).observe(requireActivity(), entity -> {
                if (entity != null) {
                    imgList = (ArrayList<String>) entity.getImgUrl();
                    binding.setEntity(entity);
                    binding.executePendingBindings();
                }
            });
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v instanceof EditText) {
            if (!hasFocus) {
                ((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_button:
                submit();
                break;
            case R.id.cancel_button:
                NavHostFragment.findNavController(this).navigateUp();
                break;
            case R.id.add_img_button:
                addImage();
                break;
        }
    }

    private void submit() {
        String sub = String.valueOf(etSubject.getText()).trim();

        if (sub.isEmpty() || sub == null) {
            etSubject.setError("제목은 필수 입니다.");
            etSubject.requestFocus();
            return;
        }

        String pattern = requireActivity().getString(R.string.date_time_pattern);
        String dateTime = new SimpleDateFormat(pattern, Locale.KOREA).format(new Date());

        for (int i = 0; i < layout.getChildCount(); i++) {
            CheckableIV iv = null;

            if (layout.getChildAt(i) instanceof CheckableIV)
                iv = (CheckableIV) layout.getChildAt(i);

            if (iv != null && !iv.isChecked()) {
                imgList.remove(iv.getTag());
            }
        }

        MemoEntity entity = binding.getEntity();

        entity.setSubject(sub);
        entity.setContent(String.valueOf(etContent.getText()));
        entity.setDateTime(dateTime);
        if (imgList.size() > 0) entity.setImgUrl(imgList);

        viewModel.updateItem(entity);

        NavHostFragment.findNavController(this).navigateUp();
    }

    private void addImage() {
        String[] items;
        if (hasCamera) {
            items = new String[]{
                    "겔러리에서 선택",
                    "외부 링크 추가",
                    "카메라 촬영"
            };

        } else {
            items = new String[]{
                    "겔러리에서 선택",
                    "외부 링크 추가",
            };
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("이미지 추가 방식")
                .setItems(items, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            fromGallery();
                            break;
                        case 1:
                            fromLink();
                            break;
                        case 2:
                            fromCamera();
                            break;
                        default:
                            dialog.dismiss();
                    }
                }).show();
    }

    private void fromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        if (galleryIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            this.startActivityForResult(galleryIntent, REQUEST_MODIFY_IMG_GALLERY);
        }
    }

    private void fromLink() {
        View madView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_input_url, (ViewGroup) requireView(), false);
        TextInputEditText editText = madView.findViewById(R.id.etUrl);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("링크 추가")
                .setMessage("URL 주소를 첨부해 주세요")
                .setView(madView)
                .setNegativeButton("취소", (dialog, which) -> dialog.cancel())
                .setPositiveButton("추가", (dialog, which) -> {
                    String url = String.valueOf(editText.getText());

                    if (URLUtil.isValidUrl(url)) imgList.add(url);
                    else
                        Toast.makeText(requireContext(), "잘 못 된 링크 주소 입니다.", Toast.LENGTH_SHORT).show();
                }).show();
    }

    private void fromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            this.startActivityForResult(cameraIntent, REQUEST_MODIFY_IMG_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_MODIFY_IMG_CAMERA) {
            if (resultCode == RESULT_OK && data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                long now = System.currentTimeMillis();

                ContentValues cv = new ContentValues();
                cv.put(MediaStore.Images.Media.TITLE, now);
                cv.put(MediaStore.Images.Media.DISPLAY_NAME, now);
                cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                cv.put(MediaStore.Images.Media.DATE_ADDED, now / 1000);
                cv.put(MediaStore.Images.Media.DATE_MODIFIED, now / 1000);
                cv.put(MediaStore.Images.Media.DATE_TAKEN, now);

                Uri cameraUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, requireContext().getContentResolver().openOutputStream(cameraUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                imgList.add(cameraUri.toString());
            }
        } else if (requestCode == REQUEST_MODIFY_IMG_GALLERY) {
            if (data != null) {
                imgList.add(data.getData().toString());
            }
        }

        setImageView();

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImageView() {
        if (layout.getChildCount() > 0) layout.removeAllViews();

        if (imgList.size() < 1) {
            binding.imgScrollContainer.setVisibility(View.GONE);
        } else {
            binding.imgScrollContainer.setVisibility(View.VISIBLE);

            for (String url : imgList) {
                CheckableIV iv = new CheckableIV(requireContext());

                Glide.with(requireContext()).load(url).circleCrop().error(R.mipmap.ic_launcher).into(iv);

                iv.setTag(url);
                iv.setChecked(true);
                iv.setOnLongClickListener(new IvLongClickListener(imgList));

                layout.addView(iv);
            }
        }
    }
}
