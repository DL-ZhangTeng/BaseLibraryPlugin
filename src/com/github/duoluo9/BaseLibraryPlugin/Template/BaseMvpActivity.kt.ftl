<#if !isFirstActivity>
package ${packageName}.activity

import android.os.Bundle

import ${packageName}.base.BaseMvpActivity
import ${packageName}.mvp.model.imodel.${imodel}
import ${packageName}.mvp.presenter.ipresenter.${ipresenter}
import ${packageName}.mvp.presenter.${presenter}
import ${packageName}.mvp.view.${view}
import ${packageName}.R

class ${activityClass} : BaseMvpActivity<${view}, ${imodel}, ${ipresenter}>() , ${view} {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.${layoutName})
    }

    /**
    *自动调用加载中动画可使用代理模式
    *return Proxy.newProxyInstance(${presenter}::class.java.classLoader, arrayOf(${ipresenter}::class.java), LoadingPresenterHandler(${presenter}())) as ${ipresenter}
    */
	override fun createPresenter():${ipresenter}? {
        return ${presenter}()
    }
	
    override fun initData() {
        
    }
}
<#else>
package ${packageName}.base;

import ${packageName}.mvp.base.IModel
import ${packageName}.mvp.base.IPresenter
import ${packageName}.mvp.base.IView


/**
 * 使用Mvp模式Activity基类
 * Created by swing on 2017/11/23.
 */

abstract class BaseMvpActivity<V : IView, M : IModel, P : IPresenter<V, M>> : BaseActivity() {
	protected var mPresenter: P? = null

    override fun initView() {
        mPresenter = createPresenter()
        mPresenter?.attachView(this as V)
        mPresenter?.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
        mPresenter?.onDestroy()
        mPresenter = null
    }

    /**
     * 子类提供实现
     * 创建对应页面的presenter
     */
    abstract fun createPresenter(): P?
}

</#if>
