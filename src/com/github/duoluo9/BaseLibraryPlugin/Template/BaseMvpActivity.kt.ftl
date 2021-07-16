package ${packageName}.activity

import android.os.Bundle

import com.zhangteng.base.base.BaseMvpActivity
import ${packageName}.mvp.${modelPath}.I${pageName}Model
import ${packageName}.mvp.${presenterPath}.I${pageName}Presenter
import ${packageName}.mvp.presenter.${pageName}Presenter
import ${packageName}.mvp.${viewPath}.I${pageName}View
import ${packageName}.R

class ${pageName}Activity : BaseMvpActivity<I${pageName}View, I${pageName}Model, I${pageName}Presenter>() , I${pageName}View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.${activityLayoutName})
    }

    /**
    *return Proxy.newProxyInstance(${pageName}Presenter::class.java.classLoader, arrayOf(I${pageName}Presenter::class.java), LoadingPresenterHandler(${pageName}Presenter())) as I${pageName}Presenter
    */
	override fun createPresenter():I${pageName}Presenter? {
        return ${pageName}Presenter()
    }
	
    override fun initData() {
        
    }
}