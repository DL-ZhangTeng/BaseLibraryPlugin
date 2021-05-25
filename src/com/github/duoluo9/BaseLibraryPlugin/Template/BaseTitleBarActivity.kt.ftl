package ${packageName}.activity

import android.os.Bundle

import com.zhangteng.base.base.TitlebarActivity
import ${packageName}.R

class ${pageName}Activity : TitlebarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.${activityLayoutName});
    }

    override fun initView() {
		super.initView()
    }

    override fun initData() {

    }
}