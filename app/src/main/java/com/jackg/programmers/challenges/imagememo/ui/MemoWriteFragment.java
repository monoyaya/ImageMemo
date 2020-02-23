package com.jackg.programmers.challenges.imagememo.ui;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.jackg.programmers.challenges.imagememo.BuildConfig;
import com.jackg.programmers.challenges.imagememo.R;
import com.jackg.programmers.challenges.imagememo.data.MemoEntity;
import com.jackg.programmers.challenges.imagememo.util.IvLongClickListener;
import com.jackg.programmers.challenges.imagememo.viewmodel.MemoViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MemoWriteFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_IMG_CAMERA = 84;
    private static final int REQUEST_IMG_GALLERY = 94;

    private TextInputEditText etSubject, etContent;
    private MaterialButton bntImg, bntBack, bntWrite;
    private LinearLayout layout;

    private ArrayList<String> imgList;
    private MemoViewModel viewModel;

    private boolean hasCamera = false;

    private Uri cameraUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_memo_write, container, false);

        imgList = new ArrayList<>();

        layout = root.findViewById(R.id.ivContainer);

        etSubject = root.findViewById(R.id.sub_edit_txt);
        etContent = root.findViewById(R.id.con_edit_txt);

        bntBack = root.findViewById(R.id.cancel_button);
        bntWrite = root.findViewById(R.id.submit_button);
        bntImg = root.findViewById(R.id.add_img_button);

        bntBack.setOnClickListener(this);
        bntWrite.setOnClickListener(this);
        bntImg.setOnClickListener(this);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        hasCamera = requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);

        viewModel = new ViewModelProvider(requireActivity()).get(MemoViewModel.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_button:
                NavHostFragment.findNavController(this).navigateUp();
                break;
            case R.id.submit_button:
                submit();
                break;
            case R.id.add_img_button:
                addImg();
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

        MemoEntity entity = new MemoEntity();

        entity.setSubject(sub);
        entity.setContent(String.valueOf(etContent.getText()));
        entity.setDateTime(dateTime);
        entity.setImgUrl(imgList);

        long memoId = viewModel.insertItem(entity);

        if (memoId != 0) {
           MemoWriteFragmentDirections.ActionMemoWriteFragmentToMemoDetailFragment action
                   = MemoWriteFragmentDirections.actionMemoWriteFragmentToMemoDetailFragment();
           action.setMemoId(memoId);

           NavHostFragment.findNavController(this).navigate(action);
        } else Toast.makeText(requireContext(), R.string.memo_write_fail_msg, Toast.LENGTH_SHORT).show();
    }

    private void addImg() {
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
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                                    default: dialog.dismiss();
                                }
                            }
                        }).show();
    }

    private void fromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        if (galleryIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            this.startActivityForResult(galleryIntent, REQUEST_IMG_GALLERY);
        }
    }

    private void fromLink() {
        View madView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_input_url, (ViewGroup) requireView(), false);
        TextInputEditText editText = madView.findViewById(R.id.etUrl);

        new MaterialAlertDialogBuilder(requireContext())
                                            .setTitle("링크 추가")
                                            .setMessage("URL 주소를 첨부해 주세요")
                                            .setView(madView)
                                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            })
                                            .setPositiveButton("추가", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String url = String.valueOf(editText.getText());

                                                    if (URLUtil.isValidUrl(url))imgList.add(url);
                                                    else Toast.makeText(requireContext(), "잘 못 된 링크 주소 입니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            }).show();
    }

    private void fromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {

            String pre = String.valueOf(System.currentTimeMillis());

            File imgFile = null;
            try {
                imgFile = File.createTempFile(pre, ".jpg",
                                        requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imgFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    cameraUri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", imgFile);
                } else cameraUri = Uri.fromFile(imgFile);

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    cameraIntent.setClipData(ClipData.newRawUri("", cameraUri));
                    cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                this.startActivityForResult(cameraIntent, REQUEST_IMG_CAMERA);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMG_CAMERA) {
            if (data != null) {
                if (Build.VERSION.SDK_INT < 29) {
                    Intent imgSendIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    imgSendIntent.setData(cameraUri);
                    requireActivity().sendBroadcast(imgSendIntent);
                }

                imgList.add(cameraUri.toString());
            }
        } else if (requestCode == REQUEST_IMG_GALLERY) {
            if (data != null) {
                cameraUri = data.getData();
                imgList.add(cameraUri.toString());
            }
        }

        if (cameraUri != null) setImageView();

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImageView() {
        if (layout.getChildCount() > 0) layout.removeAllViews();

        if (imgList.size() < 1) {
            ((HorizontalScrollView)layout.getParent()).setVisibility(View.GONE);
            return;
        } else {
            ((HorizontalScrollView)layout.getParent()).setVisibility(View.VISIBLE);
        }

        float density = requireContext().getResources().getDisplayMetrics().density;
        int dp10 = Math.round(10 * density);

        for (String url : imgList) {
            ImageView iv = new ImageView(requireContext());

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(dp10 * 6, dp10 * 6);
            params.setMargins(dp10, dp10, dp10, dp10);

            iv.setLayoutParams(params);
            iv.setTag(url);
            iv.setOnLongClickListener(new IvLongClickListener(imgList));

            layout.addView(iv);

            Glide.with(requireContext()).load(url).circleCrop().into(iv);
        }

        cameraUri = null;
    }
}
