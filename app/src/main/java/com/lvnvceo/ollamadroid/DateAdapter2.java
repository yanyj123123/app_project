package com.lvnvceo.ollamadroid;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lvnvceo.ollamadroid.ollama.ChatItem2;
import com.lvnvceo.ollamadroid.ollama.DateItem;

import java.util.List;

public class DateAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_CHAT_ITEM = 1;

    private List<Object> items;

    public DateAdapter2(List<Object> items) {
        this.items = items;
    }
    // 返回日期或者chat
    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof DateItem ? VIEW_TYPE_DATE : VIEW_TYPE_CHAT_ITEM;
    }
    // 绑定数据
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false);
            return new DateViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
            return new ChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DateViewHolder) {
            DateItem dateItem = (DateItem) items.get(position);
            ((DateViewHolder) holder).dateTextView.setText(dateItem.getDate());
            Log.d("DateAdapter2", "Binding DateItem: " + dateItem.getDate());
        } else if (holder instanceof ChatViewHolder) {
            ChatItem2 chatItem = (ChatItem2) items.get(position);
            ((ChatViewHolder) holder).questionTextView.setText(chatItem.getQuestion());
            ((ChatViewHolder) holder).answerTextView.setText(chatItem.getAnswer());
            Log.d("DateAdapter2", "Binding ChatItem: Question = " + chatItem.getQuestion() + ", Answer = " + chatItem.getAnswer());
        }

    }
    // 获取列表大小即recyclerview项的总数
    @Override
    public int getItemCount() {
        return items.size();
    }
    // 更新数据集并通知适配器
    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(List<Object> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        TextView answerTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            answerTextView = itemView.findViewById(R.id.answerTextView);
        }
    }
}

