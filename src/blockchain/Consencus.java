package src.blockchain;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

// this program helps nodes vote on what the true version of a certain data is.
//
public class Consencus {
    public class Vote {
        public Object object;
        public Integer Count;

        @Override public boolean equals(Object obj)
        {

            // checking if the two objects
            // pointing to same object
            if (this == obj)
                return true;

            // checking for two condition:
            // 1) object is pointing to null
            // 2) if the objects belong to
            // same class or not
            if (obj == null
                    || this.getClass() != obj.getClass())
                return false;

            Vote v1 = (Vote)obj; // type casting object to the
            // intended class type


            // checking if the two
            // objects share all the same values
            return this.object .equals(v1.object);
        }

    }
    public Vote Vote(Object[] items){
        ArrayList<Vote> votes = new ArrayList<>();
        for(Object item: items){
            Vote vote = new Vote();
            vote.object = item;
            vote.Count = 1;
           if (hasObject(item, votes.toArray(new Vote[0]))){
               votes =new  ArrayList<>(List.of(addToVote(item, votes.toArray(new Vote[0]))));
           } else {
               votes.add(vote);
           }
        }
        
        Vote highestVote = votes.get(0);
        for (Vote vote: votes){
            System.out.print(vote.Count);
            System.out.println(vote.object);
            if (highestVote.Count < vote.Count){
                highestVote = vote;
            }
        }

        return highestVote;
    }

    public Vote[] addToVote(Object object, Vote[] votes){
        for (Vote vote: votes){
            if (vote.object.equals(object)){
                vote.Count = vote.Count + 1;
            }
        }
        return votes;
    }

    public boolean hasObject(Object object, Vote[] votes ){
        // this checks if a vote object already exists
        for (Vote vote: votes){
            if (vote.object.equals(object)){
                return true;
            }
        }
        return  false;
    }
}
