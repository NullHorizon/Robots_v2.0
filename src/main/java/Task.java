import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by shepkan on 21.12.2016.
 */
public abstract class Task {
    Agent agOwner, recipient;
    String message;
    Status status;
    protected String type;
    protected Timer timer;
    protected int steps, time;

    public Task(){}
    protected Task(Agent own, Agent rec, String mes){
        agOwner=own;
        recipient=rec;
        message=mes;
        steps=0;
        time=0;
        timer = new javax.swing.Timer( 100, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                time=time+100;
            }
        } );
        timer.start();
        status=Status.UNSOLVED;
    }

    public String getType(){return type;}

    public void solve(){
        timer.stop();
        main.stats.addSteps(steps);
        main.stats.addTime(time);
        status=Status.SOLVED;
        main.chekTasks();
    }

    public void feedBack(Message msg){
        if (msg.getChain().size()>1) {
            ArrayList<Agent> chain = msg.getChain();
            Agent cur = chain.remove(chain.size() - 1);
            Agent next = chain.get(chain.size() - 1);
            Message m = new Message(msg.getContent(), chain, next, cur, chain.get(0), Message.MSGType.FEED_BACK);
            m.setNegative(msg.isNegative());
            cur.sendMessage(next, m);
            main.logging(m.toString());
        } else {
            if (msg.isNegative()){
                msg.getFinalTarget().setBroken(true);
            }
            int k = new Integer(msg.getContent().substring(0, msg.getContent().indexOf("_TARG")));
            main.tasks.get(k).solve();
        }
    }

    public abstract Task makeTask(Agent own, Agent rec, String mes);
    public abstract void onGetMessage(Message msg);
    public abstract void step();

    public enum Status{ UNSOLVED, SOLVED}
}
