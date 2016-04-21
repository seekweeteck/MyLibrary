package mmsr.fragment;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class CreateView {
	private ViewGroup view;
	private Context context;
	private String input;
	private int size;
	private String language;
	
	public CreateView(String lang, String input,  int size, ViewGroup tl, Context context){
		this.input = input;
		this.size = size;
		this.view = tl;
		this.context = context;
		this.language = lang;
	}
	
	public void setView() {
		String del = " ";
		String[] output = input.split(del);
		
		LinearLayout tr = new LinearLayout(context);
		tr.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		tr.setId(100);
		tr.setTag(this.language);

		// Identify width of layout
		int width = view.getWidth();
		int currentWidth = 0;

		//((ViewGroup) view).removeAllViews();

		for (int i = 0; i < output.length; i++) {
			final Button chk = new Button(context);
			
			chk.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
			chk.setGravity(Gravity.LEFT);
			chk.setText(output[i]);
			chk.setOnClickListener((OnClickListener) context);
			chk.setBackgroundColor(Color.WHITE);
			chk.setTextColor(Color.BLACK);
			chk.setId(i+1);
			chk.setContentDescription(this.language + output[i]);
			tr.addView(chk);

			chk.measure(0, 0);
			currentWidth += chk.getMeasuredWidth();
			
			if (currentWidth > width) {
				tr.removeView(chk);
				currentWidth = 0;

				view.addView(tr);

				tr = new LinearLayout(context);
				tr.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

				tr.addView(chk);
				chk.measure(0, 0);
				currentWidth += chk.getMeasuredWidth();
			}
		}

		view.addView(tr);
	}
}
