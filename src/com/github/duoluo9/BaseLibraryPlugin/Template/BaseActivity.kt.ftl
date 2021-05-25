package ${packageName}.activity

import android.os.Bundle

import com.zhangteng.base.base.BaseActivity
import ${packageName}.R

class ${pageName}Activity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.${activityLayoutName})
    }

    override fun initView() {

    }

    override fun initData() {

    }
}