package app.salvatop.brainstorm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import app.salvatop.brainstorm.R;
import app.salvatop.brainstorm.model.Idea;



public class CardAdapter extends RecyclerView.Adapter<CardAdapter.IdeaHolder> {
    private Context context;
    private ArrayList<Idea> ideas;

    public CardAdapter(Context context, ArrayList<Idea> ideas) {
        this.context = context;
        this.ideas = ideas;
    }

    @NonNull
    @Override
    public IdeaHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.idea_layout, parent, false);
        return new IdeaHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IdeaHolder holder, int position) {
        Idea idea = ideas.get(position);
        holder.setDetails(idea);
    }

    @Override
    public int getItemCount() {
        return ideas.size();
    }


    class IdeaHolder extends RecyclerView.ViewHolder {

        private TextView author, title, ideaContext, content, forks;
        private ImageView cover;

        IdeaHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.ideaAuthor);
            ideaContext = itemView.findViewById(R.id.ideaContext);
            content = itemView.findViewById(R.id.ideaContents);
            title = itemView.findViewById(R.id.ideaTitle);
            forks = itemView.findViewById(R.id.ideaForks);
            cover = itemView.findViewById(R.id.ideaCover);
        }

        void setDetails(Idea idea) {
            author.setText(idea.getAuthor());
            title.setText(idea.getTitle());
            content.setText(idea.getContent());
            ideaContext.setText(idea.getIdeaContext());
            //forks.setText(idea.getForks().size());

        }
    }
}