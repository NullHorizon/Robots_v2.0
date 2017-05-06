import java.awt.*;
import java.util.ArrayList;

/**
 * Created by AsmodeusX on 13.12.2016.
 */
public class Message
{
    private String content;
    private ArrayList<Agent> chain;
    private Agent target, from;
    private MSGType type;
    private Agent finalTarget;
    private boolean negative=false;

    public Message(String x, ArrayList<Agent> a, Agent t, Agent from, Agent ft){
        this.content = x;
        this.chain = a;
        this.target = t;
        this.from=from;
        if (this.chain == null)
            this.chain = new ArrayList<Agent>();
        if (type!=MSGType.FEED_BACK) {
            chain.add(from);
        }
        type=MSGType.MESSAGE;
        finalTarget=ft;
    }
    public Message(String x, ArrayList<Agent> a, Agent t, Agent from, Agent ft, MSGType type){
        this.content = x;
        this.chain = a;
        this.target = t;
        this.from=from;
        if (this.chain == null)
            this.chain = new ArrayList<Agent>();
        if (type!=MSGType.FEED_BACK) {
            chain.add(from);
        }
        this.type=type;
        finalTarget=ft;
    }
    public Message(String x, ArrayList<Agent> a, Agent t, Agent from, MSGType type){
        this.content = x;
        this.chain = a;
        this.target = t;
        this.from=from;
        if (this.chain == null)
            this.chain = new ArrayList<Agent>();
        if (type!=MSGType.FEED_BACK) {
            chain.add(from);
        }
        this.type=type;
        finalTarget=target;
    }

    public Message(String x, ArrayList<Agent> a, Agent t, Agent from)
    {
        this.content = x;
        this.chain = a;
        this.target = t;
        this.from=from;
        if (this.chain == null)
            this.chain = new ArrayList<Agent>();
        if (type!=MSGType.FEED_BACK) {
            chain.add(from);
        }
        type=MSGType.MESSAGE;
        finalTarget=target;
    }

    public void setContent(String x)
    {
        this.content = x;
    }
    public String getContent()
    {
        return this.content;
    }

    public void setTarget(Agent x)
    {
        this.target = x;
    }
    public Agent getTarget()
    {
        return this.target;
    }
    public Agent getFrom() {return this.from;}
    public Agent getFinalTarget() {return finalTarget;}
    public ArrayList<Agent> getChain()
    {
        return this.chain;
    }

    public boolean addToChain(Agent a)
    {
        if (this.chain.indexOf(a) != -1)
        {
            this.chain.add(a);
            return true;
        }
        return false;
    }

    public boolean removeFromChain(Agent a)
    {
        if (this.chain.indexOf(a) != -1)
        {
            this.chain.remove(this.chain.indexOf(a));
            return true;
        }
        return false;
    }

    public String toString()
    {
        String chainStr = "";
        for(int i = 0; i < this.chain.size(); i++)
            chainStr += this.chain.get(i).getId();
        return chainStr + " " + this.content;
    }

    public MSGType getType(){ return type;}

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public void setType(MSGType type) {
        this.type = type;
    }

    public enum MSGType{
        ANSWER, CONNECTION, MESSAGE, TARGET_MESSAGE, SEARCH, FINDED, SEARCH_FALL, FEED_BACK
    }
}
