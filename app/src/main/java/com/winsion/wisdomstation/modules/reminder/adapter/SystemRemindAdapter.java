package com.winsion.wisdomstation.modules.reminder.adapter;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.common.listener.ClickListener;
import com.winsion.wisdomstation.modules.operation.constants.TaskType;
import com.winsion.wisdomstation.modules.reminder.constants.ReadStatus;
import com.winsion.wisdomstation.modules.reminder.entity.RemindEntity;
import com.winsion.wisdomstation.utils.ToastUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 7:28
 */

public class SystemRemindAdapter extends CommonAdapter<RemindEntity> {

    public interface OnSelectChangeListener {
        void onSelectChange(int selectSize);
    }

    // 当前是否是多选删除状态
    private boolean isMultipleDeleteStatus;

    private ClickListener<RemindEntity> mListener;
    private OnSelectChangeListener onSelectChangeListener;
    private List<RemindEntity> selectData = new ArrayList<>();

    public SystemRemindAdapter(Context context, List<RemindEntity> data) {
        super(context, R.layout.item_system_remind, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, RemindEntity remindEntity, int position) {
        int taskType = remindEntity.getTaskType();
        int typeTextResId = R.string.homework_remind;
        switch (taskType) {
            case TaskType.COMMAND:
                typeTextResId = R.string.command_remind;
                break;
            case TaskType.COOPERATE:
                typeTextResId = R.string.cooperation_remind;
                break;
            case TaskType.GRID:
                typeTextResId = R.string.grid_remind;
                break;
            case TaskType.PLAN:
                typeTextResId = R.string.plan_remind;
                break;
        }
        viewHolder.setText(R.id.tv_remind_type, mContext.getString(typeTextResId));
        viewHolder.setText(R.id.tv_remind_content, remindEntity.getVoicecontent());
        String sendTime = remindEntity.getSendtime();
        viewHolder.setText(R.id.tv_remind_date, sendTime.substring(0, sendTime.length() - 2));

        if (remindEntity.getReaded() == 1) {
            viewHolder.setVisible(R.id.iv_red_dot, false);
            viewHolder.setTextColorRes(R.id.tv_remind_type, R.color.gray2);
            viewHolder.setTextColorRes(R.id.tv_remind_content, R.color.gray2);
        } else {
            viewHolder.setVisible(R.id.iv_red_dot, true);
            viewHolder.setTextColorRes(R.id.tv_remind_type, R.color.blue1);
            viewHolder.setTextColorRes(R.id.tv_remind_content, R.color.white1);
        }

        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.iv_bottom, true);
        } else {
            viewHolder.setVisible(R.id.iv_bottom, false);
        }

        if (isMultipleDeleteStatus) {
            viewHolder.setVisible(R.id.iv_delete, false);
            viewHolder.setVisible(R.id.iv_select, true);
            if (selectData.contains(remindEntity)) {
                viewHolder.setImageResource(R.id.iv_select, R.drawable.ic_check_box);
            } else {
                viewHolder.setImageResource(R.id.iv_select, R.drawable.ic_check_box_outline);
            }
        } else {
            viewHolder.setVisible(R.id.iv_delete, true);
            viewHolder.setVisible(R.id.iv_select, false);
        }

        viewHolder.setOnClickListener(R.id.iv_delete, v ->
                new AlertDialog.Builder(mContext)
                        .setMessage(getString(R.string.sure_to_delete_it))
                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                            if (mListener != null) {
                                mListener.onClick(remindEntity);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel())
                        .create()
                        .show());

        viewHolder.setOnClickListener(R.id.iv_select, v -> selectOneItem(viewHolder, position));
    }

    public void selectOneItem(ViewHolder viewHolder, int position) {
        RemindEntity remindEntity = mDatas.get(position);
        if (remindEntity.getReaded() == ReadStatus.UNREAD) {
            ToastUtils.showToast(mContext, R.string.only_read_remind_can_be_selected);
        } else {
            if (selectData.contains(remindEntity)) {
                selectData.remove(remindEntity);
                viewHolder.setImageResource(R.id.iv_select, R.drawable.ic_check_box_outline);
            } else {
                selectData.add(remindEntity);
                viewHolder.setImageResource(R.id.iv_select, R.drawable.ic_check_box);
            }
            if (onSelectChangeListener != null) {
                onSelectChangeListener.onSelectChange(selectData.size());
            }
        }
    }

    public void selectAll(boolean isSelectAll) {
        selectData.clear();
        if (isSelectAll) {
            for (RemindEntity mData : mDatas) {
                if (mData.getReaded() == ReadStatus.READ) {
                    selectData.add(mData);
                }
            }
        }
        notifyDataSetChanged();
        if (onSelectChangeListener != null) {
            onSelectChangeListener.onSelectChange(selectData.size());
        }
    }

    public void changeStatus(boolean isMultipleDeleteStatus) {
        this.isMultipleDeleteStatus = isMultipleDeleteStatus;
        if (!isMultipleDeleteStatus) {
            selectData.clear();
        }
        notifyDataSetChanged();
    }

    public List<RemindEntity> getSelectData() {
        return selectData;
    }

    public void setOnSelectChangeListener(OnSelectChangeListener listener) {
        this.onSelectChangeListener = listener;
    }

    public void setOnDeleteBtnClickListener(ClickListener<RemindEntity> listener) {
        this.mListener = listener;
    }

    private String getString(@StringRes int strRes) {
        return mContext.getString(strRes);
    }
}
