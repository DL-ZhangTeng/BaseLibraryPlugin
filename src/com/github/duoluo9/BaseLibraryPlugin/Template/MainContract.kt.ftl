package ${packageName}.mvp.contract

import com.zhangteng.base.mvp.base.IModel
import com.zhangteng.base.mvp.base.IPresenter
import com.zhangteng.base.mvp.base.IView

interface ${pageName}Contract {
    interface I${pageName}View : IView
    interface I${pageName}Presenter : IPresenter<I${pageName}View, I${pageName}Model>
    interface I${pageName}Model : IModel
}