package ${packageName}.mvp.base

open class BaseLoadingPresenter<V : BaseLoadingView<*>, M : IModel> : BasePresenter<V, M>() {
    fun getBaseLoadingView(): V? {
        return if (isAttach()) {
            mView?.get()
        } else {
            null
        }
    }
}