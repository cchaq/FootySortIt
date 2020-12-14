package org.mobile.android.footysortit.main;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import org.mobile.android.footysortit.R;

import java.util.ArrayList;


public class ContactPickerRecyclerViewAdapter extends RecyclerView.Adapter<ContactPickerRecyclerViewAdapter.ViewHolder>
        implements Filterable{
     private ArrayList<PlayerDetails> playerDetailsForSearch;
     private ArrayList<PlayerDetails> playerData;
     private PlayerList playerListGame = new PlayerList();
     ContactPickerInterface contactPickerInterface;


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView playerNameNumber;
        private CheckBox pickedPlayer;


        public ViewHolder(View v){
            super(v);
            playerNameNumber = v.findViewById(R.id.playerDetails);
            pickedPlayer =v.findViewById(R.id.pickContact);
//            this.setIsRecyclable(false); //This work but it is pointless with recyclerview in this situation redo this. is used to stop the checks being recyecled

        }
    }


    public ContactPickerRecyclerViewAdapter(ArrayList<PlayerDetails> playerDataFromPhoneBook, ContactPickerInterface contactPickerInterfaceOne, Activity activity){
        if(playerDataFromPhoneBook == null){
            AllContacts allContacts = new AllContacts(activity); //Not sure what i was thinking here. But activity breaks the search function somehow
            playerData = allContacts.getContacts().myPlayers;
        }
        else{
            playerData = playerDataFromPhoneBook;
        }
            playerDetailsForSearch = playerDataFromPhoneBook;
            this.contactPickerInterface = contactPickerInterfaceOne;
            //Not sure why I have two..

    }

    @Override
    public ContactPickerRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_contact_picker_scheme, parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){


        holder.playerNameNumber.setText(playerData.get(position).name + "\n" +playerData.get(position).number );


        holder.pickedPlayer.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View thisView){
                PlayerDetails contact = new PlayerDetails();
              //  int contactPos = position;
                if(holder.pickedPlayer.isChecked()) {

                    contact.number = playerData.get(position).number.replace("-","").replace(" ","");
                    contact.name = playerData.get(position).name;
                    playerListGame.addPlayer(contact);
                    String name = contact.name;
                    Toast.makeText(thisView.getContext(), name + " Added", Toast.LENGTH_SHORT).show();
                    contactPickerInterface.addTvDisplayPlayersPicked(contact.name);

                }
               else if(!holder.pickedPlayer.isChecked()){
                    contact.number = playerData.get(position).number;
                    contact.name = playerData.get(position).name;
                    /*for(PlayerDetails i : playerListGame.myPlayers){
                        if(i.name == contact.name && i.number == contact.number){
                            contactPos = playerListGame.myPlayers.indexOf(i);
                        }
                    }
                  //  playerListGame.removePlayer(contactPos);*/
                    playerListGame.removePlayerMethod(contact.number);
                    String name = contact.name;
                    Toast.makeText(thisView.getContext(), name + " Removed", Toast.LENGTH_SHORT).show();
                    contactPickerInterface.removeTvDisplayPlayersPicked(contact.name);
                }

            }

        });


    }
    @Override
    public int getItemCount(){
        return playerData.size();

    }

    public PlayerList donePickingPlayers(){
        return playerListGame;
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if(charString.isEmpty()){
                    playerData =  playerDetailsForSearch;
                }

                    else {
                    ArrayList<PlayerDetails> filteredList = new ArrayList<PlayerDetails>();

                    for (PlayerDetails row : playerDetailsForSearch) {
                        if (row.name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    playerData = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = playerData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                playerData = (ArrayList<PlayerDetails>)results.values;
                notifyDataSetChanged();

            }
        };

    }


}
