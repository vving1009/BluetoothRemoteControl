package com.satcatche.btserver;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class SatcatcheKeyboardContainer extends LinearLayout implements OnClickListener {
    Context f4023a;
    int f4024b = 0;
    int f4025c = -1;
    View f4026d;
    private InputMethodService f4027e;

    public SatcatcheKeyboardContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f4023a = context;
        setFocusable(true);
    }

    static View m3696a(int i, LinearLayout linearLayout) {
        if (linearLayout == null || i > Integer.valueOf(linearLayout.getTag().toString()).intValue()) {
            return null;
        }
        View childAt = linearLayout.getChildAt(i);
        return childAt instanceof Button ? childAt : null;
    }

    private void m3697a(char c) {
        this.f4027e.sendKeyChar(c);
        m3699a();
    }

    final LinearLayout m3698a(int i) {
        if (i > getChildCount()) {
            return null;
        }
        View childAt = getChildAt(i);
        return childAt instanceof LinearLayout ? (LinearLayout) childAt : null;
    }

    final void m3699a() {
        if (this.f4026d != null) {
            this.f4026d.requestFocus();
            // this.f4026d.setBackgroundResource(R.drawable.keybord_check);
            ((Button) this.f4026d).setTextColor(-1);
        }
    }

    public void onClick(View view) {
        try {
            int intValue = Integer.valueOf(view.getTag().toString()).intValue();
            String charSequence = ((Button) view).getText().toString();
            if (intValue == 17) {
                m3697a('*');
            } else if (intValue == 76) {
                m3697a('/');
            } else if (intValue == 62) {
                m3697a(' ');
            } else if (intValue == 67) {
                ((PinyinIME) this.f4027e).deleteText(1);
                m3699a();
            } else if (intValue == 66) {
                m3697a('\n');
            } else if (!TextUtils.isEmpty(charSequence) && !TextUtils.isEmpty(charSequence)) {
                ((PinyinIME) this.f4027e).commitText(charSequence);
                m3699a();
            }
        } catch (Exception e) {
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) childAt;
                int childCount2 = linearLayout.getChildCount();
                for (int i4 = 0; i4 < childCount2; i4++) {
                    View childAt2 = linearLayout.getChildAt(i4);
                    if (childAt2 != null) {
                        childAt2.setOnClickListener(this);
                    }
                }
            }
        }
    }

    public void setService(InputMethodService inputMethodService) {
        this.f4027e = inputMethodService;
    }
}
