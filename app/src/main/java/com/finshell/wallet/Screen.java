package com.finshell.wallet;

import android.app.Activity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

final class Screen {
    private Screen() {}
    static LinearLayout create(Activity activity) {
        ScrollView scroll = new ScrollView(activity);
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (24 * activity.getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);
        scroll.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });
        scroll.addView(layout);
        activity.setContentView(scroll);
        scroll.requestApplyInsets();
        return layout;
    }
    static TextView text(Activity activity, LinearLayout layout, CharSequence text, int size) {
        TextView view = new TextView(activity);
        view.setText(text);
        view.setTextSize(size);
        view.setPadding(0, 12, 0, 12);
        layout.addView(view);
        return view;
    }
    static void button(Activity activity, LinearLayout layout, int label, View.OnClickListener listener) {
        Button button = new Button(activity);
        button.setText(label);
        button.setOnClickListener(listener);
        layout.addView(button);
    }
}

