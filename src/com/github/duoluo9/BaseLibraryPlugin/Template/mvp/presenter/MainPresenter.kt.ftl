package ${packageName}.mvp.presenter

import ${packageName}.mvp.base.BasePresenter
import ${packageName}.mvp.model.imodel.${imodel}
import ${packageName}.mvp.presenter.ipresenter.${ipresenter}
import ${packageName}.mvp.view.${view}

class ${presenter} : BasePresenter<${view},${imodel}>() , ${ipresenter} {

}