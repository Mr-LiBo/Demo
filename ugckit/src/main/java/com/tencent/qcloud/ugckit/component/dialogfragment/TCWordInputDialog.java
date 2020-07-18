package com.tencent.qcloud.ugckit.component.dialogfragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;


import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.tencent.qcloud.ugckit.R;


import java.lang.ref.WeakReference;

/**
 * 字幕输入框
 */
public class TCWordInputDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "TCWordInputDialog";
    private TextView mTvSure, mTvCancel;
    private EditText mEtContent;
    @Nullable
    private String mDefaultText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setDialogStyle();//去掉标题栏
        return inflater.inflate(R.layout.fragment_input_word, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvCancel = (TextView) view.findViewById(R.id.word_tv_cancel);
        mTvCancel.setOnClickListener(this);
        mTvSure = (TextView) view.findViewById(R.id.word_tv_done);
        mTvSure.setOnClickListener(this);
        mEtContent = (EditText) view.findViewById(R.id.word_et_content);
        if (!TextUtils.isEmpty(mDefaultText)) {
            mEtContent.setText(mDefaultText);
        }
    }

    @Override
    public void onClick(@NonNull View v) {
        int i = v.getId();
        if (i == R.id.word_tv_cancel) {
            onClickCancel();

        } else if (i == R.id.word_tv_done) {
            String text = mEtContent.getText().toString();
            if (TextUtils.isEmpty(text)) {

                ToastUtil.toastShortMessage(getResources().getString(R.string.tc_word_input_dialog_please_enter_subtitles));
                return;
            }
            onClickSure();

        }
    }

    private void onClickCancel() {
        mEtContent.setText("");
        dismissDialog();
        if (mWefCallback.get() != null) {
            mWefCallback.get().onInputCancel();
        }
    }

    private void onClickSure() {
        String text = mEtContent.getText().toString();
        dismissDialog();
        if (mWefCallback.get() != null) {
            mWefCallback.get().onInputSure(text);
        }
    }


    private void dismissDialog() {
        try {
            dismiss();
        } catch (Exception e) {
            if (getFragmentManager() != null && isAdded()) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.remove(this);
                transaction.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null)
                window.setLayout((int) (ScreenUtils.getScreenWidth(dialog.getContext()) * 0.9),//设置宽度最小为 90%
                        WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private void setDialogStyle() {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getDialog().getWindow() != null)
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private WeakReference<OnWordInputCallback> mWefCallback;

    public void setOnWordInputCallback(OnWordInputCallback callback) {
        mWefCallback = new WeakReference<OnWordInputCallback>(callback);
    }

    public void setDefaultText(@Nullable final String defaultText) {
        Log.i(TAG, "setDefaultText: defaultText  = " + defaultText);
        if (defaultText != null) {
            mDefaultText = defaultText;
            if (mEtContent != null) {
                mEtContent.post(new Runnable() {
                    @Override
                    public void run() {
                        mEtContent.setText(defaultText);
                    }
                });
            }
        }
    }

    public interface OnWordInputCallback {
        void onInputSure(String text);

        void onInputCancel();
    }
}
