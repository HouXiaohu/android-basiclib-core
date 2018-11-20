package com.hxh.component.basicore.component.mvp.delegate.interfaces;


import com.hxh.component.basicore.component.mvp.persenter.BasePresenter;

/**
 * Created by hxh on 2018/2/28.
 */

public interface IStateLayoutRelated<P extends BasePresenter> {

    void operaErrorLayout(boolean isHide);

    void operaLoadingLayout(boolean isHide);

    void operaNoNetWorkLayout(boolean isHide);

    void operaStateLayout(String tag, boolean isHide);

}