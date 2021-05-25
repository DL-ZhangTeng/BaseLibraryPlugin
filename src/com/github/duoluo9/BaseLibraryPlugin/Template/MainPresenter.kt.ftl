package ${packageName}.mvp.presenter

import com.zhangteng.base.mvp.base.BasePresenter
import ${packageName}.mvp.model.imodel.I${pageName}Model
import ${packageName}.mvp.presenter.ipresenter.I${pageName}Presenter
import ${packageName}.mvp.view.I${pageName}View

class ${pageName}Presenter : BasePresenter<I${pageName}View,I${pageName}Model>() , I${pageName}Presenter {

}