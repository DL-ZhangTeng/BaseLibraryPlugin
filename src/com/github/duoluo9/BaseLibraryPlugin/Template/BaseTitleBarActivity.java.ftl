package ${packageName}.activity;

import android.os.Bundle;

import com.zhangteng.base.base.TitlebarActivity;
import ${packageName}.R;

public class ${pageName}Activity extends TitlebarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.${activityLayoutName});
    }

    @Override
    protected void initView() {
		super.initView();
    }

    @Override
    protected void initData() {

    }
}