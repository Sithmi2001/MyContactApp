package com.example.seu_is_20_ict_046_assignment02;

import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private Cursor cursor;
    private OnContactClickListener listener;

    public ContactAdapter(Cursor cursor, OnContactClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            int contactId = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_ID));
            String name = cursor.getString(cursor.getColumnIndex(DBHelper.COL_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(DBHelper.COL_PHONE));
            String imageUriString = cursor.getString(cursor.getColumnIndex(DBHelper.COL_IMAGE));

            holder.nameText.setText(name);
            holder.phoneText.setText(phone);

            if (imageUriString != null && !imageUriString.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(imageUriString);
                    holder.contactImage.setImageURI(imageUri);
                    holder.contactImage.setVisibility(View.VISIBLE);
                    holder.letterText.setVisibility(View.GONE);
                } catch (Exception e) {
                    holder.contactImage.setVisibility(View.GONE);
                    holder.letterText.setVisibility(View.VISIBLE);
                    setLetterFallback(holder, name);
                }
            } else {
                holder.contactImage.setVisibility(View.GONE);
                holder.letterText.setVisibility(View.VISIBLE);
                setLetterFallback(holder, name);
            }

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onContactClick(contactId);
            });
        }
    }

    private void setLetterFallback(ContactViewHolder holder, String name) {
        if (name != null && !name.isEmpty()) {
            char firstChar = Character.toUpperCase(name.charAt(0));
            holder.letterText.setText(String.valueOf(firstChar));
            holder.letterText.getBackground().setTint(getColorForLetter(firstChar));
        } else {
            holder.letterText.setText("?");
            holder.letterText.getBackground().setTint(0xFF9E9E9E);
        }
    }

    private int getColorForLetter(char letter) {
        switch (letter) {
            case 'A': case 'B': return 0xFFE57373;
            case 'C': case 'D': return 0xFF64B5F6;
            case 'E': case 'F': return 0xFF81C784;
            case 'G': case 'H': return 0xFFFFB74D;
            case 'I': case 'J': return 0xFFBA68C8;
            case 'K': case 'L': return 0xFFFF8A65;
            case 'M': case 'N': return 0xFF4DB6AC;
            case 'O': case 'P': return 0xFFA1887F;
            case 'Q': case 'R': return 0xFF7986CB;
            case 'S': case 'T': return 0xFFDCE775;
            case 'U': case 'V': return 0xFF9575CD;
            case 'W': case 'X': return 0xFFFFF176;
            case 'Y': case 'Z': return 0xFF90CAF9;
            default: return 0xFF9E9E9E;
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) cursor.close();
        cursor = newCursor;
        notifyDataSetChanged();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, phoneText, letterText;
        ImageView contactImage;

        public ContactViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameTextView);
            phoneText = itemView.findViewById(R.id.phoneTextView);
            letterText = itemView.findViewById(R.id.letterTextView);
            contactImage = itemView.findViewById(R.id.contactImageView);
        }
    }

    public interface OnContactClickListener {
        void onContactClick(int contactId);
    }
}

