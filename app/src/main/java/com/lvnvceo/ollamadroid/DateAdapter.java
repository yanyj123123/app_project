package com.lvnvceo.ollamadroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lvnvceo.ollamadroid.ollama.ChatItem;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_CHAT_ITEM = 1;

    private List<ChatItem> items;

    public DateAdapter(List<ChatItem> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof ChatItem ? VIEW_TYPE_DATE : VIEW_TYPE_CHAT_ITEM;
    }

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
            ChatItem chatItem = (ChatItem) items.get(position);
            ((DateViewHolder) holder).dateTextView.setText(chatItem.getDate());
        } else if (holder instanceof ChatViewHolder) {
            ChatItem chatItem = (ChatItem) items.get(position);
            ((ChatViewHolder) holder).questionTextView.setText(chatItem.getInput());
            ((ChatViewHolder) holder).answerTextView.setText(chatItem.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
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
