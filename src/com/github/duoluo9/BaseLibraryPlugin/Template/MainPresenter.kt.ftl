package ${packageName}.mvp.presenter

import com.zhangteng.base.mvp.base.BasePresenter
import ${packageName}.mvp.${modelPath}.I${pageName}Model
import ${packageName}.mvp.${presenterPath}.I${pageName}Presenter
import ${packageName}.mvp.${viewPath}.I${pageName}View

class ${pageName}Presenter : BasePresenter<I${pageName}View,I${pageName}Model>() , I${pageName}Presenter {

}