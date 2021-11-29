package com.example.maintenanceapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import models.*;

public class ConversationMenuAdapter extends RecyclerView.Adapter<ConversationMenuAdapter.ViewHolder>{

    Context context;
    List<Conversation> conversationPreviews;
    public static final String TAG = "DirectMessageAdapter";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_RECIPIENT = "recipient";
    public ConversationMenuAdapter(Context context, List<Conversation> conversationPreviews) {
        this.context = context;
        this.conversationPreviews = conversationPreviews;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.conversation_preview, parent, false);
        return new ConversationMenuAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation message = conversationPreviews.get(position);
        try {
            holder.bind(message);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return conversationPreviews.size();
    }

    public void addItem(Conversation c) {
        this.conversationPreviews.add(c);
        notifyItemInserted(conversationPreviews.size() - 1);
    }

    public void removeItem(Conversation c) {
        for (int i = 0; i < conversationPreviews.size(); i++) {
            if (conversationPreviews.get(i).getObjectId().equals(c.getObjectId())){
                conversationPreviews.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, conversationPreviews.size());
                return;
            }
        }
    }
    public void updateItem(Conversation c) {
        for (int i = 0; i < conversationPreviews.size(); i++) {
            if (conversationPreviews.get(i).getObjectId().equals(c.getObjectId())){
                conversationPreviews.set(i,c);
                notifyDataSetChanged();
                return;
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPreviewProfilePic;
        private TextView tvPreviewName;
        private TextView tvPreviewLatestMessage;
        Conversation tempConvo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPreviewProfilePic = itemView.findViewById(R.id.ivProfilePicMessagePreview);
            tvPreviewName = itemView.findViewById(R.id.tvMessagePreviewName);
            tvPreviewLatestMessage = itemView.findViewById(R.id.tvLatestMessage);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ChatActivity.class);
                    Log.i("convo adapter:", "in temp convo");
                    // Query for correct user in order to direct correct chat
                    String recipientId;
                    if (!(tempConvo.getUserOne().equals(ParseUser.getCurrentUser().getObjectId()))) {
                        recipientId =  tempConvo.getUserOne();
                    }
                    else recipientId = tempConvo.getUserTwo();

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("objectId", recipientId);
                    Log.i("Recipent", recipientId);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if(e != null){
                                Log.e("Error", e.getMessage());
                                return;
                            }
                            i.putExtra(KEY_RECIPIENT, user);
                            context.startActivity(i);
                        }
                    });

                }
            });
        }

        public void bind(Conversation convo) throws ParseException {

            tempConvo = convo;
            String recipientId;
            if (!(convo.getUserOne().equals(ParseUser.getCurrentUser().getObjectId()))) {
                recipientId =  convo.getUserOne();
            }
            else recipientId = convo.getUserTwo();

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo(KEY_OBJECT_ID, recipientId);
            query.getInBackground(recipientId, new GetCallback<ParseUser>() {
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        tvPreviewName.setText(user.getString("name"));
                        ParseFile profilePic = user.getParseFile("profilePic");
                        if (profilePic != null) {
                            Glide.with(context).load(profilePic.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivPreviewProfilePic);
                        }
                        tvPreviewLatestMessage.setText(convo.getRecentMessage());
                    }
                    else Log.i(TAG, "User's full name was not retrieved correctly");
                }
            });
        }
    }

}
