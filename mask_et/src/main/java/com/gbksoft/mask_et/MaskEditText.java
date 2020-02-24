package com.gbksoft.mask_et;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class MaskEditText extends AppCompatEditText implements TextWatcher {

    private static final char DEFAULT_CHAR_REPRESENTATION = '#';
    private String mask;
    private int[] maskArray;
    private int[] maskIndexArray;
    private char charRepresentation = DEFAULT_CHAR_REPRESENTATION;
    private boolean showMaskAsHint = true;

    private String etTextBefore;
    private String etText;
    private String addChar;
    private String delChar;
    private String textWithMask = "";
    private String textWithoutMask = "";
    private boolean isBefore = false;
    private boolean isAfter = false;
    private int newPosWithoutMask = -1;
    private int newPosWithMask = -1;

    public MaskEditText(Context context) {
        super(context);
        initView(null);
    }

    public MaskEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public MaskEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        addTextChangedListener(this);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaskEditText);

        mask = a.getString(R.styleable.MaskEditText_mask);
        showMaskAsHint = a.getBoolean(R.styleable.MaskEditText_show_mask_as_hint, true);

        String representation = a.getString(R.styleable.MaskEditText_char_representation);
        if (representation != null) {
            charRepresentation = representation.charAt(0);
        }

        if (showMaskAsHint) {
            setHint(mask);
        }

        init();

        a.recycle();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        etTextBefore = s.toString();
        isBefore = true;
        isAfter = false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!isBefore || isAfter) {
            return;
        }

        isBefore = false;
        etText = s.toString();
        if (etTextBefore == null) {
            etTextBefore = "";
        }
        addChar = null;
        delChar = null;
        if (etTextBefore.length() < etText.length()) {
            addChar = etText.substring(start, start + count);
        } else if (etTextBefore.length() > etText.length()) {
            delChar = etTextBefore.substring(start, start + before);
        }

        newPosWithoutMask = -1;
        newPosWithMask = -1;

        if (maskIndexArray == null) {
            return;
        }

        if (!TextUtils.isEmpty(addChar)) {
            for (int i = 0; i < maskIndexArray.length; i++) {
                if (maskIndexArray[i] >= start) {
                    newPosWithoutMask = i;
                    if (i + 1 < maskIndexArray.length) {
                        newPosWithMask = maskIndexArray[i + 1];
                    } else {
                        newPosWithMask = maskArray.length - 1;
                    }
                    break;
                }
            }
            if (newPosWithoutMask == -1) {
                return;
            }

            String str = textWithoutMask.substring(0, newPosWithoutMask) + addChar + textWithoutMask.substring(newPosWithoutMask);
            if (str.length() > maskIndexArray.length) {
                textWithoutMask = str.substring(0, maskIndexArray.length);
            } else {
                textWithoutMask = str;
            }
            int newPosWithMaskTemp;
            if (textWithoutMask.length() < maskIndexArray.length) {
                newPosWithMaskTemp = maskIndexArray[textWithoutMask.length()];
            } else {
                newPosWithMaskTemp = maskArray.length;
            }

            StringBuilder text = new StringBuilder();
            for (int i = 0; i < newPosWithMaskTemp; i++) {
                if (maskArray[i] == -1) {
                    text.append(mask.substring(i, i + 1));
                } else {
                    text.append(textWithoutMask.substring(maskArray[i], maskArray[i] + 1));
                }
            }
            textWithMask = text.toString();
        }

        if (!TextUtils.isEmpty(delChar)) {
            for (int i = 0; i < maskIndexArray.length; i++) {
                if (maskIndexArray[i] == start) {
                    newPosWithoutMask = i;
                    if (i - 1 < 0) {
                        newPosWithMask = 0;
                    } else {
                        newPosWithMask = maskIndexArray[i];
                    }
                    break;
                } else if (i - 1 >= 0 && (maskIndexArray[i - 1] < start && maskIndexArray[i] > start)) {
                    newPosWithoutMask = i - 1;
                    newPosWithMask = maskIndexArray[i - 1];
                }
            }
            if (newPosWithoutMask == -1) {
                return;
            }

            String str = textWithoutMask.substring(0, newPosWithoutMask) + textWithoutMask.substring(newPosWithoutMask + 1);
            if (str.length() > maskIndexArray.length) {
                return;
            }
            textWithoutMask = str;
            int newPosWithMaskTemp;
            if (textWithoutMask.length() < maskIndexArray.length) {
                newPosWithMaskTemp = maskIndexArray[textWithoutMask.length()];
            } else {
                newPosWithMaskTemp = maskArray.length;
            }

            StringBuilder text = new StringBuilder();
            for (int i = 0; i < newPosWithMaskTemp; i++) {
                if (maskArray[i] == -1) {
                    text.append(mask.substring(i, i + 1));
                } else {
                    text.append(textWithoutMask.substring(maskArray[i], maskArray[i] + 1));
                }
            }
            textWithMask = text.toString();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        isAfter = true;
        removeTextChangedListener(this);

        if (showMaskAsHint) {
            StringBuilder sb = new StringBuilder(textWithMask);
            sb.append(mask.substring(textWithMask.length()));

            int start = textWithoutMask.length() == 0 ? 0 : textWithMask.length();
            int end = mask.length();
            SpannableStringBuilder ssb = new SpannableStringBuilder(sb);
            ssb.setSpan(new ForegroundColorSpan(getCurrentHintTextColor()), start, end, 0);
            setText(ssb);

        } else {
            setText(textWithMask);
        }

        addTextChangedListener(this);
        if (newPosWithMask != -1) {
            setSelection(newPosWithMask);
        } else {
            if (maskIndexArray != null && maskIndexArray.length != 0) {
                setSelection(maskIndexArray[maskIndexArray.length - 1] + 1);
            } else {
                setSelection(0);
            }
        }
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (textWithMask != null && selStart > textWithMask.length()) {
            setSelection(textWithMask.length());
        } else if (maskIndexArray != null && maskIndexArray.length != 0 && selStart > maskIndexArray[maskIndexArray.length - 1]) {
            setSelection(maskIndexArray[maskIndexArray.length - 1] + 1);
        }
        super.onSelectionChanged(selStart, selEnd);
    }

    private void init() {
        clear();
        setHint(showMaskAsHint ? mask : "");
        generateMaskArray();
        generateMaskIndexArray();
        invalidate();
    }

    private void generateMaskArray() {
        maskArray = new int[mask.length()];
        int index = 0;

        for (int i = index; i < maskArray.length; i++) {
            if (mask.charAt(i) == charRepresentation) {
                maskArray[i] = index;
                index++;
            } else {
                maskArray[i] = -1;
            }
        }
    }

    private void generateMaskIndexArray() {
        int[] tempArray = new int[maskArray.length];
        int index = 0;

        for (int i = index; i < maskArray.length; i++) {
            if (maskArray[i] != -1) {
                tempArray[index] = i;
                index++;
            }
        }
        maskIndexArray = new int[index];
        System.arraycopy(tempArray, 0, maskIndexArray, 0, index);
    }


    public void setMaskAndCharRepresentation(String mask, char charRepresentation) {
        this.mask = mask;
        this.charRepresentation = charRepresentation;
        init();
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
        init();
    }

    public char getCharRepresentation() {
        return charRepresentation;
    }

    public void setCharRepresentation(char charRepresentation) {
        this.charRepresentation = charRepresentation;
        init();
    }

    public void setDefaultCharRepresentation() {
        this.charRepresentation = DEFAULT_CHAR_REPRESENTATION;
        init();
    }

    public boolean isShowMaskAsHint() {
        return showMaskAsHint;
    }

    public void setShowMaskAsHint(boolean showMaskAsHint) {
        this.showMaskAsHint = showMaskAsHint;
    }

    public void clear() {
        maskArray = null;
        maskIndexArray = null;
        showMaskAsHint = true;
        etTextBefore = "";
        etText = "";
        textWithMask = "";
        textWithoutMask = "";
        isBefore = false;
        isAfter = false;
        newPosWithoutMask = -1;
        newPosWithMask = -1;
        setText("");
    }
}
