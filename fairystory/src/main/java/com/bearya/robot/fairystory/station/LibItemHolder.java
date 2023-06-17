package com.bearya.robot.fairystory.station;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bearya.robot.base.util.ResourceUtil;
import com.bearya.robot.fairystory.R;
import com.bumptech.glide.Glide;


class LibItemHolder extends RecyclerViewHolder<LibItem> {
    ImageView iconView;
    ImageView soundView;
    TextView nameView;

    public LibItemHolder(ViewGroup parent) {
        super(parent, R.layout.lib_item_view);
        iconView = itemView.findViewById(R.id.iconView);
        soundView = itemView.findViewById(R.id.soundView);
        nameView = itemView.findViewById(R.id.nameView);
    }

    @Override
    public void setData(LibItem libItem) {
        Glide.with(iconView.getContext()).load(ResourceUtil.getMipmapId(libItem.image))
                .thumbnail(0.1f)
                .into(iconView);
        nameView.setText(libItem.name);
//        try {
//            LibSoundItem soundItem = (LibSoundItem) libItem;
//            soundView.setVisibility(View.VISIBLE);
//        }catch (Exception e){
//
//        }
    }
}
