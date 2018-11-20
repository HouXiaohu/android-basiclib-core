package com.hxh.component.basicore.component.mvp.delegate;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hxh.component.basicore.component.mvp.persenter.BasePresenter;
import com.hxh.component.basicore.component.mvp.persenter.IPresenter;
import com.hxh.component.basicore.component.mvp.view.IView;
import com.hxh.component.basicore.util.AutoUtils;

/**
 * 应该只专注于Fragment，如ToolBar应该交由ToolBarDelegate去做
 */
public class FragmentDelegate<P extends IPresenter> {
    public FragmentDelegate(IView<P> fragment) {
        this.fragment = fragment;
    }

    private IView<P> fragment;

    protected P p; //当前P
    private SparseArray<View> mViews; //View的缓存类
    private Context context;
    protected View rootView;//代表当前VIew


    private boolean isLazyLoad;
    private Bundle savedInstanceState;
    private boolean isFirstVisible = true;


    //region 生命周期
    public View onCrateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutid = (fragment).getLayoutId();
        if (layoutid <= 0) {
            throw new IllegalStateException("please set LayoutId");
        } else {
            mViews = new SparseArray<>();
            if (null == rootView) {
                rootView = inflater.inflate(layoutid, container, false);
                AutoUtils.auto(rootView);
            } else {
                //当重复加载时候，就从跟布局中删除这个布局
                ViewGroup vp = (ViewGroup) rootView.getParent();
                if (null != vp) {
                    vp.removeView(rootView);
                }
            }
        }
        this.savedInstanceState = savedInstanceState;
        return rootView;
    }

    /**
     * 正常的Fragment需要实现并调用这个方法
     *
     * @param savedInstanceState
     */
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        (fragment).initData(savedInstanceState);
    }

    public void onAttach(Context context) {
        this.context = context;
    }

    public void onVisible() {
        if (null != ((BasePresenter) getP()))
            ((BasePresenter) getP()).newCompositeSubscription();
    }


    public void onInVisible() {
        if (null != ((BasePresenter) getP()))
            ((BasePresenter) getP()).unSubscription();
    }


    public void onDetach() {
        rootView = null;
        if (null != getP()) getP().DetachView();
        p = null;
        context = null;
        if (null != mViews) {
            mViews.clear();
            mViews = null;
        }
    }

    /**
     * onDestory 什么也不做，按照声明周期来说，onDetach()是Fragment的最后一步，但是如果直接在onDestory()
     * 中释放了view的话，那么就会造成，处于这俩之间的事件，调用getView(),getP()崩溃的情况
     */
    public void onDestroy() {

    }

    /**
     * 懒加载的需要实现并调用这个方法
     */
    public void onSupportVisible() {

        if (isFirstVisible && isLazyLoad) {
            ((IView) fragment).initData(savedInstanceState);
            isFirstVisible = false;
        }
    }

    //endregion


    public P getP() {
        if (null == p) {
            p = (fragment).newP();
            if (null != p) {
                p.AttachView(fragment);
            }
        }

        return p;
    }


    public <V extends View> V findViewBy(int resId) {

        View view = mViews.get(resId);
        if (null == view) {
            view = rootView.findViewById(resId);
            mViews.put(resId, view);
        }
        return (V) view;
    }


    /**
     * 如果你是懒加载的fragment，必须显示指定
     *
     * @param islazy
     */
    public void setEnableLazyLoad(boolean islazy) {
        this.isLazyLoad = islazy;
    }


    public Context getContext() {
        return getmContext();
    }

    public Context getmContext() {
        return context;
    }

}
