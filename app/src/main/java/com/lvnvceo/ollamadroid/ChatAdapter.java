package com.lvnvceo.ollamadroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final List<ChatMessage> messages;
    private final Context context;

    private static final String FAVORITES_PREFS = "favorites_prefs";
    private static final String FAVORITES_KEY = "favorites_key";

    // 接收消息
    private static final int VIEW_TYPE_RECEIVED = 1;
    // 发送消息
    private static final int VIEW_TYPE_SENT = 2;

    public ChatAdapter(Context context,List<ChatMessage> messages) {
        this.messages = messages;
        this.context=context;
    }


    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if (message.isSent()) { // 根据isSent字段判断消息类型
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentViewHolder(view);
        }
    }

    ChatMessage receive_message=null;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);




        if (holder.getItemViewType() == VIEW_TYPE_RECEIVED) {
            ReceivedViewHolder receivedViewHolder = (ReceivedViewHolder) holder;
            receivedViewHolder.profileImage.setImageResource(message.getProfileImage());
            receivedViewHolder.profileName.setText(message.getProfileName());
            receivedViewHolder.messageContent.setText(message.getMessageContent());
            //System.out.println("rev:   "+message.getMessageContent());
            receive_message=message;

        } else {
            SentViewHolder sentViewHolder = (SentViewHolder) holder;
            sentViewHolder.messageContent.setText(message.getMessageContent());
            boolean favoriteStatus = message.isFavorite();
            if(favoriteStatus){
                sentViewHolder.favoriteButton.setImageResource(R.drawable.collect_succ);
            }
            else {
                sentViewHolder.favoriteButton.setImageResource(R.drawable.collect);
            }
            sentViewHolder.favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean newFavoriteStatus = !message.isFavorite();
                    message.setFavorite(newFavoriteStatus); // 更新 ChatMessage 的收藏状态
                    //System.out.println("send+  "+message);
                    // 更新图标
                    if (newFavoriteStatus) {
                        sentViewHolder.favoriteButton.setImageResource(R.drawable.collect_succ);
                        saveToFavorites(message);
                        saveToFavorites(receive_message);

                    } else {
                        sentViewHolder.favoriteButton.setImageResource(R.drawable.collect);
                        removeFromFavorites(message);
                    }

                    // 显示收藏状态的 Toast
                    String toastMessage = newFavoriteStatus ? "收藏成功" : "取消收藏";
                    Toast.makeText(v.getContext(), toastMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private void saveToFavorites(ChatMessage message) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FAVORITES_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> favorites = sharedPreferences.getStringSet(FAVORITES_KEY, new HashSet<>());
        favorites.add(message.toJson()); // Assuming ChatMessage has a toJson() method
        editor.putStringSet(FAVORITES_KEY, favorites);
        editor.apply();
    }

    private void removeFromFavorites(ChatMessage message) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FAVORITES_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> favorites = sharedPreferences.getStringSet(FAVORITES_KEY, new HashSet<>());
        favorites.remove(message.toJson()); // Assuming ChatMessage has a toJson() method
        editor.putStringSet(FAVORITES_KEY, favorites);
        editor.apply();
    }

    private List<ChatMessage> getFavorites() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("favorites_list", null);
        Type type = new TypeToken<ArrayList<ChatMessage>>() {}.getType();
        List<ChatMessage> favorites = gson.fromJson(json, type);
        if (favorites == null) {
            favorites = new ArrayList<>();
        }
        return favorites;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView profileName;
        TextView messageContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profile_name);
            messageContent = itemView.findViewById(R.id.message_content);
        }
    }


    public static class ReceivedViewHolder extends ViewHolder {
        ImageView profileImage;
        TextView profileName;
        TextView messageContent;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profile_name);
            messageContent = itemView.findViewById(R.id.message_content);
        }
    }

    public static class SentViewHolder extends ViewHolder {
        TextView messageContent;
        ImageButton favoriteButton;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.message_content);
            favoriteButton = itemView.findViewById(R.id.button_favorite);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyDataSetChanged(); // Notify adapter of dataset change
    }
    @SuppressLint("NotifyDataSetChanged")
    public void removeMessage(int pos){
        messages.remove(pos);
        notifyDataSetChanged();
    }
}
