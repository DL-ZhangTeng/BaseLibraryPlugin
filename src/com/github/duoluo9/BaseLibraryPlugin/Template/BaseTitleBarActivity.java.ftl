<#if !isFirstActivity>
package ${packageName}.activity;

import android.os.Bundle;

import ${packageName}.base.TitlebarActivity;
import ${packageName}.R;

public class ${activityClass} extends TitlebarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.${layoutName});
    }

    @Override
    protected void initView() {
		super.initView();
    }

    @Override
    protected void initData() {

    }
}
<#else>
package ${packageName}.base;

import android.view.View;

import ${packageName}.widget.CommonTitleBar;

import ${packageName}.R;


/**
 * Created by swing on 2017/11/23.
 */

public abstract class TitlebarActivity extends BaseActivity {
    protected CommonTitleBar mTitlebar;

    @Override
    protected void initView() {
        mTitlebar = findViewById(R.id.titlebar);
        if (mTitlebar == null) {
            throw new IllegalStateException(
                    "The subclass of TitlebarActivity must contain a titlebar.");
        }
        mTitlebar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON || action == CommonTitleBar.ACTION_LEFT_TEXT) {
                    finish();
                }
            }
        });
    }
}

</#if>
