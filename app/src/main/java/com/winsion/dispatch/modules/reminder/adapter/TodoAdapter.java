package com.winsion.dispatch.modules.reminder.adapter;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.View;

import com.winsion.dispatch.R;
import com.winsion.dispatch.common.listener.ClickListener;
import com.winsion.dispatch.modules.reminder.entity.TodoEntity;
import com.winsion.dispatch.utils.ConvertUtils;
import com.winsion.dispatch.utils.constants.Formatter;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 1:24
 */

public class TodoAdapter extends CommonAdapter<TodoEntity> {
    private ClickListener<TodoEntity> deleteBtnClickListener;

    public TodoAdapter(Context context, List<TodoEntity> data) {
        super(context, R.layout.item_todo, data);
    }

    @Override
    protected void convert(ViewHolder viewHolder, TodoEntity todoEntity, int position) {
        if (position == mDatas.size() - 1) {
            viewHolder.setVisible(R.id.iv_bottom, true);
        } else {
            viewHolder.setVisible(R.id.iv_bottom, false);
        }
        String planData = ConvertUtils.formatDate(todoEntity.getPlanDate(), Formatter.DATE_FORMAT1);
        String[] split = planData.split(" ");
        String[] split1 = split[0].split("-");
        // 年
        viewHolder.setText(R.id.tv_year, split1[0] + getString(R.string.suffix_year));
        // 日期
        viewHolder.setText(R.id.tv_date, split1[1] + getString(R.string.suffix_month) + split1[2]);
        // 时间
        viewHolder.setText(R.id.tv_time, split[1].substring(0, 5));
        // 事项描述
        viewHolder.setText(R.id.tv_desc, todoEntity.getContent());
        viewHolder.setOnClickListener(R.id.iv_delete, (View v) -> {
            if (deleteBtnClickListener != null) {
                deleteBtnClickListener.onClick(mDatas.get(position));
            }
        });
        if (!todoEntity.getFinished() && todoEntity.getPlanDate() < System.currentTimeMillis()) {
            viewHolder.setVisible(R.id.iv_red_dot, true);
        } else {
            viewHolder.setVisible(R.id.iv_red_dot, false);
        }
    }

    public void setDeleteBtnClickListener(ClickListener<TodoEntity> listener) {
        this.deleteBtnClickListener = listener;
    }

    private String getString(@StringRes int strRes) {
        return mContext.getString(strRes);
    }
}
