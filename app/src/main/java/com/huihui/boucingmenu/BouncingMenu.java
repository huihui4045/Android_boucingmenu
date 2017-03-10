package com.huihui.boucingmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.FrameLayout;

public class BouncingMenu {
	private ViewGroup mParentVG;
	private View rootView;
	private BouncingView bouncingView;
	private RecyclerView recyclerView;
	private MyRecyclerAdapter adapter;
	
	public BouncingMenu(View view, int resId, MyRecyclerAdapter adapter) {
		this.adapter = adapter;
		//找到@android:id/content---不断地去找父容器，知道找到
		mParentVG = findRootParent(view);
		//渲染自己的布局进来
		rootView = LayoutInflater.from(view.getContext()).inflate(resId, null, false);
		
		bouncingView = (BouncingView) rootView.findViewById(R.id.sv);
		recyclerView = (RecyclerView)rootView.findViewById(R.id.rv);
		recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
		bouncingView.setAnimationListener(new MyAnimationListener());
	}
	
	class MyAnimationListener implements BouncingView.AnimationListener{

		@Override
		public void showContent() {
			recyclerView.setVisibility(View.VISIBLE);
			recyclerView.setAdapter(adapter);
			recyclerView.scheduleLayoutAnimation();
		}
		
	}
	
	private ViewGroup findRootParent(View view) {
//		FrameLayout decorview = (FrameLayout) ((Activity)view.getContext()).getWindow().getDecorView();
//		decorview.findViewById(android.R.id.content);
		do{
			if(view instanceof FrameLayout){
				if(view.getId()==android.R.id.content){
					return (ViewGroup) view;
				}
			}
			if(view!=null){
				ViewParent parent = view.getParent();
				view = parent instanceof View?(View)parent:null;
			}
			
		}while(view!=null);
		return null;
	}

	public static BouncingMenu makeMenu(View view, int resId, MyRecyclerAdapter adapter){
		return new BouncingMenu(view,resId,adapter);
	}

	public BouncingMenu show(){
		//往@android:id/content里面addView(resId)
		if(rootView.getParent()!=null){
			mParentVG.removeView(rootView);
		}
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mParentVG.addView(rootView,lp );
		//开始动画
		bouncingView.show();
		return this;
	}
	
	public void dismiss(){
		ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, "translationY", 0,rootView.getHeight());
		animator.setDuration(600);
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				mParentVG.removeView(rootView);
				rootView = null;
			}
		});
		animator.start();
	}
	
}
