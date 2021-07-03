package ${packageName}.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zhangteng.base.base.BaseMvpFragment
import ${packageName}.mvp.model.imodel.I${pageName}Model
import ${packageName}.mvp.presenter.ipresenter.I${pageName}Presenter
import ${packageName}.mvp.presenter.${pageName}Presenter
import ${packageName}.mvp.view.I${pageName}View
import ${packageName}.R

class ${pageName}Fragment : BaseMvpFragment<I${pageName}View, I${pageName}Model, I${pageName}Presenter>() , I${pageName}View {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.${fragmentLayoutName}, container, false)
    }

    /**
    *return Proxy.newProxyInstance(${pageName}Presenter::class.java.classLoader, arrayOf(I${pageName}Presenter::class.java), LoadingPresenterHandler(${pageName}Presenter())) as I${pageName}Presenter
    */
	override fun createPresenter():I${pageName}Presenter? {
        return ${pageName}Presenter()
    }

	override fun initView(view: View, savedInstanceState: Bundle?) {
	    super.initView(view, savedInstanceState)

	}

    override fun initData(savedInstanceState: Bundle?) {
        
    }
}