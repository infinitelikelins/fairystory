package com.bearya.robot.fairystory.station;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bearya.robot.fairystory.R;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * description :
 * author : yexifeng
 * email : ye_xi_feng@163.com
 * date : 2019/6/14 16:01
 */
public class RecyclerViewAdapter<T extends RecyclerViewHolder<D>,D> extends RecyclerView.Adapter<T> {
    private List<D> datas;
    private OnItemClickedListener<D> onItemClickedListener;

    public RecyclerViewAdapter(List<D> datas) {
        this.datas = datas;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        Class<T > entityClass  =  getViewClass();
        T view = null;
        try{
            view = entityClass.getConstructor(ViewGroup.class).newInstance(parent);
            return view;
        }catch (Exception e){}
        return null;
    }

    @Override
    public void onBindViewHolder(T holder, final int position) {
        if(onItemClickedListener!=null){
            if(holder.needOnItemClickedListener()){
                holder.setOnItemClickedListener(onItemClickedListener);
            }
            holder.itemView.setTag(datas.get(position));
            holder.itemView.setTag(R.id.item_click_flag,position);
            holder.itemView.setOnClickListener(onItemClickedListener);
        }
        holder.setData(datas.get(position),position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public Class<T> getViewClass(){
        return (Class< T >) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[ 0 ];
    }

    public void setOnItemClickedListener(OnItemClickedListener<D> onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }
}
