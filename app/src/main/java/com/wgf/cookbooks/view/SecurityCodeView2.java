package com.wgf.cookbooks.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wgf.cookbooks.R;


/**
 * author WuGuofei on 2017/5/3.
 * e-mail：guofei_wu@163.com
 */

public class SecurityCodeView2 extends RelativeLayout {

    private EditText mEditText;
    private TextView[] mTextViews;
    private StringBuffer stringBuffer = new StringBuffer();
    private String inputContent;//输入内容
    private int count = 4;
    private IInputCompleteListener mListener;

    public SecurityCodeView2(Context context) {
        this(context, null);
    }

    public SecurityCodeView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SecurityCodeView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.security_code, this);

        mTextViews = new TextView[4];
        mTextViews[0] = (TextView) view.findViewById(R.id.item_code_tv1);
        mTextViews[1] = (TextView) view.findViewById(R.id.item_code_tv2);
        mTextViews[2] = (TextView) view.findViewById(R.id.item_code_tv3);
        mTextViews[3] = (TextView) view.findViewById(R.id.item_code_tv4);

        mEditText = (EditText) view.findViewById(R.id.item_edittext);

       mEditText.setCursorVisible(false);//将光标设置为不可见

        setListener();


    }

    public void setListener(IInputCompleteListener mListener) {
        this.mListener = mListener;
    }

    private void setListener() {
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //输入不为空
                if (!TextUtils.isEmpty(s)) {
                    if (stringBuffer.length() > 3) {//字符串长度大于3
                        mEditText.setText("");
                        return;
                    } else {
                        stringBuffer.append(s);//将输入加入到stringbuffer中
                        mEditText.setText("");
                        count = stringBuffer.length();//获取长度，删除的时候使用
                        inputContent = stringBuffer.toString();
                        if (stringBuffer.length() == 4) {
                            if (mListener != null) {
                                mListener.complete();
                            }
                        }
                    }
                    for (int i = 0; i < count; i++) {
                        mTextViews[i].setText(String.valueOf(inputContent.charAt(i)));
                        mTextViews[i].setBackgroundResource(R.drawable.bg_verify_press);
                    }
                }
            }
        });
        mEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN ) {
                    if (onKeyDelete())
                        return true;
                    return true;
                }
                return false;
            }
        });


    }

    //删除验证码
    private boolean onKeyDelete() {
        if (count == 0) {
            count = 4;
            return true;
        }
        if (stringBuffer.length() > 0) {
            stringBuffer.delete(count - 1, count);//删除最后一个
            count--;
            inputContent = stringBuffer.toString();
            mTextViews[stringBuffer.length()].setText("");
            mTextViews[stringBuffer.length()].setBackgroundResource(R.drawable.bg_verify);
            if (mListener != null) {
                mListener.deleteContent(true);
            }
        }
        return false;
    }


    public interface IInputCompleteListener {
        void complete();//输入完成

        void deleteContent(boolean isDelete);//删除输入
    }


    /**
     * 获取输入文本
     *
     * @return
     */
    public String getEditContent() {
        return inputContent;
    }
}
