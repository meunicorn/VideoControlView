package com.meunicorn.videocontrolview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * @author YangYF
 *
 * @date 2016年6月4日
 * 
 * @note
 *
 * @params
 */
public class VerticalTextView extends TextView {

	public VerticalTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public VerticalTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		// TODO Auto-generated method stub
		if ("".equals(text) || text == null || text.length() == 0) {
			return;
		}
		int m = text.length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < m; i++) {
			CharSequence index = text.toString().subSequence(i, i + 1);
			sb.append(index + "\n");
		}
		super.setText(sb, type);
	}
}